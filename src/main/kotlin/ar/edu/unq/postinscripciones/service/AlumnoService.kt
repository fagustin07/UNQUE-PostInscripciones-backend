package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.helpers.ChequeadorDeMateriasDisponibles
import ar.edu.unq.postinscripciones.model.*
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.persistence.*
import ar.edu.unq.postinscripciones.service.dto.alumno.*
import ar.edu.unq.postinscripciones.service.dto.comision.ComisionParaAlumno
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioAlumnoDTO
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioDirectorDTO
import ar.edu.unq.postinscripciones.service.dto.formulario.SolicitudSobrecupoDTO
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaComision
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaCursadaResumenDTO
import ar.edu.unq.postinscripciones.webservice.config.security.JWTTokenUtil
import io.swagger.annotations.ApiModelProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime
import javax.persistence.Tuple
import javax.transaction.Transactional

@Service
class AlumnoService {

    @Autowired
    private lateinit var alumnoRepository: AlumnoRepository

    @Autowired
    private lateinit var materiaRepository: MateriaRepository

    @Autowired
    private lateinit var formularioRepository: FormularioRepository

    @Autowired
    private lateinit var comisionRepository: ComisionRespository

    @Autowired
    private lateinit var cuatrimestreRepository: CuatrimestreRepository

    @Autowired
    private lateinit var solicitudSobrecupoRepository: SolicitudSobrecupoRepository

    @Autowired
    private lateinit var jwtTokenUtil: JWTTokenUtil

    @Autowired
    private lateinit var chequeadorDeMateriaDisponible: ChequeadorDeMateriasDisponibles

    @Transactional
    fun registrarAlumnos(planillaAlumnos: List<FormularioCrearAlumno>): List<ConflictoAlumno> {
        val conflictos: MutableList<ConflictoAlumno> = mutableListOf()
        planillaAlumnos.forEach { formulario ->
            val alumnoExistente = alumnoRepository.findByDniOrLegajo(formulario.dni, formulario.legajo)
            if (alumnoExistente.isPresent) {
                val mensaje = "Conflicto con el alumno con dni ${alumnoExistente.get().dni} " +
                        "y legajo ${alumnoExistente.get().legajo}"
                conflictos.add(ConflictoAlumno(formulario.dni, formulario.legajo, mensaje))
            } else {
                guardarAlumno(formulario)
            }
        }
        return conflictos
    }

    @Transactional
    fun subirHistoriaAcademica(alumnosMateriaCursada: List<AlumnoMateriaCursada>): List<ConflictoHistoriaAcademica> {
        alumnosMateriaCursada.forEach { alumnoMateriaCursada ->
            val alumno = alumnoRepository.findById(alumnoMateriaCursada.dni).get()
            val materia = materiaRepository.findMateriaByCodigo(alumnoMateriaCursada.codigo).get()
            alumno.agregarMateriaCursada(materia, alumnoMateriaCursada.fecha, alumnoMateriaCursada.resultado)
            alumnoRepository.save(alumno)
        }
        return emptyList()
    }

    @Transactional
    fun actualizarHistoriaAcademica(alumnosConHistoriaAcademica: List<AlumnoConHistoriaAcademica>): MutableList<ConflictoHistoriaAcademica> {
        val conflictoHistoriaAcademicaAlumnos: MutableList<ConflictoHistoriaAcademica> = mutableListOf()
        alumnosConHistoriaAcademica.forEach { alumnoConHistoriaAcademica ->
            val existeAlumno = alumnoRepository.findById(alumnoConHistoriaAcademica.dni)
            if (existeAlumno.isPresent) {
                val alumno = existeAlumno.get()
                val historiaAcademica: MutableList<MateriaCursada> = mutableListOf()
                    alumnoConHistoriaAcademica.materiasCursadas.forEach {
                    val existeMateria = materiaRepository.findMateriaByCodigo(it.codigoMateria)
                    if (existeMateria.isPresent) {
                        historiaAcademica.add(MateriaCursada(existeMateria.get(), it.estado, it.fechaDeCarga))
                    } else {
                        conflictoHistoriaAcademicaAlumnos.add(ConflictoHistoriaAcademica(alumno.dni, it.codigoMateria, "No existe la materia"))
                    }
                }
                if (historiaAcademica.isEmpty()) {
                    conflictoHistoriaAcademicaAlumnos
                        .add(ConflictoHistoriaAcademica(alumno.dni, "-", "No se modificó " +
                                "la historia academica ya que se presentó una lista con materias inválidas que " +
                                "hizo que quede vacía"))
                } else {
                    alumno.actualizarHistoriaAcademica(historiaAcademica)
                    alumnoRepository.save(alumno)
                }
            } else {
                conflictoHistoriaAcademicaAlumnos.add(ConflictoHistoriaAcademica(alumnoConHistoriaAcademica.dni, "-", "No existe el alumno"))
            }

        }

        return conflictoHistoriaAcademicaAlumnos
    }

    @Transactional
    fun guardarSolicitudPara(
        dni: Int,
        idComisiones: List<Long>,
        cuatrimestre: Cuatrimestre = Cuatrimestre.actual(),
        fechaCarga: LocalDateTime = LocalDateTime.now(),
        comisionesInscriptoIds: List<Long> = listOf()
    ): FormularioAlumnoDTO {
        val alumno = alumnoRepository.findById(dni).get()
        val formulario = crearFormulario(cuatrimestre, alumno, idComisiones, comisionesInscriptoIds, fechaCarga)
        alumno.guardarFormulario(formulario)
        alumnoRepository.save(alumno)

        return FormularioAlumnoDTO.desdeModeloCerrado(formulario, alumno.dni)
    }

    @Transactional
    fun actualizarFormulario(
        dni: Int,
        idComisiones: List<Long>,
        cuatrimestre: Cuatrimestre = Cuatrimestre.actual(),
        fechaCarga: LocalDateTime = LocalDateTime.now(),
        comisionesInscriptoIds: List<Long> = listOf()
    ): FormularioAlumnoDTO {
        val alumno = alumnoRepository.findById(dni).get()
        val formulario = crearFormulario(cuatrimestre, alumno, idComisiones, comisionesInscriptoIds, fechaCarga)
        alumno.cambiarFormulario(cuatrimestre.anio, cuatrimestre.semestre, formulario)
        alumnoRepository.save(alumno)

        return FormularioAlumnoDTO.desdeModeloCerrado(formulario, alumno.dni)
    }

    @Transactional
    fun crear(formulario: FormularioCrearAlumno): Alumno {
        return this.guardarAlumno(formulario)
    }

    @Transactional
    fun todos(patronDni: String = ""): List<AlumnoDTO> {
        return alumnoRepository.findByDniStartsWithOrderByCantAprobadasDesc(patronDni).map { AlumnoDTO.desdeModelo(it) }
    }

    @Transactional
    fun obtenerFormulario(token: String, cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): FormularioAlumnoDTO {
        val cuatrimestreObtenido =
            cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
                .orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        val dni = jwtTokenUtil.obtenerDni(token)
        val alumno = alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("No existe el alumno") }

        val formulario = alumno.obtenerFormulario(
            cuatrimestreObtenido.anio,
            cuatrimestreObtenido.semestre
        )

        return if (formulario.estado === EstadoFormulario.CERRADO) {
            FormularioAlumnoDTO.desdeModelo(formulario, dni)
        } else {
            FormularioAlumnoDTO.desdeModeloCerrado(formulario, dni)
        }
    }

    @Transactional
    fun borrarAlumno(dni: Int) {
        alumnoRepository.deleteById(dni)
    }

    @Transactional
    fun cambiarEstadoSolicitud(
        solicitudId: Long,
        estado: EstadoSolicitud,
        formularioId: Long,
        fecha: LocalDateTime = LocalDateTime.now()
    ): SolicitudSobrecupoDTO {
        val solicitud =
            solicitudSobrecupoRepository.findById(solicitudId).orElseThrow { ExcepcionUNQUE("No existe la solicitud") }
        val formulario = formularioRepository.findById(formularioId).get()

        chequearEstado(formulario, fecha)
        val materia = solicitud.comision.materia
        if(estado == EstadoSolicitud.APROBADO && formulario.tieneAprobadaAlgunaDe(materia)) {
            throw ExcepcionUNQUE("El alumno ya tiene una comision aprobada de la materia ${materia.nombre}")
        }
        solicitud.cambiarEstado(estado)
        comisionRepository.save(solicitud.comision)
        return SolicitudSobrecupoDTO.desdeModelo(solicitudSobrecupoRepository.save(solicitud))
    }

    @Transactional
    fun cerrarFormulario(formularioId: Long, alumnoDni: Int): FormularioDirectorDTO {
        val formulario =
            formularioRepository.findById(formularioId).orElseThrow { ExcepcionUNQUE("No existe el formulario") }
        formulario.cerrarFormulario()
        return FormularioDirectorDTO.desdeModelo(formularioRepository.save(formulario), alumnoDni)
    }

    @Transactional
    fun cerrarFormularios(fecha: LocalDateTime = LocalDateTime.now()) {
        val cuatrimestre = Cuatrimestre.actual()
        val cuatrimestreObtenido = cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre).orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        val alumnos = alumnoRepository.findAll()

        if (cuatrimestreObtenido.finInscripciones > fecha) {
            throw ExcepcionUNQUE("No se puede cerrar los formularios aun, la fecha de inscripciones no ha concluido")
        }

        alumnos.forEach {
            if(it.yaGuardoUnFormulario(cuatrimestreObtenido)) {
                val formulario = it.obtenerFormulario(cuatrimestreObtenido.anio, cuatrimestreObtenido.semestre)
                chequearEstado(formulario, fecha)
                formulario.cerrarFormulario()
            }
        }
    }

    @Transactional
    fun materiasDisponibles(dni: Int, cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): List<MateriaComision> {
        val cuatrimestreObtenido =
            cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
                .orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        val alumno =
            alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("No existe el alumno") }

        val comisionesOfertadas = comisionRepository.findByCuatrimestre(cuatrimestreObtenido)
        val codigos: List<String> = comisionesOfertadas.map { it.materia }.groupBy { it.codigo }.map { it.key }
        val materiasOfertadas: List<Materia> = materiaRepository.findAllByCodigoIn(codigos)

        val materiasQuePuedeCursar = chequeadorDeMateriaDisponible.materiasQuePuedeCursar(alumno, materiasOfertadas)

        return materiasQuePuedeCursar.map { materia ->
            val comisiones = comisionesOfertadas.filter { it.materia.esLaMateria(materia) }

            MateriaComision(
                materia.codigo,
                materia.nombre,
                comisiones.map { ComisionParaAlumno.desdeModelo(it) }.toMutableList()
            )
        }
    }

    @Transactional
    fun obtenerResumenAlumno(dni: Int): ResumenAlumno {
        val cuatrimestreObtenido = Cuatrimestre.actual()
        val alumno = alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("El Alumno no existe") }
        val materiasCursadas = alumnoRepository.findResumenHistoriaAcademica(dni)
            .map {
                val materia = materiaRepository.findMateriaByCodigo(it.get(0) as String)
                    .orElseThrow { ExcepcionUNQUE("Materia no encontrada") }
                val fecha = (it.get(2) as java.sql.Date).toLocalDate()
                val estado = EstadoMateria.desdeString(it.get(1) as String)
                val intentos = (it.get(3) as BigInteger).toInt()

                MateriaCursadaResumenDTO(materia.nombre, materia.codigo, estado, fecha, intentos)
            }

        return ResumenAlumno(
            alumno.nombre,
            alumno.dni,
            alumno.legajo,
            alumno.carrera,
            alumno.coeficiente,
            FormularioDirectorDTO.desdeModelo(
                alumno.obtenerFormulario(
                    cuatrimestreObtenido.anio,
                    cuatrimestreObtenido.semestre
                ), alumno.dni
            ),
            materiasCursadas
        )
    }

    @Transactional
    fun alumnosQueSolicitaronComision(idComision: Long): List<AlumnoSolicitaComision> {
        comisionRepository.findById(idComision).orElseThrow { ExcepcionUNQUE("No existe la comision") }
        val alumnos = alumnoRepository.findBySolicitaComisionIdOrderByCantidadAprobadas(idComision)

        return alumnos.map { AlumnoSolicitaComision.desdeTupla(it) }
    }

    @Transactional
    fun alumnosQueSolicitaronMateria(
        codigo: String,
        numeroComision: Int? = null,
        pendiente: Boolean? = null,
        cuatrimestre: Cuatrimestre = Cuatrimestre.actual()
    ): List<AlumnoSolicitaMateria> {
        val materia = materiaRepository.findById(codigo).orElseThrow { ExcepcionUNQUE("No existe la materia") }
        val cuatrimestreObtenido = cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
            .orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        if (numeroComision != null) {
            comisionRepository.findByNumeroAndMateriaAndCuatrimestre(numeroComision, materia, cuatrimestreObtenido).orElseThrow { ExcepcionUNQUE("No existe la comision") }
        }

        val alumnos: List<Tuple> =
            alumnoRepository.findBySolicitaMateriaAndComisionMOrderByCantidadAprobadas(
                codigo,
                numeroComision,
                cuatrimestre.semestre,
                cuatrimestre.anio,
                pendiente
            )

        return alumnos.map { AlumnoSolicitaMateria.desdeTupla(it) }
    }

    @Transactional
    fun agregarSolicitud(
        dni: Int,
        idComision: Long,
        cuatrimestre: Cuatrimestre = Cuatrimestre.actual(),
        fechaCarga: LocalDateTime = LocalDateTime.now()
    ): FormularioDirectorDTO {
        val cuatrimestreObtenido =
            cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
                .orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        val alumno = alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("No existe el alumno") }
        val comision = comisionRepository.findById(idComision).get()

        val formulario = alumno.agregarSolicitud(comision, cuatrimestreObtenido)

        alumnoRepository.save(alumno)
        return FormularioDirectorDTO.desdeModelo(formulario, alumno.dni)

    }

    @Transactional
    fun buscarAlumno(dni: Int): Alumno {
        return alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("No existe el alumno") }
    }

    @Transactional
    fun alumnosPorDni(
        dni: Int? = null,
        sinProcesar: Boolean? = null,
        pendiente: Boolean? = null,
        cuatrimestre: Cuatrimestre = Cuatrimestre.actual()
    ): List<AlumnoFormulario> {
        val cuatrimestreObtenido =
            cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
                .orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        val alumnos = alumnoRepository.findAllByDni(
            dni?.toString(),
            cuatrimestreObtenido.semestre,
            cuatrimestreObtenido.anio,
            sinProcesar,
            pendiente
        )
        return alumnos.map { AlumnoFormulario.fromTuple(it) }
    }

    @Transactional
    fun rechazarSolicitudesPendientesMateria(
            codigo: String,
            numeroComision: Int? = null,
            cuatrimestre: Cuatrimestre = Cuatrimestre.actual(),
            fecha: LocalDateTime = LocalDateTime.now()
    ) {
        val solicitudes = solicitudSobrecupoRepository.findByMateria(codigo, numeroComision)
        solicitudes.forEach {
            val solicitudId = (it.get(0) as BigInteger).toLong()
            val formularioId = (it.get(1) as BigInteger).toLong()
            cambiarEstadoSolicitud(solicitudId, EstadoSolicitud.RECHAZADO, formularioId, fecha)
        }
    }

    @Transactional
    fun borrarFormulario(jwt: String, fecha: LocalDateTime = LocalDateTime.now(), cuatrimestre: Cuatrimestre = Cuatrimestre.actual()) {
        val alumnoDni = jwtTokenUtil.obtenerDni(jwt)
        val alumno = alumnoRepository.findById(alumnoDni).orElseThrow { ExcepcionUNQUE("No existe el alumno") }
        val cuatrimestrePersistido = cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
                .orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        if(cuatrimestrePersistido.finInscripciones < fecha || cuatrimestrePersistido.inicioInscripciones > fecha) {
            throw ExcepcionUNQUE("No se puede borrar el formulario, la fecha de inscripcion ha concluido o aun no ha comenzado")
        }
        alumno.borrarFormulario(cuatrimestrePersistido.anio, cuatrimestrePersistido.semestre)
        alumnoRepository.save(alumno)
    }

    @Transactional
    fun agregarComentario(
            formularioId: Long,
            dni: Int,
            autor:String,
            descripcion: String,
            cuatrimestre: Cuatrimestre = Cuatrimestre.actual(),
            fechaCarga: LocalDateTime = LocalDateTime.now()
    ): FormularioDirectorDTO {
        val formulario = formularioRepository.findById(formularioId).get()
        formulario.agregarComentarios(descripcion, autor, fechaCarga)

        return FormularioDirectorDTO.desdeModelo(formularioRepository.save(formulario), dni)
    }

    @Transactional
    fun modificarCoeficienteAlumno(coeficienteAlumnos: List<CoeficienteAlumnoDTO>): List<ConflictoAlumnoCoeficiente> {
        val conflictivos = mutableListOf<ConflictoAlumnoCoeficiente>()
        coeficienteAlumnos.forEach {
            try{
                val alumno = alumnoRepository.findById(it.alumnoDni).orElseThrow { ExcepcionUNQUE("No se encontro el alumno con dni ${it.alumnoDni}") }
                alumno.coeficiente = it.coeficienteAlumno
                alumnoRepository.save(alumno)
            } catch (excepcion: ExcepcionUNQUE) {
                conflictivos.add(ConflictoAlumnoCoeficiente(it.alumnoDni, excepcion.message))
            }
        }

        return conflictivos
    }

    fun crearFormulario(
        cuatrimestre: Cuatrimestre,
        alumno: Alumno,
        idComisiones: List<Long>,
        comisionesInscriptoIds: List<Long>,
        fechaCarga: LocalDateTime
    ): Formulario {
        val cuatrimestreObtenido =
            cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
                .orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        this.checkFecha(cuatrimestreObtenido.inicioInscripciones, cuatrimestreObtenido.finInscripciones, fechaCarga)

        val solicitudes = chequearSiPuedeCursarYObtenerSolicitudes(alumno, cuatrimestre, idComisiones)
        val comisionesInscripto = comisionesInscriptoIds.map {
            val comision = comisionRepository.findById(it).orElseThrow {
                ExcepcionUNQUE("La comision no existe")
            }
            if (alumno.haAprobado(comision.materia)) {
                throw ExcepcionUNQUE("Ya has aprobado ${comision.materia.nombre}")
            }else {
                comision
            }
        }

        return formularioRepository.save(Formulario(cuatrimestreObtenido, solicitudes, comisionesInscripto.toMutableList()))
    }

    private fun guardarAlumno(formulario: FormularioCrearAlumno): Alumno {
        val alumno = Alumno(
            formulario.dni,
            formulario.nombre,
            formulario.apellido,
            formulario.correo,
            formulario.legajo,
            "",
            formulario.carrera,
            formulario.coeficiente
        )

        return alumnoRepository.save(alumno)
    }

    private fun chequearSiPuedeCursarYObtenerSolicitudes(
        alumno: Alumno,
        cuatrimestre: Cuatrimestre,
        idComisiones: List<Long>
    ): MutableList<SolicitudSobrecupo> {
        val solicitudes = idComisiones.map { idComision ->
            val comision = comisionRepository.findById(idComision)
            SolicitudSobrecupo(comision.get())
        }.toMutableList()
        val materiasDisponibles = this.materiasDisponibles(alumno.dni, cuatrimestre)
        this.checkPuedeCursar(alumno, solicitudes, materiasDisponibles)

        return solicitudes
    }

    private fun chequearEstado(formulario: Formulario, fecha: LocalDateTime) {
        val cuatrimestreObtenido = cuatrimestreRepository
            .findByAnioAndSemestre(formulario.cuatrimestre.anio, formulario.cuatrimestre.semestre)
            .get()

        if (cuatrimestreObtenido.finInscripciones > fecha && formulario.estado != EstadoFormulario.CERRADO) {
            throw ExcepcionUNQUE("No se puede cambiar el estado de esta solicitud, la fecha de inscripciones no ha concluido")
        }
        if (formulario.estado == EstadoFormulario.CERRADO) {
            throw ExcepcionUNQUE("No se puede cambiar el estado de esta solicitud, el formulario al que pertenece se encuentra cerrado")
        }
    }

    private fun checkFecha(
        inicioInscripciones: LocalDateTime,
        finInscripciones: LocalDateTime,
        fechaCargaFormulario: LocalDateTime
    ) {
        if (inicioInscripciones > fechaCargaFormulario) {
            throw ExcepcionUNQUE("El periodo para enviar solicitudes de sobrecupos no ha empezado.")
        }
        if (finInscripciones < fechaCargaFormulario) {
            throw ExcepcionUNQUE("El periodo para enviar solicitudes de sobrecupos ya ha pasado.")
        }
    }

    private fun checkPuedeCursar(
        alumno: Alumno,
        solicitudesPorMateria: List<SolicitudSobrecupo>,
        materiasDisponibles: List<MateriaComision>
    ) {
        if (!alumno.puedeCursar(
                solicitudesPorMateria.map { it.comision.materia },
                materiasDisponibles.map { it.codigo })
        ) throw ExcepcionUNQUE("El alumno no puede cursar las materias solicitadas")
    }

    @Transactional
    fun borrarTodos() {
        alumnoRepository.findAll().forEach { alumnoRepository.deleteById(it.dni) }
    }
}

data class ConflictoAlumno(
    @ApiModelProperty(example = "12345678")
    val dni: Int,
    @ApiModelProperty(example = "45965")
    val legajo: Int,
    @ApiModelProperty(example = "hay conflicto con el alumno ... y legajo ...")
    val mensaje: String
)

data class ConflictoHistoriaAcademica(
    @ApiModelProperty(example = "12345678")
    val dni: Int,
    @ApiModelProperty(example = "231321")
    val materia: String,
    @ApiModelProperty(example = "Materia no encontrada")
    val mensaje: String
)

data class CoeficienteAlumnoDTO(
        val alumnoDni: Int,
        val coeficienteAlumno: Double
)

data class ConflictoAlumnoCoeficiente(
        @ApiModelProperty(example = "12345678")
        val dni: Int,
        @ApiModelProperty(example = "hay conflicto con el alumno ... y legajo ...")
        val mensaje: String?
)

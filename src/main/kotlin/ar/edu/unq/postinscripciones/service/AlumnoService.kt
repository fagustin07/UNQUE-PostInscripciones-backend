package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.*
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.persistence.*
import ar.edu.unq.postinscripciones.service.dto.alumno.*
import ar.edu.unq.postinscripciones.service.dto.comision.ComisionParaAlumno
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioDTO
import ar.edu.unq.postinscripciones.service.dto.formulario.SolicitudSobrecupoDTO
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaComision
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaCursadaResumenDTO
import ar.edu.unq.postinscripciones.webservice.config.security.JWTTokenUtil
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
    lateinit var jwtTokenUtil: JWTTokenUtil

    @Transactional
    fun registrarAlumnos(planillaAlumnos: List<FormularioCrearAlumno>): List<ConflictoAlumnoDTO> {
        val alumnosConflictivos: MutableList<ConflictoAlumnoDTO> = mutableListOf()

        planillaAlumnos.forEach { formulario ->
            val alumnoExistente = alumnoRepository.findByDniOrLegajo(formulario.dni, formulario.legajo)
            if (alumnoExistente.isPresent) {
                alumnosConflictivos.add(ConflictoAlumnoDTO(AlumnoDTO.desdeModelo(alumnoExistente.get()), formulario))
            } else {
                guardarAlumno(formulario)
            }
        }
        return alumnosConflictivos.toList()
    }

    @Transactional
    fun actualizarHistoriaAcademica(alumnosConHistoriaAcademica: List<AlumnoConHistoriaAcademica>): List<AlumnoDTO> {
        return alumnosConHistoriaAcademica.map { alumnoConHistoriaAcademica ->
            val alumno = alumnoRepository.findById(alumnoConHistoriaAcademica.dni)
                .orElseThrow { ExcepcionUNQUE("No se encontro al alumno") }

            val historiaAcademica: List<MateriaCursada> = alumnoConHistoriaAcademica.materiasCursadas.map {
                val materia = materiaRepository
                    .findMateriaByCodigo(it.codigoMateria).orElseThrow { ExcepcionUNQUE("No existe la materia") }
                val materiaCursada = MateriaCursada(materia)
                materiaCursada.estado = it.estado
                materiaCursada.fechaDeCarga = it.fechaDeCarga
                materiaCursada
            }

            alumno.actualizarHistoriaAcademica(historiaAcademica)

            AlumnoDTO.desdeModelo(alumnoRepository.save(alumno))
        }
    }

    @Transactional
    fun guardarSolicitudPara(
        dni: Int,
        idComisiones: List<Long>,
        cuatrimestre: Cuatrimestre = Cuatrimestre.actual(),
        fechaCarga: LocalDateTime = LocalDateTime.now(),
        comisionesInscriptoIds: List<Long> = listOf()
    ): FormularioDTO {
        val alumno = alumnoRepository.findById(dni).get()
        val formulario = crearFormulario(cuatrimestre, alumno, idComisiones, comisionesInscriptoIds, fechaCarga)
        alumno.guardarFormulario(formulario)
        alumnoRepository.save(alumno)

        return FormularioDTO.desdeModeloParaAlumno(formulario, alumno.dni)
    }

    @Transactional
    fun actualizarFormulario(
        dni: Int,
        idComisiones: List<Long>,
        cuatrimestre: Cuatrimestre = Cuatrimestre.actual(),
        fechaCarga: LocalDateTime = LocalDateTime.now(),
        comisionesInscriptoIds: List<Long> = listOf()
    ): FormularioDTO {
        val alumno = alumnoRepository.findById(dni).get()
        val formulario = crearFormulario(cuatrimestre, alumno, idComisiones, comisionesInscriptoIds, fechaCarga)
        alumno.cambiarFormulario(cuatrimestre.anio, cuatrimestre.semestre, formulario)
        alumnoRepository.save(alumno)

        return FormularioDTO.desdeModeloParaAlumno(formulario, alumno.dni)
    }

    @Transactional
    fun crear(formulario: FormularioCrearAlumno): Alumno {
        return this.guardarAlumno(formulario)
    }

    @Transactional
    fun todos(patronDni: String = ""): List<AlumnoDTO> {
        return alumnoRepository.findByDniStartsWithOrderByCoeficienteDesc(patronDni).map { AlumnoDTO.desdeModelo(it) }
    }

    @Transactional
    fun obtenerFormulario(token: String, cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): FormularioDTO {
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
            FormularioDTO.desdeModelo(formulario, dni)
        } else {
            FormularioDTO.desdeModeloParaAlumno(formulario, dni)
        }

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
        return SolicitudSobrecupoDTO.desdeModelo(solicitudSobrecupoRepository.save(solicitud))
    }

    @Transactional
    fun cerrarFormulario(formularioId: Long, alumnoDni: Int, comentarios: String = ""): FormularioDTO {
        val formulario =
            formularioRepository.findById(formularioId).orElseThrow { ExcepcionUNQUE("No existe el formulario") }
        formulario.cerrarFormulario()
        formulario.agregarComentarios(comentarios)
        return FormularioDTO.desdeModelo(formularioRepository.save(formulario), alumnoDni)
    }

    @Transactional
    fun cerrarFormularios(fecha: LocalDateTime = LocalDateTime.now(), comentarioGeneral: String = "") {
        val cuatrimestreObtenido = Cuatrimestre.actual()
        val alumnos = alumnoRepository.findAll()

        if (cuatrimestreObtenido.finInscripciones > fecha) {
            throw ExcepcionUNQUE("No se puede cerrar los formularios aun, la fecha de inscripciones no ha concluido")
        }

        alumnos.forEach {
            val formulario = it.obtenerFormulario(cuatrimestreObtenido.anio, cuatrimestreObtenido.semestre)
            chequearEstado(formulario, fecha)
            formulario.agregarComentarios(comentarioGeneral)
            formulario.cerrarFormulario()
        }
    }

    @Transactional
    fun materiasDisponibles(dni: Int, cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): List<MateriaComision> {
        val cuatrimestreObtenido =
            cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
                .orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        val alumno =
            alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("No existe el alumno") }
        val materiasDisponibles = materiaRepository.findMateriasDisponibles(
            alumno.materiasAprobadas(),
            alumno.carrera,
            cuatrimestreObtenido.anio,
            cuatrimestreObtenido.semestre
        )

        return this.mapToMateriaComision(materiasDisponibles)
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
            FormularioDTO.desdeModelo(
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
    ): FormularioDTO {
        val cuatrimestreObtenido =
            cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
                .orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        val alumno = alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("No existe el alumno") }
        val comision = comisionRepository.findById(idComision).get()

        val formulario = alumno.agregarSolicitud(comision, cuatrimestreObtenido)

        alumnoRepository.save(alumno)
        return FormularioDTO.desdeModelo(formulario, alumno.dni)

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
            throw ExcepcionUNQUE("No se puede borrar el formulario, la fecha de inscripciones ha concluido o aun no a comenzado")
        }
        alumno.borrarFormulario(cuatrimestrePersistido.anio, cuatrimestrePersistido.semestre)
        alumnoRepository.save(alumno)
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
            comisionRepository.findById(it).orElseThrow {
                ExcepcionUNQUE("La comision no existe")
            }
        }

        return formularioRepository.save(Formulario(cuatrimestreObtenido, solicitudes, comisionesInscripto))
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

    private fun mapToMateriaComision(materiasDisponibles: List<Tuple>): List<MateriaComision> {
        val materias = mutableListOf<MateriaComision>()
        materiasDisponibles.map {
            val materiaActual = materias.find { mat -> mat.codigo == (it.get(0) as String) }
            materiaActual?.comisiones?.add(ComisionParaAlumno.desdeModelo(it.get(2) as Comision))
                ?: materias.add(
                    MateriaComision(
                        it.get(0) as String,
                        it.get(1) as String,
                        mutableListOf(ComisionParaAlumno.desdeModelo(it.get(2) as Comision))
                    )
                )
        }
        return materias
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
}

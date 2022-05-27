package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.*
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.comision.Dia
import ar.edu.unq.postinscripciones.model.comision.Modalidad
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime

@IntegrationTest
internal class AlumnoServiceTest {

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @Autowired
    private lateinit var cuatrimestreService: CuatrimestreService

    @Autowired
    private lateinit var comisionService: ComisionService

    @Autowired
    private lateinit var materiaService: MateriaService

    @Autowired
    private lateinit var dataService: DataService

    private lateinit var alumno: Alumno
    private lateinit var fede: Alumno
    private lateinit var cuatrimestre: Cuatrimestre
    private lateinit var comision1Algoritmos: Comision
    private lateinit var algo: MateriaDTO
    private lateinit var funcional: MateriaDTO

    @BeforeEach
    fun setUp() {
        val nicoFormularioCrear = FormularioCrearAlumno(
            45328,
            "Nicolas",
            "Martinez",
            "nicolas.martinez@unq.edu.ar",
            42256394,
            "42256395",
            Carrera.TPI,
            listOf()
        )

        val fedeFormularioCrear = FormularioCrearAlumno(
            45329,
            "Fede",
            "Sandoval",
            "fede.sando@unq.edu.ar",
            11223344,
            "1234",
            Carrera.TPI,
            listOf()
        )

        alumno = alumnoService.crear(nicoFormularioCrear)
        fede = alumnoService.crear(fedeFormularioCrear)
        algo = materiaService.crear("Algoritmos", "ALG-208", mutableListOf(), Carrera.SIMULTANEIDAD)
        funcional = materiaService.crear("Funcional", "FUN-205", mutableListOf(), Carrera.SIMULTANEIDAD)
        val formularioCuatrimestre = FormularioCuatrimestre(2022, Semestre.S1)
        cuatrimestre = cuatrimestreService.crear(formularioCuatrimestre)
        val horarios = listOf(
            HorarioDTO(Dia.LUNES, "18:30", "21:30"),
            HorarioDTO(Dia.JUEVES, "18:30", "21:30")
        )

        val formularioComision = FormularioComision(
            1,
            algo.codigo,
            2022,
            Semestre.S1,
            35,
            5,
            horarios,
            Modalidad.PRESENCIAL
        )
        comision1Algoritmos = comisionService.crear(formularioComision)
    }

    @Test
    fun `Se puede crear un alumno`() {
        assertThat(alumno).isNotNull
    }

    @Test
    fun `Un alumno registra un formulario de solicitud de cupo`() {
        val formulario =
            alumnoService.guardarSolicitudPara(
                alumno.dni,
                listOf(comision1Algoritmos.id!!),
                cuatrimestre
            )
        val comisionesDeSolicitudes = formulario.solicitudes.map { it.comision.id }

        assertThat(comisionesDeSolicitudes).contains(ComisionDTO.desdeModelo(comision1Algoritmos).id)
    }

    @Test
    fun `Un alumno no puede registrar dos formularios para el mismo cuatrimestre`() {
        alumnoService.guardarSolicitudPara(
            alumno.dni,
            listOf(comision1Algoritmos.id!!),
            cuatrimestre
        )
        val exception = assertThrows<ExcepcionUNQUE> {
            alumnoService.guardarSolicitudPara(
                alumno.dni,
                listOf(),
                cuatrimestre
            )
        }
        assertThat(exception.message).isEqualTo("Ya has solicitado materias para este cuatrimestre")
    }

    @Test
    fun `Un alumno no puede registrar un formulario para una materia que no tiene disponible`() {
        val logica = materiaService.crear("Lógica y Programacion", "LOG-209", mutableListOf(algo.codigo), Carrera.TPI)
        val formularioComision = FormularioComision(
                1,
                logica.codigo,
                cuatrimestre.anio,
                cuatrimestre.semestre,
                35,
                5,
                listOf(
                        HorarioDTO(Dia.LUNES, "18:00", "20:00"),
                        HorarioDTO(Dia.JUEVES, "09:00", "11:00")
                ),
                Modalidad.PRESENCIAL
        )
        val comisionLogica = comisionService.crear(formularioComision)
        val exception = assertThrows<ExcepcionUNQUE> {
            alumnoService.guardarSolicitudPara(
                    alumno.dni,
                    listOf(comisionLogica.id!!),
                    cuatrimestre
            )
        }
        assertThat(exception.message).isEqualTo("El alumno no puede cursar las materias solicitadas")
    }

    @Test
    fun `Se puede obtener el formulario`() {
        val formularioDTO =
            alumnoService.guardarSolicitudPara(
                alumno.dni,
                listOf(comision1Algoritmos.id!!),
                cuatrimestre
            )
        val formularioPersistido = alumnoService.obtenerFormulario(alumno.dni, cuatrimestre)

        assertThat(formularioDTO).usingRecursiveComparison().isEqualTo(formularioPersistido)
    }

    @Test
    fun `Se puede aprobar una solicitud de sobrecupo`() {
        val formulario =
            alumnoService.guardarSolicitudPara(
                alumno.dni,
                listOf(comision1Algoritmos.id!!),
                cuatrimestre
            )
        val solicitudPendiente = formulario.solicitudes.first()
        val solicitudAprobada = alumnoService.cambiarEstado(solicitudPendiente.id, EstadoSolicitud.APROBADO)

        assertThat(solicitudAprobada.estado).isEqualTo(EstadoSolicitud.APROBADO)
        assertThat(solicitudAprobada).usingRecursiveComparison().ignoringFields("estado").isEqualTo(solicitudPendiente)
    }

    @Test
    fun `Se puede rechazar una solicitud de sobrecupo`() {
        val formulario =
            alumnoService.guardarSolicitudPara(
                alumno.dni,
                listOf(comision1Algoritmos.id!!),
                cuatrimestre
            )
        val solicitudPendiente = formulario.solicitudes.first()
        val solicitudRechazada = alumnoService.cambiarEstado(solicitudPendiente.id, EstadoSolicitud.RECHAZADO)

        assertThat(solicitudRechazada.estado).isEqualTo(EstadoSolicitud.RECHAZADO)
    }

    @Test
    fun `Se pueden cerrar todos los formularios del cuatrimestre corriente`() {
        val formularioAntesDeCerrar =
            alumnoService.guardarSolicitudPara(
                alumno.dni,
                listOf(comision1Algoritmos.id!!),
                cuatrimestre
            )
        val formulario2AntesDeCerrar =
            alumnoService.guardarSolicitudPara(
                fede.dni,
                listOf(comision1Algoritmos.id!!),
                cuatrimestre
            )

        alumnoService.cambiarEstadoFormularios()
        val formularioDespuesDeCerrar =
            alumnoService.obtenerFormulario(alumno.dni, cuatrimestre)
        val formulario2DespuesDeCerrar =
            alumnoService.obtenerFormulario(fede.dni, cuatrimestre)

        assertThat(listOf(formularioAntesDeCerrar, formulario2AntesDeCerrar).map { it.estado })
            .usingRecursiveComparison()
            .isEqualTo(listOf(EstadoFormulario.ABIERTO, EstadoFormulario.ABIERTO))

        assertThat(listOf(formularioDespuesDeCerrar, formulario2DespuesDeCerrar).map { it.estado })
            .usingRecursiveComparison()
            .isEqualTo(listOf(EstadoFormulario.CERRADO, EstadoFormulario.CERRADO))
    }

    @Test
    fun `Se puede cargar la historia academica de un alumno`() {
        val materiaCursada = MateriaCursadaDTO(funcional.codigo, EstadoMateria.APROBADO, LocalDate.of(2021, 12, 20))
        val formularioAlumno = FormularioCrearAlumno(
            1234567,
            "Pepe",
            "Sanchez",
            "pepe.sanchez@unq.edu.ar",
            44556,
            "1234",
            Carrera.TPI,
            listOf(materiaCursada)
        )

        val alumno = alumnoService.crear(formularioAlumno)

        assertThat(alumno.historiaAcademica).isNotEmpty
        assertThat(alumno.historiaAcademica.first().materia.codigo).isEqualTo(materiaCursada.codigoMateria)
    }

    @Test
    fun `Se puede obtener las materias disponibles de un alumno`() {
        val materiasdisponibles =
            alumnoService.materiasDisponibles(alumno.dni, cuatrimestre)
        assertThat(materiasdisponibles).isNotEmpty
        assertThat(materiasdisponibles.first().codigo).isEqualTo(algo.codigo)
    }

    @Test
    fun `un alumno tiene disponible materias solo de su carrera`() {
        val logica = materiaService.crear("Lógica y Programacion", "LOG-209", mutableListOf(), Carrera.LICENCIATURA)
        val materiasdisponibles =
            alumnoService.materiasDisponibles(alumno.dni, cuatrimestre)
        assertThat(materiasdisponibles.map { it.codigo }).doesNotContain(logica.codigo)
    }

    @Test
    fun `un alumno no tiene disponible materias de las cuales no cumple los requisitos`() {
        val logica = materiaService.crear("Lógica y Programacion", "LOG-209", mutableListOf("ALG-208"), Carrera.TPI)
        val materiasdisponibles =
            alumnoService.materiasDisponibles(alumno.dni, cuatrimestre)
        assertThat(materiasdisponibles).hasSize(1)
        assertThat(materiasdisponibles.map { it.codigo }).contains(algo.codigo).doesNotContain(logica.codigo)
    }

    @Test
    fun `un alumno tiene disponible materias de las cuales cumple los requisitos`() {
        val materiaCursada = MateriaCursadaDTO(algo.codigo, EstadoMateria.APROBADO, LocalDate.of(2021, 12, 20))
        val formularioAlumno = FormularioCrearAlumno(
            123456712,
            "Pepe",
            "Sanchez",
            "pepe.sanchez@unq.edu.ar",
            4455611,
            "1234",
            Carrera.TPI,
            listOf(materiaCursada)
        )
        val nacho = alumnoService.crear(formularioAlumno)
        val logica = materiaService.crear("Lógica y Programacion", "LOG-209", mutableListOf(algo.codigo), Carrera.TPI)
        val formularioComision = FormularioComision(
            1,
            logica.codigo,
            cuatrimestre.anio,
            cuatrimestre.semestre,
            35,
            5,
            listOf(
                HorarioDTO(Dia.LUNES, "18:00", "20:00"),
                HorarioDTO(Dia.JUEVES, "09:00", "11:00")
            ),
            Modalidad.PRESENCIAL
        )
        val comisionLogica = comisionService.crear(formularioComision)
        val materiasdisponibles = alumnoService.materiasDisponibles(nacho.dni, cuatrimestre)
        assertThat(materiasdisponibles).hasSize(1)
        assertThat(materiasdisponibles.map { it.codigo }).contains(logica.codigo)
        assertThat(materiasdisponibles.first().comisiones).allMatch {
            it == ComisionParaAlumno.desdeModelo(
                comisionLogica
            )
        }
    }

    @Test
    fun `un alumno no tiene disponible materias que ya aprobo`() {
        val materiaCursada = MateriaCursadaDTO(algo.codigo, EstadoMateria.APROBADO, LocalDate.of(2021, 12, 20))
        val formularioAlumno = FormularioCrearAlumno(
            123456712,
            "Pepe",
            "Sanchez",
            "pepe.sanchez@unq.edu.ar",
            4455611,
            "1234",
            Carrera.TPI,
            listOf(materiaCursada)
        )
        val nacho = alumnoService.crear(formularioAlumno)
        val materiasdisponibles = alumnoService.materiasDisponibles(nacho.dni, cuatrimestre)
        assertThat(materiasdisponibles.map { it.codigo }).doesNotContain(algo.codigo)
    }

    @Test
    fun `se levanta una excepcion al enviar un formulario pasada la fecha de fin aceptada por el cuatrimestre`() {
        comisionService.actualizarOfertaAcademica(listOf(), LocalDateTime.now(), LocalDateTime.now().plusDays(3))

        val excepcion = assertThrows<ExcepcionUNQUE> {
            alumnoService.guardarSolicitudPara(
                alumno.dni,
                listOf(comision1Algoritmos.id!!),
                cuatrimestre,
                LocalDateTime.now().plusDays(5)
            )
        }
        assertThat(excepcion.message).isEqualTo("El periodo para enviar solicitudes de sobrecupos ya ha pasado.")
    }

    @Test
    fun `se levanta una excepcion al enviar un formulario antes de la fecha de inicio aceptada por el cuatrimestre`() {
        comisionService.actualizarOfertaAcademica(listOf(), LocalDateTime.now(), LocalDateTime.now().plusDays(3))

        val excepcion = assertThrows<ExcepcionUNQUE> {
            alumnoService.guardarSolicitudPara(
                alumno.dni,
                listOf(comision1Algoritmos.id!!),
                cuatrimestre,
                LocalDateTime.now().minusDays(5)
            )
        }
        assertThat(excepcion.message).isEqualTo("El periodo para enviar solicitudes de sobrecupos no ha empezado.")
    }

    @Test
    fun `se puede pedir el resumen de estado de un alumno`() {
        val materiaCursada = MateriaCursadaDTO(algo.codigo, EstadoMateria.APROBADO, LocalDate.of(2021, 12, 20))
        val materiaCursada2 = MateriaCursadaDTO(funcional.codigo, EstadoMateria.DESAPROBADO, LocalDate.of(2020, 12, 20))
        val materiaCursada3 = MateriaCursadaDTO(funcional.codigo, EstadoMateria.APROBADO, LocalDate.of(2021, 7, 20))
        val intro = materiaService.crear("Intro", "INT-205", mutableListOf(), Carrera.SIMULTANEIDAD)

        val formularioComision2 = FormularioComision(
                1,
                intro.codigo,
                2022,
                Semestre.S1,
                35,
                5,
                listOf(),
                Modalidad.PRESENCIAL
        )
        val comisionIntro = comisionService.crear(formularioComision2)
        val formularioAlumno = FormularioCrearAlumno(
            123456712,
            "Pepe",
            "Sanchez",
            "pepe.sanchez@unq.edu.ar",
            44556,
            "1234",
            Carrera.SIMULTANEIDAD,
            listOf(materiaCursada, materiaCursada2, materiaCursada3)
        )
        val nacho = alumnoService.crear(formularioAlumno)

        val formulario = alumnoService.guardarSolicitudPara(
            nacho.dni,
            listOf(comisionIntro.id!!)
        )

        val resumen = alumnoService.obtenerResumenAlumno(nacho.dni)

        assertThat(resumen.nombre).isEqualTo(nacho.nombre)
        assertThat(resumen.dni).isEqualTo(nacho.dni)
        assertThat(resumen.formulario).usingRecursiveComparison().isEqualTo(formulario)
        assertThat(resumen.resumenCursadas.map { it.nombreMateria }).usingRecursiveComparison()
            .isEqualTo(listOf(algo.nombre, funcional.nombre))
    }

    @Test
    fun `se pueden obtener los alumnos que pidieron una comision`() {
        val formularioAlumno = alumnoService.guardarSolicitudPara(alumno.dni, listOf(comision1Algoritmos.id!!))
        val formularioFede = alumnoService.guardarSolicitudPara(fede.dni, listOf(comision1Algoritmos.id!!))

        val alumnos = alumnoService.alumnosQueSolicitaron(comision1Algoritmos.id!!)

        val alumnosEsperados: List<AlumnoSolicitaComision> =
            listOf(
                AlumnoSolicitaComision(alumno.dni, alumno.cantidadAprobadas(), formularioAlumno.id, formularioAlumno.solicitudes.first().id),
                AlumnoSolicitaComision(fede.dni,  fede.cantidadAprobadas(), formularioFede.id, formularioFede.solicitudes.first().id)
            )
        assertThat(alumnos).hasSize(2)
        assertThat(alumnos).usingRecursiveComparison().isEqualTo(alumnosEsperados)
    }

    @Test
    fun `se pueden obtener los alumnos que pidieron una comision con el id del formulario`() {
        val formularioAlumno = alumnoService.guardarSolicitudPara(alumno.dni, listOf(comision1Algoritmos.id!!))
        val formularioFede = alumnoService.guardarSolicitudPara(fede.dni, listOf(comision1Algoritmos.id!!))

        val alumnos = alumnoService.alumnosQueSolicitaron(comision1Algoritmos.id!!)

        assertThat(alumnos.first().idFormulario).isEqualTo(formularioAlumno.id)
        assertThat(alumnos.last().idFormulario).isEqualTo(formularioFede.id)
    }

    @Test
    fun `se pueden obtener los alumnos que pidieron una comision ordenados descendentemente por cantidad de materias aprobadas`() {
        val materiaCursada = MateriaCursadaDTO(funcional.codigo, EstadoMateria.APROBADO, LocalDate.of(2021, 12, 20))
        val formularioAlumno = FormularioCrearAlumno(
            123456712,
            "Pepe",
            "Sanchez",
            "pepe.sanchez@unq.edu.ar",
            4455611,
            "1234",
            Carrera.TPI,
            listOf(materiaCursada)
        )
        val nacho = alumnoService.crear(formularioAlumno)

        alumnoService.guardarSolicitudPara(alumno.dni, listOf(comision1Algoritmos.id!!))
        alumnoService.guardarSolicitudPara(nacho.dni, listOf(comision1Algoritmos.id!!))

        val alumnos = alumnoService.alumnosQueSolicitaron(comision1Algoritmos.id!!)
        assertThat(alumnos.first().cantidadDeAprobadas).isEqualTo(alumnos.maxOf { it.cantidadDeAprobadas })
        assertThat(alumnos.last().cantidadDeAprobadas).isEqualTo(alumnos.minOf { it.cantidadDeAprobadas })
    }

    @AfterEach
    fun tearDown() {
        dataService.clearDataSet()
    }

}
package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.EstadoSolicitud
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.comision.Dia
import ar.edu.unq.postinscripciones.model.comision.Modalidad
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.comision.HorarioDTO
import ar.edu.unq.postinscripciones.service.dto.formulario.*
import ar.edu.unq.postinscripciones.service.dto.materia.Correlativa
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaConCorrelativas
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@IntegrationTest
internal class MateriaServiceTest {

    @Autowired
    private lateinit var materiaService: MateriaService

    @Autowired
    private lateinit var dataService: DataService
    @Autowired
    private lateinit var comisionService: ComisionService

    @Autowired
    private lateinit var cuatrimestreService: CuatrimestreService

    @Autowired
    private lateinit var alumnoService: AlumnoService
    private lateinit var bdd: MateriaDTO
    private lateinit var algo: MateriaDTO
    private lateinit var comision: Comision
    private lateinit var comision2: Comision
    private lateinit var cuatrimestre: Cuatrimestre

    @BeforeEach
    fun setUp() {
        bdd = materiaService.crear("Base de datos", "BD-096", mutableListOf(), Carrera.PW)
        algo = materiaService.crear("Algoritmos", "AA-208", mutableListOf(), Carrera.PW)

        val formularioCuatrimestre = FormularioCuatrimestre(2022, Semestre.S1)
        cuatrimestre = cuatrimestreService.crear(formularioCuatrimestre)

        var horarios = listOf(
            HorarioDTO(Dia.LUNES, "18:30", "21:30"),
            HorarioDTO(Dia.JUEVES, "18:30", "21:30")
        )
        val formulario = FormularioComision(
            1,
            bdd.codigo,
            2022,
            Semestre.S1,
            35,
            5,
            horarios,
            Modalidad.PRESENCIAL
        )
        comision = comisionService.crear(formulario)
        horarios = listOf(
            HorarioDTO(Dia.LUNES, "18:30", "21:30"),
            HorarioDTO(Dia.JUEVES, "18:30", "21:30")
        )
        val formulario2 = FormularioComision(
            2,
            algo.codigo,
            2022,
            Semestre.S1,
            35,
            5,
            horarios,
            Modalidad.PRESENCIAL
        )
        comision2 = comisionService.crear(formulario2)
    }

    @Test
    fun `Se puede crear una materia`() {
        val materia = materiaService.crear("Intro", "IP-102", mutableListOf(), Carrera.PW)
        assertThat(materia).isNotNull
    }

    @Test
    fun `no se puede crear una materia con un nombre existente`() {
        val materia = materiaService.crear("Intro", "IP-102", mutableListOf(), Carrera.PW)
        assertThat(materia).isNotNull
    }

    @Test
    fun `no se puede crear una materia con un codigo o nombre existente`() {
        val materia = materiaService.crear("Intro", "IP-102", mutableListOf(), Carrera.PW)
        val nombreConflictivo = materia.nombre.lowercase()
        val codigoConflictivo = materia.codigo.lowercase()
        val excepcion = assertThrows<ExcepcionUNQUE> {
            materiaService.crear(
                nombreConflictivo,
                codigoConflictivo,
                mutableListOf(),
                Carrera.PW
            )
        }

        assertThat(excepcion.message).isEqualTo(
            "La materia que desea crear con nombre $nombreConflictivo " +
                    "y codigo $codigoConflictivo, " +
                    "genera conflicto con la materia: ${materia.nombre}, codigo: ${materia.codigo}"
        )
    }

    @Test
    fun `se puede crear una materia con una correlativa`() {
        val materia = materiaService.crear("Orga", "ORGA-101", mutableListOf("BD-096"), Carrera.PW)
        assertThat(materia.correlativas.first()).isEqualTo(bdd.nombre)
    }

    @Test
    fun `no se puede crear una materia con una correlativa inexistente`() {
        val excepcion = assertThrows<ExcepcionUNQUE> {
            materiaService.crear(
                "Orga",
                "ORGA-101",
                mutableListOf("EPYL-103"),
                Carrera.PW
            )
        }

        assertThat(excepcion.message).isEqualTo("No existe la materia con codigo: EPYL-103")
    }

    @Test
    fun `Se puede crear una lista de materias`() {
        val intro = FormularioMateria("00487", "Introducción a la Programación", Carrera.PW)
        val orga = FormularioMateria("01032", "Organización de las Computadoras", Carrera.PW)
        materiaService.crear(listOf(intro, orga))

        assertThat(materiaService.todas().map { it.nombre }).containsAll(listOf(intro.nombre, orga.nombre))
    }

    @Test
    fun `no se puede crear una lista de materias que ya existen`() {
        val listaConflictiva = materiaService.crear(listOf(FormularioMateria("Base de datos", "BD-096",  Carrera.PW)))

        assertThat(listaConflictiva.first().mensaje).isEqualTo("Conflicto con la materia ${bdd.nombre} y codigo ${bdd.codigo}")
    }

    @Test
    fun `se puede crear una lista de materias con una correlativa`() {
        val orga = FormularioMateria("01032", "Organización de las Computadoras", Carrera.PW)
        materiaService.crear(listOf(orga))
        val materiasParaActualizar = listOf(MateriaConCorrelativas(orga.codigo, listOf(Correlativa(bdd.codigo))))
        materiaService.actualizarCorrelativas(materiasParaActualizar)

        val materia = materiaService.obtener(orga.codigo)
        assertThat(materia.correlativas.first().nombre).isEqualTo(bdd.nombre)
    }

    @Test
    fun `no se puede las correlativas de una materia con un codigo inexistente`() {
        val orga = FormularioMateria("01032", "Organización de las Computadoras", Carrera.PW)
        materiaService.crear(listOf(orga))

        val resultado = materiaService
            .actualizarCorrelativas(listOf(
                MateriaConCorrelativas(orga.codigo, listOf(Correlativa("NO EXISTE")))))

        assertThat(resultado.first().mensaje).isEqualTo("No se encontró la correlativa")
    }

    @Test
    fun `Se pueden obtener todas las materias registradas`() {
        val materiasEsperadas = arrayListOf(bdd, algo)

        assertThat(materiaService.todas()).usingRecursiveComparison().isEqualTo(materiasEsperadas)
    }

    @Test
    fun `Se puede obtener una materia especifica`() {
        val materiaEncontrada = materiaService.obtener(bdd.codigo)

        assertThat(materiaEncontrada.nombre).isEqualTo(bdd.nombre)
    }

    @Test
    fun `No se puede obtener una materia que no existe`() {
        val exception = assertThrows<ExcepcionUNQUE> { materiaService.obtener("AA-207") }

        assertThat(exception.message).isEqualTo("No se encuentra la materia")
    }

    @Test
    fun `Se puede actualizar las materias correlativas de una materia`() {
        val materia = materiaService.crear("Orga", "ORGA-101", mutableListOf("BD-096"), Carrera.PW)
        val materiasParaActualizar = listOf(MateriaConCorrelativas(materia.codigo, listOf(Correlativa(algo.codigo))))
        materiaService.actualizarCorrelativas(materiasParaActualizar)

        val materiaActualizada = materiaService.obtener(materia.codigo)

        assertThat(materiaActualizada.correlativas.first().nombre).isEqualTo(algo.nombre)
        assertThat(materiaActualizada.correlativas).hasSize(1)
    }

    @Test
    fun `Obtener comisiones ordenadas por cantidad de solicitudes pendientes`() {
        val alumno1 =
            alumnoService.crear(FormularioCrearAlumno(4235, "", "", "", 12341, Carrera.W, 0.0))
        val alumno2 =
            alumnoService.crear(FormularioCrearAlumno(42355, "", "", "", 12331, Carrera.W, 0.0))

        val formulario = alumnoService.guardarSolicitudPara(alumno1.dni, listOf(comision.id!!, comision2.id!!), cuatrimestre)
        alumnoService.guardarSolicitudPara(alumno2.dni, listOf(comision.id!!), cuatrimestre)

        comisionService.actualizarOfertaAcademica(listOf(), LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1), cuatrimestre)
        alumnoService.cambiarEstadoSolicitud(
            formulario.solicitudes.first().id,
            EstadoSolicitud.APROBADO,
            formulario.id
        )
        alumnoService.cambiarEstadoSolicitud(
            formulario.solicitudes.last().id,
            EstadoSolicitud.APROBADO,
            formulario.id
        )
        val materiasObtenidas = materiaService.materiasPorSolicitudes(cuatrimestre, "")

        assertThat(materiasObtenidas.maxOf { it.cantidadSolicitudesPendientes }).isEqualTo(materiasObtenidas.first().cantidadSolicitudesPendientes)
        assertThat(materiasObtenidas.minOf { it.cantidadSolicitudesPendientes }).isEqualTo(materiasObtenidas.last().cantidadSolicitudesPendientes)
        assertThat(materiasObtenidas.first().cantidadSolicitudesPendientes).isEqualTo(1)
        assertThat(materiasObtenidas.first().codigo).isEqualTo(comision.materia.codigo)
        assertThat(materiasObtenidas.last().cantidadSolicitudesPendientes).isEqualTo(0)
    }

    @Test
    fun `se puede modificar una materia ya creada`() {
        val formulario = FormularioModificarMateria("Algoritmoss", algo.codigo, Carrera.W)
        val nuevaMateria = materiaService.modificar(formulario)

        val materiaPersistida = materiaService.obtener(algo.codigo)

        assertThat(nuevaMateria).usingRecursiveComparison().isNotEqualTo(algo)
        assertThat(nuevaMateria.nombre).isEqualTo(materiaPersistida.nombre)
        assertThat(nuevaMateria.carrera).isEqualTo(materiaPersistida.carrera())
    }

    @AfterEach
    fun tearDown() {
        dataService.clearDataSet()
    }
}
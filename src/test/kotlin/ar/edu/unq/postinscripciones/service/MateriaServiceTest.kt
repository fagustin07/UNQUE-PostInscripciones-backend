package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.comision.Dia
import ar.edu.unq.postinscripciones.model.comision.Modalidad
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.comision.HorarioDTO
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioComision
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCuatrimestre
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioMateria
import ar.edu.unq.postinscripciones.service.dto.materia.Correlativa
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaConCorrelativas
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

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
        bdd = materiaService.crear("Base de datos", "BD-096", mutableListOf(), Carrera.SIMULTANEIDAD)
        algo = materiaService.crear("Algoritmos", "AA-208", mutableListOf(), Carrera.SIMULTANEIDAD)

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
        val materia = materiaService.crear("Intro", "IP-102", mutableListOf(), Carrera.SIMULTANEIDAD)
        assertThat(materia).isNotNull
    }

    @Test
    fun `no se puede crear una materia con un nombre existente`() {
        val materia = materiaService.crear("Intro", "IP-102", mutableListOf(), Carrera.SIMULTANEIDAD)
        assertThat(materia).isNotNull
    }

    @Test
    fun `no se puede crear una materia con un codigo o nombre existente`() {
        val materia = materiaService.crear("Intro", "IP-102", mutableListOf(), Carrera.SIMULTANEIDAD)
        val nombreConflictivo = materia.nombre.lowercase()
        val codigoConflictivo = materia.codigo.lowercase()
        val excepcion = assertThrows<ExcepcionUNQUE> {
            materiaService.crear(
                nombreConflictivo,
                codigoConflictivo,
                mutableListOf(),
                Carrera.SIMULTANEIDAD
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
        val materia = materiaService.crear("Orga", "ORGA-101", mutableListOf("BD-096"), Carrera.SIMULTANEIDAD)
        assertThat(materia.correlativas.first()).isEqualTo(bdd.nombre)
    }

    @Test
    fun `no se puede crear una materia con una correlativa inexistente`() {
        val excepcion = assertThrows<ExcepcionUNQUE> {
            materiaService.crear(
                "Orga",
                "ORGA-101",
                mutableListOf("EPYL-103"),
                Carrera.SIMULTANEIDAD
            )
        }

        assertThat(excepcion.message).isEqualTo("No existe la materia con codigo: EPYL-103")
    }

    @Test
    fun `Se puede crear una lista de materias`() {
        val intro = FormularioMateria("00487", "Introducción a la Programación", Carrera.SIMULTANEIDAD)
        val orga = FormularioMateria("01032", "Organización de las Computadoras", Carrera.SIMULTANEIDAD)
        val materiasCreadas = materiaService.crear(listOf(intro, orga))

        assertThat(materiasCreadas.map{it.codigo}).containsAll(listOf(intro.codigo, orga.codigo))
    }

    @Test
    fun `no se puede crear una lista de materias que ya existen`() {
        val excepcion = assertThrows<ExcepcionUNQUE> {
            materiaService.crear(listOf(FormularioMateria("Base de datos", "BD-096",  Carrera.SIMULTANEIDAD)))
        }
        assertThat(excepcion.message).isEqualTo(
            "La materia que desea crear con nombre Base de datos " +
                    "y codigo BD-096, " +
                    "genera conflicto con la materia: ${bdd.nombre}, codigo: ${bdd.codigo}"
        )
    }

    @Test
    fun `se puede crear una lista de materias con una correlativa`() {
        val orga = FormularioMateria("01032", "Organización de las Computadoras", Carrera.SIMULTANEIDAD)
        materiaService.crear(listOf(orga))
        val materiasParaActualizar = listOf(MateriaConCorrelativas(orga.nombre, listOf(Correlativa(bdd.nombre))))
        val materiasDTO = materiaService.actualizarCorrelativas(materiasParaActualizar)

        assertThat(materiasDTO.first().correlativas.first()).isEqualTo(bdd.nombre)
    }

    @Test
    fun `no se puede las correlativas de una materia con un nombre inexistente`() {
        val orga = FormularioMateria("01032", "Organización de las Computadoras", Carrera.SIMULTANEIDAD)
        materiaService.crear(listOf(orga))
        val excepcion = assertThrows<ExcepcionUNQUE> {
            materiaService.actualizarCorrelativas(listOf(MateriaConCorrelativas(orga.nombre, listOf(Correlativa("NO EXISTE")))))
        }

        assertThat(excepcion.message).isEqualTo("No existe la materia con nombre: NO EXISTE")
    }

    @Test
    fun `Se pueden obtener todas las materias registradas`() {
        val materiasEsperadas = arrayListOf(bdd, algo)

        assertThat(materiaService.todas()).usingRecursiveComparison().isEqualTo(materiasEsperadas)
    }

    @Test
    fun `Se puede obtener una materia especifica`() {
        val materiaEncontrada = materiaService.obtener(bdd.codigo)
        bdd.correlativas.size
        assertThat(materiaEncontrada).usingRecursiveComparison().isEqualTo(bdd)
    }

    @Test
    fun `No se puede obtener una materia que no existe`() {
        val exception = assertThrows<ExcepcionUNQUE> { materiaService.obtener("AA-207") }

        assertThat(exception.message).isEqualTo("No se encuentra la materia")
    }

    @Test
    fun `Se puede actualizar las materias correlativas de una materia`() {
        val materia = materiaService.crear("Orga", "ORGA-101", mutableListOf("BD-096"), Carrera.SIMULTANEIDAD)
        val correlativasAntes = materia.correlativas
        val materiasParaActualizar = listOf(MateriaConCorrelativas(materia.nombre, listOf(Correlativa(algo.nombre))))
        val materiasDespuesDeActualizarCorrelativas =
                materiaService.actualizarCorrelativas(materiasParaActualizar)

        assertThat(correlativasAntes).isNotEqualTo(materiasDespuesDeActualizarCorrelativas.first().correlativas)
        assertThat(materiasDespuesDeActualizarCorrelativas.first().correlativas.first()).isEqualTo(algo.nombre)

    }

    @Test
    fun `Obtener comisiones ordenadas por cantidad de solicitudes`() {
        val alumno1 =
            alumnoService.crear(FormularioCrearAlumno(4235, "", "", "", 12341, Carrera.LICENCIATURA, 0.0))
        val alumno2 =
            alumnoService.crear(FormularioCrearAlumno(42355, "", "", "", 12331, Carrera.LICENCIATURA, 0.0))

        alumnoService.guardarSolicitudPara(alumno1.dni, listOf(comision.id!!, comision2.id!!), cuatrimestre)
        alumnoService.guardarSolicitudPara(alumno2.dni, listOf(comision.id!!), cuatrimestre)

        val materiasObtenidas = materiaService.materiasPorSolicitudes(cuatrimestre)

        assertThat(materiasObtenidas.maxOf { it.cantidadSolicitudes }).isEqualTo(materiasObtenidas.first().cantidadSolicitudes)
        assertThat(materiasObtenidas.minOf { it.cantidadSolicitudes }).isEqualTo(materiasObtenidas.last().cantidadSolicitudes)
        assertThat(materiasObtenidas.first().cantidadSolicitudes).isEqualTo(2)
        assertThat(materiasObtenidas.first().codigo).isEqualTo(bdd.codigo)
        assertThat(materiasObtenidas.last().cantidadSolicitudes).isEqualTo(1)
    }

    @AfterEach
    fun tearDown() {
        dataService.clearDataSet()
    }
}
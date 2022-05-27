package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.MateriaDTO
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

    private lateinit var bdd: MateriaDTO
    private lateinit var algo: MateriaDTO

    @BeforeEach
    fun setUp() {
        bdd = materiaService.crear("Base de datos", "BD-096", mutableListOf(), Carrera.SIMULTANEIDAD)
        algo = materiaService.crear("Algoritmos", "AA-208", mutableListOf(), Carrera.SIMULTANEIDAD)
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

        val materiaDespuesDeActualizarCorrelativas =
                materiaService.actualizarCorrelativas(materia.codigo, listOf(algo.codigo))

        assertThat(correlativasAntes).isNotEqualTo(materiaDespuesDeActualizarCorrelativas.correlativas)
        assertThat(materiaDespuesDeActualizarCorrelativas.correlativas.first()).isEqualTo(algo.nombre)

    }

    @AfterEach
    fun tearDown() {
        dataService.clearDataSet()
    }
}
package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.FormularioCuatrimestre
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class CuatrimestreServiceTest {

    @Autowired
    private lateinit var dataService: DataService

    @Autowired
    private lateinit var cuatrimestreService: CuatrimestreService

    private lateinit var c1: Cuatrimestre
    private lateinit var c2: Cuatrimestre

    @BeforeEach
    fun setUp() {
        val formularioCuatrimestreS1 = FormularioCuatrimestre(2022, Semestre.S1)
        val formularioCuatrimestreS2 = FormularioCuatrimestre(2022, Semestre.S2)
        c1 = cuatrimestreService.crear(formularioCuatrimestreS1)
        c2 = cuatrimestreService.crear(formularioCuatrimestreS2)

    }

    @Test
    fun `Se puede crear un cuatrimestre con el anio y el semestre actual`() {
        assertThat(c2).isNotNull
    }

    @Test
    fun `no se pueden repetir cuatrimestres por la combinacion del anio y semestre`() {
        val formularioCuatrimestreS2 = FormularioCuatrimestre(2022, Semestre.S2)
        val excepcion = assertThrows<ExcepcionUNQUE> { cuatrimestreService.crear(formularioCuatrimestreS2) }

        assertThat(excepcion.message).isEqualTo("Ya existe el cuatrimestre que desea crear.")
    }

    @AfterEach
    fun tearDown() {
        dataService.clearDataSet()
    }
}
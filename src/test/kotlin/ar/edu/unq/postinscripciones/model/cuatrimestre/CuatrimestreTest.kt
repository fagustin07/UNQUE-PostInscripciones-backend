package ar.edu.unq.postinscripciones.model.cuatrimestre

import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

internal class CuatrimestreTest {
    lateinit var cuatrimestre: Cuatrimestre

    @BeforeEach
    fun setUp() {
        cuatrimestre = Cuatrimestre(2009, Semestre.S2)
    }

    @Test
    fun `un cuatrimestre conoce su anio`() {
        assertThat(cuatrimestre.anio).isEqualTo(2009)
    }

    @Test
    fun `un cuatrimestre conoce su semestre`() {
        assertThat(cuatrimestre.semestre).isEqualTo(Semestre.S2)
    }

    @Test
    fun `un cuatrimestre puede decir que es igual a otro`() {
        assertThat(cuatrimestre.esElCuatrimestre(2009, Semestre.S2)).isTrue
    }

    @Test
    fun `un cuatrimestre puede decir que no es igual a otro`() {
        assertThat(cuatrimestre.esElCuatrimestre(2020, Semestre.S1)).isFalse
    }

    @Test
    fun `un cuatrimestre conoce su fecha de incio y de fin para aceptar formularios`() {
        val inicio = LocalDateTime.now()
        val fin = LocalDateTime.now().plusDays(14)

        cuatrimestre.actualizarFechas(inicio, fin)

        assertThat(cuatrimestre.inicioInscripciones).isEqualTo(inicio)
        assertThat(cuatrimestre.finInscripciones).isEqualTo(fin)
    }

    private val mensajeInicioMayorAFin = "La fecha de inicio no puede ser mayor que la de fin"

    @Test
    fun `un cuatrimestre no puede cambiar la fecha de inicio a una mayor que la de fin existente`() {
        val inicio = cuatrimestre.finInscripciones.plusDays(1)

        val excepcion = assertThrows<ExcepcionUNQUE> { cuatrimestre.actualizarFechas(inicio, null) }

        assertThat(excepcion.message).isEqualTo(mensajeInicioMayorAFin)
    }

    @Test
    fun `un cuatrimestre no puede cambiar la fecha de inicio a una mayor que la de fin a actualizar`() {
        val inicio = cuatrimestre.finInscripciones.plusDays(15)
        val fin = cuatrimestre.finInscripciones.plusDays(1)

        val excepcion = assertThrows<ExcepcionUNQUE> { cuatrimestre.actualizarFechas(inicio, fin) }

        assertThat(excepcion.message).isEqualTo(mensajeInicioMayorAFin)
    }

    @Test
    fun `un cuatrimestre no puede cambiar la fecha de fin a una menor que la de inicio existente`() {
        val fin = cuatrimestre.inicioInscripciones.minusDays(2)

        val excepcion = assertThrows<ExcepcionUNQUE> { cuatrimestre.actualizarFechas(null, fin) }

        assertThat(excepcion.message).isEqualTo("La fecha de fin no puede ser menor a la de inicio")
    }

    @Test
    fun `el cuatrimestre actual no puede tener una fecha de inicio dada mayor a la de fin dada`() {
        val excepcion = assertThrows<ExcepcionUNQUE> { Cuatrimestre.actualConFechas(LocalDateTime.now().plusDays(30), LocalDateTime.now()) }

        assertThat(excepcion.message).isEqualTo(mensajeInicioMayorAFin)
    }

    @Test
    fun `el cuatrimestre actual no puede tener una fecha de inicio mayor a la de fin`() {
        val excepcion = assertThrows<ExcepcionUNQUE> { Cuatrimestre.actualConFechas(LocalDateTime.now().plusDays(30), null) }

        assertThat(excepcion.message).isEqualTo(mensajeInicioMayorAFin)
    }

    @Test
    fun `el cuatrimestre actual no puede tener una fecha de fin menor a la de inicio`() {
        val excepcion = assertThrows<ExcepcionUNQUE> { Cuatrimestre.actualConFechas(null, LocalDateTime.now().minusDays(30)) }

        assertThat(excepcion.message).isEqualTo(mensajeInicioMayorAFin)
    }


}
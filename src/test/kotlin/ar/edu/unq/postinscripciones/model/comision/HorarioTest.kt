package ar.edu.unq.postinscripciones.model.comision

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalTime

internal class HorarioTest {
    lateinit var horario: Horario
    val horaDieciocho: LocalTime = LocalTime.of(18, 0)
    val horaVeintiuno: LocalTime = LocalTime.of(21, 0)

    @BeforeEach
    fun `set up`() {
        horario = Horario(Dia.Lun, horaDieciocho, horaVeintiuno)
    }

    @Test
    fun `un horario conoce su dia`() {
        assertThat(horario.dia).isEqualTo(Dia.Lun)
    }

    @Test
    fun `un horario conoce su hora de inicio`() {
        assertThat(horario.inicio).isEqualTo(horaDieciocho)
    }

    @Test
    fun `un horario conoce su hora de fin`() {
        assertThat(horario.fin).isEqualTo(horaVeintiuno)
    }
}
package ar.edu.unq.postinscripciones.model.comision

import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ErrorDeNegocio
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Locacion
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalTime

internal class ComisionTest {
    lateinit var bdd: Materia
    lateinit var comisionUnoBdd: Comision

    val cuposTotales = 25
    val sobrecuposTotales = 15
    val cuatrimestre = Cuatrimestre(2022, Semestre.S2)

    @BeforeEach
    fun `set up`() {
        val horarios: List<Horario> = horariosBdd()
        bdd = Materia()
        comisionUnoBdd = Comision(bdd, 1, cuatrimestre, horarios.toMutableList(), cuposTotales, sobrecuposTotales, locacion = Locacion.Berazategui)
    }

    @Test
    fun `una comision pertenece a una materia`() {
        assertThat(comisionUnoBdd.materia).isEqualTo(bdd)
    }

    @Test
    fun `una comision posee un numero de comision`() {
        assertThat(comisionUnoBdd.numero).isEqualTo(1)
    }

    @Test
    fun `una comision conoce en que cuatrimestre se dicta`() {
        assertThat(comisionUnoBdd.cuatrimestre).isEqualTo(cuatrimestre)
    }

    @Test
    fun `una comision conoce sus cupos totales`() {
        assertThat(comisionUnoBdd.cuposTotales).isEqualTo(25)
    }

    @Test
    fun `una comision conoce sus horarios`() {
        assertThat(comisionUnoBdd.horarios).usingRecursiveComparison().isEqualTo(horariosBdd())
    }

    @Test
    fun `una comision sabe modificar sus horarios`() {
        val nuevosHorarios = listOf(
                Horario(Dia.Mar, LocalTime.of(10, 0), LocalTime.of(12, 0)),
                Horario(Dia.Jue, LocalTime.of(10, 0), LocalTime.of(12, 0)))
        comisionUnoBdd.modificarHorarios(nuevosHorarios)

        assertThat(comisionUnoBdd.horarios).usingRecursiveComparison().isEqualTo(nuevosHorarios)
    }

    @Test
    fun `una comision conoce sus sobrecupos totales`() {
        assertThat(comisionUnoBdd.sobrecuposTotales).isEqualTo(15)
    }

    @Test
    fun `una comision conoce donde se dicta`() {
        assertThat(comisionUnoBdd.locacion).isEqualTo(Locacion.Berazategui)
    }

    @Test
    fun `una comision conoce sus cupos disponibles`() {
        assertThat(comisionUnoBdd.sobrecuposDisponibles()).isEqualTo(sobrecuposTotales)
    }
    @Test
    fun `Una comision sabe su modalidad`() {
        val horarios: List<Horario> = horariosBdd()
        val comisionDosBdd = Comision(bdd, 2, cuatrimestre, horarios.toMutableList(), cuposTotales, sobrecuposTotales, Modalidad.VIRTUAL_SINCRONICA)
        assertThat(comisionDosBdd.modalidad).isEqualTo(Modalidad.VIRTUAL_SINCRONICA)
    }

    @Test
    fun `Cuando una comision asigna un sobrecupo ahora tiene un sobrecupo disponible menos`() {
        val sobrecuposDisponiblesAntes = comisionUnoBdd.sobrecuposDisponibles()
        comisionUnoBdd.asignarSobrecupo()
        assertThat(comisionUnoBdd.sobrecuposDisponibles()).isEqualTo(sobrecuposDisponiblesAntes - 1)
    }

    @Test
    fun `Cuando una comision quita un sobrecupo ahora tiene un sobrecupo disponible mas`() {
        comisionUnoBdd.asignarSobrecupo()
        val sobrecuposDisponiblesAntes = comisionUnoBdd.sobrecuposDisponibles()

        comisionUnoBdd.quitarSobrecupo()

        assertThat(comisionUnoBdd.sobrecuposDisponibles()).isEqualTo(sobrecuposDisponiblesAntes + 1)
    }

    @Test
    fun `No se puede asignar un sobrecupo a una comision que no tiene sobrecupos disponibles`() {
        val comision = Comision(bdd, 2, sobrecuposTotales = 0)
        val exception = assertThrows<ExcepcionUNQUE> { comision.asignarSobrecupo() }

        assertThat(exception.message).isEqualTo("No hay sobrecupos disponibles")
    }

    @Test
    fun `No se puede quitar un sobrecupo a una comision que no tiene sobrecupos ocupados`() {
        val comision = Comision(bdd, 2)
        val exception = assertThrows<ExcepcionUNQUE> { comision.quitarSobrecupo() }

        assertThat(exception.message).isEqualTo("No hay sobrecupos ocupados")
    }

    @Test
    fun `Se puede modificar la cantidad de cupos totales de una comision`() {
        val comision = Comision(bdd, cuposTotales = 30)
        val cuposAntes = comision.cuposTotales

        comision.modificarCuposTotales(20)

        assertThat(comision.cuposTotales).isEqualTo(20)
        assertThat(cuposAntes).isNotEqualTo(comision.cuposTotales)
    }

    @Test
    fun `Se puede modificar la cantidad de sobrecupos totales de una comision`() {
        val comision = Comision(bdd, sobrecuposTotales = 5)
        val sobrecuposAntes = comision.sobrecuposTotales

        comision.modificarSobreuposTotales(2)

        assertThat(comision.sobrecuposTotales).isEqualTo(2)
        assertThat(sobrecuposAntes).isNotEqualTo(comision.sobrecuposTotales)
    }

    @Test
    fun `No se puede asignar menos sobrecupos totales que los ocupados a una comision`() {
        val comision = Comision(bdd, sobrecuposTotales = 2)
        comision.asignarSobrecupo()
        comision.asignarSobrecupo()

        val exception =  assertThrows<ErrorDeNegocio> { comision.modificarSobreuposTotales(1) }

        assertThat(exception.message).isEqualTo("No se puede modificar la cantidad de sobrecupos dado que la cantidad de sobrecupos ocupados es mayor")
    }


    fun horariosBdd(): List<Horario> {
        return listOf(
            Horario(Dia.Lun, LocalTime.of(18, 0), LocalTime.of(21, 0)),
            Horario(Dia.Mie, LocalTime.of(12, 0), LocalTime.of(15, 0))
        )
    }
}
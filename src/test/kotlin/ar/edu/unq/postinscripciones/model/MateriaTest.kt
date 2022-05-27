package ar.edu.unq.postinscripciones.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MateriaTest {
    lateinit var bdd: Materia
    lateinit var correlativa: Materia

    @BeforeEach
    fun `set up`() {
        correlativa = Materia("80005","Elementos de Programación y lógica")
        bdd = Materia("PW-1050", "Bases de Datos", mutableListOf(correlativa), Carrera.LICENCIATURA)
    }

    @Test
    fun `una materia conoce su codigo codigo`() {
        assertThat(bdd.codigo).isEqualTo("PW-1050")
    }

    @Test
    fun `una materia conoce su codigo nombre`() {
        assertThat(bdd.nombre).isEqualTo("Bases de Datos")
    }

    @Test
    fun `una materia tiene correlativas`() {
        assertThat(bdd.correlativas.first()).isEqualTo(correlativa)
    }

    @Test
    fun `se pueden actualizar las correlativas de una materia`() {
        val mate = Materia("MA-102","Matematicas")

        bdd.actualizarCorrelativas(mutableListOf(mate))

        assertThat(bdd.correlativas.first()).isEqualTo(mate)
    }

    @Test
    fun `una materia tiene carrera`() {
        assertThat(bdd.carrera).isEqualTo(Carrera.LICENCIATURA)
    }
}

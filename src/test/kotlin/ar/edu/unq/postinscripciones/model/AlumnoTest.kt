package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

internal class AlumnoTest {
    lateinit var alumno: Alumno
    lateinit var comisionBdd: Comision
    lateinit var otraComision: Comision

    @BeforeEach
    fun `set up`() {
        comisionBdd = Comision()
        otraComision = Comision(numero = 5)
        alumno = Alumno(carrera = Carrera.TPI)
    }

    @Test
    fun `un alumno puede completar un formulario de solicitud de cupo por cuatrimestre`() {
        val otraComision = Comision()
        val formulario = Formulario(comisionBdd.cuatrimestre, mutableListOf())

        alumno.guardarFormulario(formulario)

        assertThat(alumno.yaGuardoUnFormulario(comisionBdd.cuatrimestre)).isTrue
        assertThat(alumno.haSolicitado(otraComision)).isFalse
    }

    @Test
    fun `un alumno no puede tener dos formularios de sobrecupos del mismo cuatrimestre`() {
        val formulario = Formulario(comisionBdd.cuatrimestre)
        alumno.guardarFormulario(formulario)

        val excepcion = assertThrows<ExcepcionUNQUE> { alumno.guardarFormulario(formulario) }

        assertThat(excepcion.message).isEqualTo("Ya has guardado un formulario para este cuatrimestre")
    }

    @Test
    fun `un alumno puede solicitar cupo para mas de una comision`() {
        val formulario = Formulario(
            comisionBdd.cuatrimestre,
            mutableListOf(
                SolicitudSobrecupo(comisionBdd),
                SolicitudSobrecupo(otraComision)
            )
        )

        alumno.guardarFormulario(formulario)

        assertThat(alumno.haSolicitado(comisionBdd)).isTrue
        assertThat(alumno.haSolicitado(otraComision)).isTrue
    }

    @Test
    fun `un alumno conoce su legajo`() {
        alumno = Alumno(dni = 90)

        assertThat(alumno.dni).isEqualTo(90)
    }

    @Test
    fun `un alumno conoce su informacion`() {
        alumno = Alumno(nombre = "fede")

        assertThat(alumno.nombre).isEqualTo("fede")
    }

    @Test
    fun `un alumno conoce su apellido`() {
        alumno = Alumno(apellido = "generico")

        assertThat(alumno.apellido).isEqualTo("generico")
    }

    @Test
    fun `un alumno conoce su correo`() {
        alumno = Alumno(correo = "correo@correo.com")

        assertThat(alumno.correo).isEqualTo("correo@correo.com")
    }

    @Test
    fun `un alumno conoce su contrasenia`() {
        alumno = Alumno(contrasenia = "123")

        assertThat(alumno.contrasenia).isEqualTo("123")
    }

    @Test
    fun `un alumno conoce su dni`() {
        alumno = Alumno(legajo = 123)

        assertThat(alumno.legajo).isEqualTo(123)
    }

    @Test
    fun `Un alumno conoce la carrera en la que se encuentra inscripto`() {
        alumno = Alumno(carrera = Carrera.TPI)

        assertThat(alumno.carrera).isEqualTo(Carrera.TPI)
    }

    @Test
    fun `Un alumno conoce su historia academica`() {
        val intro = Materia("int-102", "Intro", mutableListOf())
        val materiaCursada1 = MateriaCursada(intro)

        alumno.cargarHistoriaAcademica(materiaCursada1)

        assertThat(alumno.historiaAcademica).usingRecursiveComparison().isEqualTo(listOf(materiaCursada1))


    }

    @Test
    fun `La historia academica se encuentra ordenada por fecha de carga descendente`() {
        val intro = Materia("int-102", "Intro", mutableListOf())
        val materiaCursada1 = MateriaCursada(intro, EstadoMateria.PA,LocalDate.of(2021, 7, 20))
        val materiaCursada2 = MateriaCursada(intro, EstadoMateria.PA,LocalDate.of(2021, 12, 20))

        alumno.cargarHistoriaAcademica(materiaCursada1)
        alumno.cargarHistoriaAcademica(materiaCursada2)


        assertThat(alumno.historiaAcademica).usingRecursiveComparison().isEqualTo(listOf(materiaCursada2, materiaCursada1))
    }

    @Test
    fun `Un alumno conoce sus materias aprobadas`() {
        val intro = Materia("int-102", "Intro", mutableListOf())
        val materiaCursada1 = MateriaCursada(intro)
        materiaCursada1.cambiarEstado(EstadoMateria.APROBADO)
        alumno.cargarHistoriaAcademica(materiaCursada1)

        assertThat(alumno.materiasAprobadas()).usingRecursiveComparison().isEqualTo(listOf(materiaCursada1.materia))
    }
}
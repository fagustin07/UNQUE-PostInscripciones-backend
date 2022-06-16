package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
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

    @Test
    fun `Un alumno conoce su coeficiente`() {
        val nuevoCoeficiente = 5.23

        alumno.cambiarCoeficiente(nuevoCoeficiente)

        assertThat(alumno.coeficiente).isEqualTo(nuevoCoeficiente)
    }

    @Test
    fun `A un alumno se le puede modificar su coeficiente`() {
        val coeficienteAntes = alumno.coeficiente
        alumno.cambiarCoeficiente(7.0)

        assertThat(alumno.coeficiente).isEqualTo(7.0)
        assertThat(alumno.coeficiente).isNotEqualTo(coeficienteAntes)
    }

    @Test
    fun `un alumno no puede agregar solicitudes de materias que ya ha aprobado`() {
        val intro = Materia("int-102", "Intro", mutableListOf())
        val materiaCursada1 = MateriaCursada(intro, EstadoMateria.APROBADO)
        val cuatrimestre = Cuatrimestre()
        val comision = Comision(intro, 1, cuatrimestre)
        val formulario = Formulario(cuatrimestre)
        alumno.cargarHistoriaAcademica(materiaCursada1)
        alumno.guardarFormulario(formulario)

        val excepcion = assertThrows<ExcepcionUNQUE> { alumno.agregarSolicitud(comision, cuatrimestre) }

        assertThat(excepcion.message).isEqualTo("El alumno ya ha aprobado la materia ${intro.nombre}")
    }

    @Test
    fun `un alumno no puede agregar solicitudes de materias que se encuentra inscripto por Guarani`() {
        val intro = Materia("int-102", "Intro", mutableListOf())
        val cuatrimestre = Cuatrimestre()
        val comision = Comision(intro, 1, cuatrimestre)
        val formulario = Formulario(cuatrimestre, comisionesInscripto = listOf(comision))
        alumno.guardarFormulario(formulario)

        val excepcion = assertThrows<ExcepcionUNQUE> { alumno.agregarSolicitud(comision, cuatrimestre) }

        assertThat(excepcion.message).isEqualTo("El alumno ya se encuentra inscripto por Guaraní a la materia ${intro.nombre} este cuatrimestre")
    }

    @Test
    fun `un alumno no puede agregar solicitudes de comisiones que ya ha solicitado`() {
        val intro = Materia("int-102", "Intro", mutableListOf())
        val cuatrimestre = Cuatrimestre()
        val comision = Comision(intro, 1, cuatrimestre)
        val formulario = Formulario(cuatrimestre, solicitudes = mutableListOf(SolicitudSobrecupo(comision)))
        alumno.guardarFormulario(formulario)

        val excepcion = assertThrows<ExcepcionUNQUE> { alumno.agregarSolicitud(comision, cuatrimestre) }

        assertThat(excepcion.message).isEqualTo("El alumno ya ha solicitado la comision ${comision.numero} de la materia ${intro.nombre} este cuatrimestre")
    }
}
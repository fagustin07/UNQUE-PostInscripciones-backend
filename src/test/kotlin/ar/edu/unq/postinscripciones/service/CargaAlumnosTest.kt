package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.*
import ar.edu.unq.postinscripciones.model.exception.ErrorDeNegocio
import ar.edu.unq.postinscripciones.service.dto.carga.datos.AlumnoCarga
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Conflicto
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Locacion
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
class CargaAlumnosTest {

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @Autowired
    private lateinit var autenticacionService: AutenticacionService

    @Test
    fun `se puede cargar nuevos alumnos`() {
        val dni = 123456
        alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.P,
                    2010,
                    EstadoInscripcion.Aceptado,
                    Calidad.Activo,
                    Regular.S,
                    Locacion.General_Belgrano,
                    null,
                    123
                )
            )
        )

        val alumnoCargado = alumnoService.buscarAlumno(dni)
        assertThat(alumnoCargado.carrera).isEqualTo(Carrera.P)
        assertThat(alumnoCargado.cursaTPI2010).isTrue
        assertThat(alumnoCargado.estadoInscripcion).isEqualTo(EstadoInscripcion.Aceptado)
        assertThat(alumnoCargado.regular).isEqualTo(Regular.S)
        assertThat(alumnoCargado.calidad).isEqualTo(Calidad.Activo)
        assertThat(alumnoCargado.locacion).isEqualTo(Locacion.General_Belgrano)
    }

    @Test
    fun `se puede actualizar la carrera y regularidad de un alumno`() {
        val dni = 123456
        alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.P,
                    2010,
                    EstadoInscripcion.Aceptado,
                    Calidad.Activo,
                    Regular.S,
                    Locacion.General_Belgrano,
                    null,
                    123
                )
            )
        )

        val resultados = alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.P,
                    2015,
                    EstadoInscripcion.Pendiente,
                    Calidad.Pasivo,
                    Regular.N,
                    Locacion.General_Belgrano,
                    null,
                    125
                )
            )
        )

        val alumnoCargado = alumnoService.buscarAlumno(dni)
        assertThat(alumnoCargado.carrera).isEqualTo(Carrera.P)
        assertThat(alumnoCargado.cursaTPI2010).isFalse
        assertThat(alumnoCargado.estadoInscripcion).isEqualTo(EstadoInscripcion.Pendiente)
        assertThat(alumnoCargado.regular).isEqualTo(Regular.N)
        assertThat(alumnoCargado.calidad).isEqualTo(Calidad.Pasivo)

        assertThat(resultados).hasSize(1)
        assertThat(resultados.first()).usingRecursiveComparison().isEqualTo(Conflicto(125, "El alumno con dni $dni ya existe y se actualizó su información"))
    }


    @Test
    fun `se puede actualizar la carrera de un alumno existente de P a PW`() {
        val dni = 123456
        alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.P,
                    2010,
                    EstadoInscripcion.Aceptado,
                    Calidad.Activo,
                    Regular.S,
                    Locacion.General_Belgrano,
                    null,
                    123
                )
            )
        )

        val resultados = alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.W,
                    2015,
                    EstadoInscripcion.Pendiente,
                    Calidad.Pasivo,
                    Regular.N,
                    Locacion.General_Belgrano,
                    null,
                    125
                )
            )
        )

        val alumnoCargado = alumnoService.buscarAlumno(dni)
        assertThat(alumnoCargado.carrera).isEqualTo(Carrera.PW)
        assertThat(alumnoCargado.cursaTPI2010).isTrue

        assertThat(resultados).hasSize(1)
        assertThat(resultados.first()).usingRecursiveComparison().isEqualTo(Conflicto(125, "El alumno con dni $dni ya existe y se actualizó su información"))
    }

    @Test
    fun `se puede actualizar la carrera de un alumno existente de W a PW cursando tpi2015`() {
        val dni = 123456
        alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.W,
                    2019,
                    EstadoInscripcion.Aceptado,
                    Calidad.Activo,
                    Regular.S,
                    Locacion.General_Belgrano,
                    null,
                    123
                )
            )
        )

        val resultados = alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.P,
                    2015,
                    EstadoInscripcion.Pendiente,
                    Calidad.Pasivo,
                    Regular.N,
                    Locacion.General_Belgrano,
                    null,
                    125
                )
            )
        )

        val alumnoCargado = alumnoService.buscarAlumno(dni)
        assertThat(alumnoCargado.carrera).isEqualTo(Carrera.PW)
        assertThat(alumnoCargado.cursaTPI2010).isFalse

        assertThat(resultados).hasSize(1)
        assertThat(resultados.first()).usingRecursiveComparison().isEqualTo(Conflicto(125, "El alumno con dni $dni ya existe y se actualizó su información"))
    }

    @Test
    fun `se puede actualizar la carrera de un alumno existente de W a PW`() {
        val dni = 123456
        alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.W,
                    2019,
                    EstadoInscripcion.Aceptado,
                    Calidad.Activo,
                    Regular.S,
                    Locacion.General_Belgrano,
                    null,
                    123
                )
            )
        )

        val resultados = alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.P,
                    2010,
                    EstadoInscripcion.Pendiente,
                    Calidad.Pasivo,
                    Regular.N,
                    Locacion.General_Belgrano,
                    null,
                    125
                )
            )
        )

        val alumnoCargado = alumnoService.buscarAlumno(dni)
        assertThat(alumnoCargado.carrera).isEqualTo(Carrera.PW)
        assertThat(alumnoCargado.cursaTPI2010).isTrue

        assertThat(resultados).hasSize(1)
        assertThat(resultados.first()).usingRecursiveComparison().isEqualTo(Conflicto(125, "El alumno con dni $dni ya existe y se actualizó su información"))
    }

    @Test
    fun `un alumno puede pasar de cursar tpi 2010 a tpi 2015`() {
        val dni = 123456
        alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.P,
                    2010,
                    EstadoInscripcion.Aceptado,
                    Calidad.Activo,
                    Regular.S,
                    Locacion.General_Belgrano,
                    null,
                    123
                )
            )
        )

        val alumnoAntesDeSegundaCarga = alumnoService.buscarAlumno(dni)

        alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.P,
                    2015,
                    EstadoInscripcion.Pendiente,
                    Calidad.Pasivo,
                    Regular.N,
                    Locacion.General_Belgrano,
                    null,
                    125
                )
            )
        )

        val alumnoCargado = alumnoService.buscarAlumno(dni)

        assertThat(alumnoCargado.carrera).isEqualTo(Carrera.P)
        assertThat(alumnoAntesDeSegundaCarga.carrera).isEqualTo(Carrera.P)
        assertThat(alumnoAntesDeSegundaCarga.cursaTPI2010).isTrue
        assertThat(alumnoCargado.cursaTPI2010).isFalse
    }

    @Test
    fun `si no se recibe un correo se autogenera uno y se le confirma la cuenta con contrasenia alumno`() {
        val dni = 123456
        alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.P,
                    2010,
                    EstadoInscripcion.Aceptado,
                    Calidad.Activo,
                    Regular.S,
                    Locacion.General_Belgrano,
                    null,
                    123
                )
            )
        )

        val alumnoCargado = alumnoService.buscarAlumno(dni)
        assertThat(alumnoCargado.estadoCuenta).isEqualTo(EstadoCuenta.CONFIRMADA)
        assertThat(alumnoCargado.correo).isEqualTo("${alumnoCargado.dni}_${alumnoCargado.apellido}@alu.unque.edu.ar")
        assertThat(autenticacionService.loguearse(alumnoCargado.dni,"alumno")).isNotBlank
    }

    @Test
    fun `si se recibe un correo no se guarda una cuenta confirmada`() {
        val dni = 123456
        val correo = "micorreo@unq.edu.ar"
        alumnoService.subirAlumnos(
            listOf(
                AlumnoCarga(
                    dni,
                    "Flavio",
                    "Estigarribia",
                    Carrera.P,
                    2010,
                    EstadoInscripcion.Aceptado,
                    Calidad.Activo,
                    Regular.S,
                    Locacion.General_Belgrano,
                    correo,
                    123
                )
            )
        )

        val alumnoCargado = alumnoService.buscarAlumno(dni)

        val excepcion = assertThrows<ErrorDeNegocio> {
            autenticacionService.loguearse(alumnoCargado.dni,"alumno")
        }

        assertThat(alumnoCargado.estadoCuenta).isEqualTo(EstadoCuenta.SIN_CONFIRMAR)
        assertThat(alumnoCargado.correo).isEqualTo(correo)
        assertThat(excepcion.message).isEqualTo("Cree o confirme su cuenta")
    }

    @AfterEach
    fun tearDown() {
        alumnoService.borrarTodos()
    }
}
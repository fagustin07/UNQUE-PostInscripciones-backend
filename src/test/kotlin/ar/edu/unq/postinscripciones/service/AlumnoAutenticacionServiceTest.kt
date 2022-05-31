package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.EstadoCuenta
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.AlumnoDTO
import ar.edu.unq.postinscripciones.service.dto.FormularioCrearAlumno
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@IntegrationTest
internal class AlumnoAutenticacionServiceTest {

    @Autowired
    private lateinit var dataService: DataService

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @Autowired
    private lateinit var alumnoAutenticacionService: AlumnoAutenticacionService

    @Test
    fun `un alumno puede registrarse si fue cargado previamente por algun director y se genera un codigo de confirmacion`() {
        val dni = 12345678
        this.crearAlumno(dni)

        val codigo = alumnoAutenticacionService.crearCuenta(dni, "contrasenia", "contrasenia")

        assertThat(alumnoService.buscarAlumno(dni).codigo).isEqualTo(codigo)
    }

    @Test
    fun `un alumno no puede registrarse si no fue cargado previamente por algun director`() {
        val exception =
            assertThrows<ExcepcionUNQUE> {
                alumnoAutenticacionService.crearCuenta(
                    32165487,
                    "contrasenia",
                    "contrasenia"
                )
            }

        assertThat(exception.message).isEqualTo("No puedes registrarte. Comunicate con el equipo directivo")
    }

    @Test
    fun `se levanta una excepcion si se quiere crear una cuenta con un codigo sin expirar`() {
        val dni = 32165487
        this.crearAlumno(dni)
        alumnoAutenticacionService.crearCuenta(dni, "contrasenia", "contrasenia")

        val exception =
            assertThrows<ExcepcionUNQUE> { alumnoAutenticacionService.crearCuenta(dni, "contrasenia", "contrasenia") }

        assertThat(exception.message).isEqualTo("Usted posee un codigo que no expiró. Revise su correo y confirme su cuenta con el codigo dado")
    }

    @Test
    fun `se puede crear nuevamente una cuenta si el codigo ha expirado`() {
        val dni = 32165487
        this.crearAlumno(dni)
        val primerCodigo = alumnoAutenticacionService.crearCuenta(dni, "contrasenia", "contrasenia")

        val segundoCodigo =
            alumnoAutenticacionService.crearCuenta(dni, "contrasenia", "contrasenia", LocalDateTime.now().plusDays(1))

        assertThat(segundoCodigo).isNotEqualTo(primerCodigo)
        assertThat(alumnoService.buscarAlumno(dni).codigo).isEqualTo(segundoCodigo)
    }

    @Test
    fun `un alumno puede confirmar su cuenta con el codigo dado`() {
        val dni = 32165487
        this.crearAlumno(dni)
        val codigo = alumnoAutenticacionService.crearCuenta(dni, "contrasenia", "contrasenia")

        alumnoAutenticacionService.confirmarCuenta(dni, codigo)

        assertThat(alumnoService.buscarAlumno(dni).estadoCuenta).isEqualTo(EstadoCuenta.CONFIRMADA)
    }

    @Test
    fun `se levanta una excepcion si se quiere confirmar una cuenta con un codigo expirado`() {
        val dni = 32165487
        this.crearAlumno(dni)
        val codigo =
            alumnoAutenticacionService.crearCuenta(dni, "contrasenia", "contrasenia", LocalDateTime.now().minusDays(1))

        val exception = assertThrows<ExcepcionUNQUE> { alumnoAutenticacionService.confirmarCuenta(dni, codigo) }

        assertThat(exception.message).isEqualTo("Su codigo ha expirado. Cree su cuenta nuevamente")
    }

    @Test
    fun `se levanta una excepcion si se quiere confirmar una cuenta con un codigo incorrecto`() {
        val dni = 32165487
        this.crearAlumno(dni)
        val codigo = alumnoAutenticacionService.crearCuenta(dni, "contrasenia", "contrasenia")

        val exception = assertThrows<ExcepcionUNQUE> { alumnoAutenticacionService.confirmarCuenta(dni, codigo - 1) }

        assertThat(exception.message).isEqualTo("Codigo incorrecto. Intente nuevamente")
    }

    @Test
    fun `se levanta una excepcion si se quiere crear una cuenta con una cuenta ya confirmada`() {
        val dni = 32165487
        this.crearAlumno(dni)
        val codigo = alumnoAutenticacionService.crearCuenta(dni, "contrasenia", "contrasenia")

        alumnoAutenticacionService.confirmarCuenta(dni, codigo)

        val exception = assertThrows<ExcepcionUNQUE> {
            alumnoAutenticacionService.crearCuenta(
                dni,
                "contrasenia",
                "contrasenia",
                LocalDateTime.now().plusDays(1)
            )
        }

        assertThat(exception.message).isEqualTo("Ya posees una cuenta")
    }

    @Test
    fun `se levanta una excepcion si se quiere confirmar una cuenta sin haberla creado`() {
        val dni = 32165487
        this.crearAlumno(dni)

        val exception = assertThrows<ExcepcionUNQUE> { alumnoAutenticacionService.confirmarCuenta(dni, 1234567) }

        assertThat(exception.message).isEqualTo("Cree su cuenta. Si el problema persiste, comuniquese con el equipo directivo")
    }

    @Test
    fun `un alumno puede loguearse con su cuenta confirmada`() {
        val dni = 32165487
        this.crearAlumno(dni)
        val password = "contrasenia"
        val codigo = alumnoAutenticacionService.crearCuenta(dni, password, password)
        alumnoAutenticacionService.confirmarCuenta(dni, codigo)

        assertThat(alumnoAutenticacionService.loguearse(dni, password))
            .isEqualTo(AlumnoDTO.desdeModelo(alumnoService.buscarAlumno(dni)))
    }

    @Test
    fun `se levanta una excepcion si un alumno se intenta loguear con una contrasenia incorrecta`() {
        val dni = 32165487
        this.crearAlumno(dni)
        val password = "contrasenia"
        val codigo = alumnoAutenticacionService.crearCuenta(dni, password, password)
        alumnoAutenticacionService.confirmarCuenta(dni, codigo)

        val excepcion = assertThrows<ExcepcionUNQUE> { alumnoAutenticacionService.loguearse(dni, "otropass") }

        assertThat(excepcion.message).isEqualTo("Credenciales invalidas")
    }

    @Test
    fun `se levanta una excepcion si un alumno se intenta loguear con un dni invalido`() {
        val dni = 32165487
        val password = "contrasenia"

        val excepcion = assertThrows<ExcepcionUNQUE> { alumnoAutenticacionService.loguearse(dni, password) }

        assertThat(excepcion.message).isEqualTo("Cree o confirme su cuenta")
    }

    @Test
    fun `se levanta una excepcion si un alumno se intenta loguear con su cuenta sin confirmar`() {
        val dni = 32165487
        this.crearAlumno(dni)
        val password = "contrasenia"
        alumnoAutenticacionService.crearCuenta(dni, password, password)

        val excepcion = assertThrows<ExcepcionUNQUE> { alumnoAutenticacionService.loguearse(dni, password) }

        assertThat(excepcion.message).isEqualTo("Cree o confirme su cuenta")
    }

    private fun crearAlumno(dni: Int) {
        alumnoService.registrarAlumnos(
            listOf(
                FormularioCrearAlumno(
                    dni, "F", "S", "f@correo.com", 12345, Carrera.TPI, listOf()
                )
            )
        )
    }

    @AfterEach
    fun tearDown() {
        dataService.clearDataSet()
    }
}
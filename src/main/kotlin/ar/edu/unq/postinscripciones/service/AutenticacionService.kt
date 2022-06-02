package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Directivo
import ar.edu.unq.postinscripciones.model.EstadoCuenta
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.persistence.AlumnoRepository
import ar.edu.unq.postinscripciones.persistence.DirectivoRepository
import ar.edu.unq.postinscripciones.webservice.config.JWTTokenUtil
import io.swagger.annotations.ApiModelProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class AutenticacionService {

    @Autowired
    lateinit var alumnoRepository: AlumnoRepository

    @Autowired
    lateinit var directivoRepository: DirectivoRepository

    @Autowired
    lateinit var jwtTokenUtil: JWTTokenUtil

    @Transactional
    fun crearCuenta(
        dni: Int,
        contrasenia: String,
        confirmarContrasenia: String,
        carga: LocalDateTime = LocalDateTime.now()
    ): Int {
        val alumno = alumnoRepository.findById(dni)
            .orElseThrow { ExcepcionUNQUE("No puedes registrarte. Comunicate con el equipo directivo") }
        val codigo = (1000000 + Math.random() * 9000000).toInt()
        alumno.actualizarCodigoYContrasenia(codigo, contrasenia, confirmarContrasenia, carga)
        alumnoRepository.save(alumno)
        return codigo
    }

    @Transactional
    fun confirmarCuenta(dni: Int, codigo: Int, carga: LocalDateTime = LocalDateTime.now()) {
        val alumno = alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("No existe el alumno") }
        alumno.confirmarCuenta(codigo, carga)
        alumnoRepository.save(alumno)
    }

    @Transactional
    fun loguearse(dni: Int, contrasenia: String): String {
        val alumno = alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("Cree o confirme su cuenta") }
        if (alumno.estadoCuenta == EstadoCuenta.SIN_CONFIRMAR) throw ExcepcionUNQUE("Cree o confirme su cuenta")

        return if (contrasenia == alumno.contrasenia) {
            jwtTokenUtil.generarTokenAlumno(alumno)
        } else {
            throw credencialesInvalidas()
        }
    }

    @Transactional
    fun crearDirectivo(creacionDirectivo: CreacionDirectivo): Directivo {
        return directivoRepository.save(
            Directivo(
                creacionDirectivo.correo,
                creacionDirectivo.nombre,
                creacionDirectivo.contrasenia
            )
        )
    }

    @Transactional
    fun loguearDirectivo(correoDirectivo: String, contrasenia: String): String {
        val directivo = directivoRepository.findByCorreo(correoDirectivo)
        directivo?.let {
            if (it.contrasenia == contrasenia) {
                return jwtTokenUtil.generarTokenDirectivo(it)
            } else {
                throw credencialesInvalidas()
            }
        }
        throw credencialesInvalidas()
    }

    private fun credencialesInvalidas() = ExcepcionUNQUE("Credenciales invalidas")
}


data class CreacionDirectivo(
    @ApiModelProperty(example = "fla@unque.edu.ar")
    val correo: String,
    @ApiModelProperty(example = "Flavia S")
    val nombre: String,
    @ApiModelProperty(example = "123456")
    val contrasenia: String
)

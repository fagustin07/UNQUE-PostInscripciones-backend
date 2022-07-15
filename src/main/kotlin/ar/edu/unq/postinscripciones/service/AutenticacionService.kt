package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Directivo
import ar.edu.unq.postinscripciones.model.EstadoCuenta
import ar.edu.unq.postinscripciones.model.exception.AlumnoNoEncontrado
import ar.edu.unq.postinscripciones.model.exception.ErrorDeNegocio
import ar.edu.unq.postinscripciones.persistence.AlumnoRepository
import ar.edu.unq.postinscripciones.persistence.DirectivoRepository
import ar.edu.unq.postinscripciones.service.dto.CreacionDirectivo
import ar.edu.unq.postinscripciones.service.dto.alumno.AlumnoCodigo
import ar.edu.unq.postinscripciones.service.dto.alumno.AlumnoDTO
import ar.edu.unq.postinscripciones.webservice.config.security.JWTTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
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

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Transactional
    fun crearCuenta(
        dni: Int,
        contrasenia: String,
        confirmarContrasenia: String,
        carga: LocalDateTime = LocalDateTime.now()
    ): AlumnoCodigo {
        val alumno = alumnoRepository.findById(dni)
            .orElseThrow { ErrorDeNegocio("No puedes registrarte. Comunicate con el equipo directivo") }
        val codigo = (1000000 + Math.random() * 9000000).toInt()
        alumno.actualizarCodigoYContrasenia(codigo, passwordEncoder.encode(contrasenia), carga)
        if (contrasenia != confirmarContrasenia) throw ErrorDeNegocio("Las contrasenias no coinciden")
        alumnoRepository.save(alumno)
        return AlumnoCodigo(AlumnoDTO.desdeModelo(alumno), codigo)
    }

    @Transactional
    fun confirmarCuenta(dni: Int, codigo: Int, carga: LocalDateTime = LocalDateTime.now()) {
        val alumno = alumnoRepository.findById(dni).orElseThrow { AlumnoNoEncontrado(dni) }
        alumno.confirmarCuenta(codigo, carga)
        alumnoRepository.save(alumno)
    }

    @Transactional
    fun loguearse(dni: Int, contrasenia: String): String {
        val alumno = alumnoRepository.findById(dni).orElseThrow { ErrorDeNegocio("Cree o confirme su cuenta") }
        if (alumno.estadoCuenta == EstadoCuenta.SIN_CONFIRMAR) throw ErrorDeNegocio("Cree o confirme su cuenta")

        return if (passwordEncoder.matches(contrasenia, alumno.contrasenia)) {
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
                passwordEncoder.encode(creacionDirectivo.contrasenia)
            )
        )
    }

    @Transactional
    fun loguearDirectivo(correoDirectivo: String, contrasenia: String): String {
        val directivo = directivoRepository.findByCorreo(correoDirectivo)
        directivo?.let {
            if (passwordEncoder.matches(contrasenia, directivo.contrasenia)) {
                return jwtTokenUtil.generarTokenDirectivo(it)
            } else {
                throw credencialesInvalidas()
            }
        }
        throw credencialesInvalidas()
    }

    private fun credencialesInvalidas() = ErrorDeNegocio("Credenciales invalidas")

    @Transactional
    fun borrarTodos() {
        alumnoRepository.deleteAll()
        directivoRepository.deleteAll()
    }
}
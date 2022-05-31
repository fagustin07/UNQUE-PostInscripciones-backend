package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.EstadoCuenta
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.persistence.AlumnoRepository
import ar.edu.unq.postinscripciones.service.dto.AlumnoDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class AlumnoAutenticacionService {

    @Autowired
    lateinit var alumnoRepository: AlumnoRepository

    @Transactional
    fun crearCuenta(
        dni: Int,
        contrasenia: String,
        confirmarContrasenia: String,
        carga: LocalDateTime = LocalDateTime.now()
    ): Int {
        val alumno = alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("No puedes registrarte. Comunicate con el equipo directivo") }
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
    fun loguearse(dni: Int, contrasenia: String): AlumnoDTO {
        val alumno = alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("Cree o confirme su cuenta") }
        if (alumno.estadoCuenta == EstadoCuenta.SIN_CONFIRMAR) throw ExcepcionUNQUE("Cree o confirme su cuenta")

        return if (contrasenia == alumno.contrasenia) {
            AlumnoDTO.desdeModelo(alumno)
        } else {
            throw ExcepcionUNQUE("Credenciales invalidas")
        }
    }
}
package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.service.AutenticacionService
import ar.edu.unq.postinscripciones.service.MailSenderService
import ar.edu.unq.postinscripciones.service.dto.autenticacion.ConfirmacionCuenta
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioRegistro
import ar.edu.unq.postinscripciones.service.dto.autenticacion.LoguearAlumno
import ar.edu.unq.postinscripciones.service.dto.autenticacion.LoguearDirectivo
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@ServiceREST
@RequestMapping("/api/auth")
class AutenticacionController {
    @Autowired
    private lateinit var autenticacionService: AutenticacionService

    @Autowired
    private lateinit var mailSenderService: MailSenderService

    @ApiOperation("Utilizado para que un alumno cree su cuenta")
    @ApiResponses(
        value = [
            ApiResponse(code = 204, message = "Correo de confirmación enviado"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/alumno/registrar"], method = [RequestMethod.POST])
    fun registrarse(@RequestBody formularioRegistro: FormularioRegistro): ResponseEntity<*> {
        val alumnoCodigo = autenticacionService.crearCuenta(
            formularioRegistro.dni,
            formularioRegistro.contrasenia,
            formularioRegistro.confirmacionContrasenia
        )
        mailSenderService.enviarCodigo(alumnoCodigo.alumno, alumnoCodigo.codigo)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null)
    }

    @ApiOperation("Se utiliza para que un alumno confirme su cuenta con el codigo brindado")
    @ApiResponses(
        value = [
            ApiResponse(code = 204, message = "Cuenta confirmada"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/alumno/confirmar"], method = [RequestMethod.POST])
    fun confirmarCuenta(@RequestBody confirmarCuenta: ConfirmacionCuenta): ResponseEntity<Void> {
        autenticacionService.confirmarCuenta(confirmarCuenta.dni, confirmarCuenta.codigo)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @ApiOperation("Utilizado para que un alumno ingrese a la aplicacion")
    @ApiResponses(
        value = [
            ApiResponse(code = 204, message = "OK"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/alumno/login"], method = [RequestMethod.POST])
    fun loguearAlumno(@RequestBody loguearAlumno: LoguearAlumno): ResponseEntity<*> {
        val token = autenticacionService.loguearse(loguearAlumno.dni, loguearAlumno.contrasenia)
        val responseHeaders = HttpHeaders()
        responseHeaders.set("authorization", token)
        responseHeaders.set("rol", "Alumno")
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(responseHeaders).body(null)
    }

    @ApiOperation("Utilizado para que los directivos ingresen a la aplicacion")
    @ApiResponses(
        value = [
            ApiResponse(code = 204, message = "OK"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/directivo/login"], method = [RequestMethod.POST])
    fun loguearDirectora(@RequestBody loguearDirectivo: LoguearDirectivo): ResponseEntity<*> {
        val token = autenticacionService.loguearDirectivo(loguearDirectivo.correo, loguearDirectivo.contrasenia)
        val responseHeaders = HttpHeaders()
        responseHeaders.set("authorization", token)
        responseHeaders.set("rol", "Directivo")
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(responseHeaders).body(null)
    }
}
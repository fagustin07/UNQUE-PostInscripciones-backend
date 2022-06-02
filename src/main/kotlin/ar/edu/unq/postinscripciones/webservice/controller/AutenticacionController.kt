package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.service.AutenticacionService
import ar.edu.unq.postinscripciones.service.dto.ConfirmacionCuenta
import ar.edu.unq.postinscripciones.service.dto.FormularioRegistro
import ar.edu.unq.postinscripciones.service.dto.LoguearAlumno
import ar.edu.unq.postinscripciones.service.dto.LoguearDirectivo
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@ServiceREST
@RequestMapping("/api/auth")
class AutenticacionController {
    @Autowired
    lateinit var autenticacionService: AutenticacionService

    @ApiOperation("Endpoint para que un alumno cree su cuenta")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "OK", response = Int::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/registrar"], method = [RequestMethod.POST])
    fun registrarse(@RequestBody formularioRegistro: FormularioRegistro): ResponseEntity<*> {
        val respuesta = autenticacionService.crearCuenta(
            formularioRegistro.dni,
            formularioRegistro.contrasenia,
            formularioRegistro.confirmacionContrasenia
        )
        return ResponseEntity(respuesta, HttpStatus.CREATED)
    }

    @ApiOperation("Endpoint para que un alumno confirme su cuenta con el codigo brindado")
    @ApiResponses(
        value = [
            ApiResponse(code = 204, message = "Cuenta confirmada"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/confirmar"], method = [RequestMethod.POST])
    fun confirmarCuenta(@RequestBody confirmarCuenta: ConfirmacionCuenta): ResponseEntity<Void> {
        autenticacionService.confirmarCuenta(confirmarCuenta.dni, confirmarCuenta.codigo)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @ApiOperation("Endpoint para que un alumno ingrese a la aplicacion")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = JWTToken::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/login"], method = [RequestMethod.POST])
    fun loguearAlumno(@RequestBody loguearAlumno: LoguearAlumno): ResponseEntity<*> {
        val token = autenticacionService.loguearse(loguearAlumno.dni, loguearAlumno.contrasenia)
        return ResponseEntity(
            JWTToken(token),
            HttpStatus.OK
        )
    }

    @ApiOperation("Endpoint para que un director ingrese a la aplicacion")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = JWTToken::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/directora/login"], method = [RequestMethod.POST])
    fun loguearDirectora(@RequestBody loguearDirectivo: LoguearDirectivo): ResponseEntity<*> {
        val token = autenticacionService.loguearDirectivo(loguearDirectivo.correo, loguearDirectivo.contrasenia)
        return ResponseEntity(
            JWTToken(token),
            HttpStatus.OK
        )
    }


}

data class JWTToken(
    @ApiModelProperty("Bearer .........")
    val token: String
)
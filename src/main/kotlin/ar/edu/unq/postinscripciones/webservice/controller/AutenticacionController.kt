package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.service.AlumnoAutenticacionService
import ar.edu.unq.postinscripciones.service.dto.AlumnoDTO
import ar.edu.unq.postinscripciones.service.dto.ConfirmacionCuenta
import ar.edu.unq.postinscripciones.service.dto.FormularioRegistro
import ar.edu.unq.postinscripciones.service.dto.LoguearAlumno
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
    lateinit var alumnoAutenticacionService: AlumnoAutenticacionService

    @ApiOperation("Endpoint para que un alumno cree su cuenta")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "OK", response = Int::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/alumno/registrar"], method = [RequestMethod.POST])
    fun registrarse(@RequestBody formularioRegistro: FormularioRegistro): ResponseEntity<*> {
        val respuesta = alumnoAutenticacionService.crearCuenta(
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
    @RequestMapping(value = ["/alumno/confirmar"], method = [RequestMethod.POST])
    fun confirmarCuenta(@RequestBody confirmarCuenta: ConfirmacionCuenta): ResponseEntity<Void> {
        alumnoAutenticacionService.confirmarCuenta(confirmarCuenta.dni, confirmarCuenta.codigo)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @ApiOperation("Endpoint para que un alumno ingrese a la aplicacion")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = AlumnoDTO::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/alumno/login"], method = [RequestMethod.POST])
    fun loguearAlumno(@RequestBody loguearAlumno: LoguearAlumno): ResponseEntity<*> {
        return ResponseEntity(
            alumnoAutenticacionService.loguearse(loguearAlumno.dni, loguearAlumno.contrasenia),
            HttpStatus.OK
        )
    }
}

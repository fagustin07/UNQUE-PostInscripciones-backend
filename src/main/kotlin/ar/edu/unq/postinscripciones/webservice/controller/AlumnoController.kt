package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.service.AlumnoService
import ar.edu.unq.postinscripciones.service.CuatrimestreService
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearOActualizarFormulario
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioDTO
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaComision
import ar.edu.unq.postinscripciones.webservice.config.security.JWTTokenUtil
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@ServiceREST
@PreAuthorize("hasRole('ALUMNO')")
@RequestMapping("/api/alumno")
class AlumnoController {

    @Autowired
    private lateinit var cuatrimestreService: CuatrimestreService

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @Autowired
    private lateinit var jwtTokenUtil: JWTTokenUtil

    @ApiOperation("Realiza carga de una solicitud de comisiones para el alumno.")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Solicitudes cargadas correctamente", response = FormularioDTO::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/solicitudes"], method = [RequestMethod.POST])
    fun cargarSolicitudes(
        @ApiParam(hidden=true)
        @RequestHeader("Authorization")
        token: String,
        @ApiParam(value = "Lista de id de comisiones solicitadas y de comisiones en las que el alumno se encuentra inscripto.", required = true)
        @RequestBody formulario: FormularioCrearOActualizarFormulario,
    ): ResponseEntity<*> {
        val dni = jwtTokenUtil.obtenerDni(token)
        return ResponseEntity(
            alumnoService.guardarSolicitudPara(dni, formulario.comisiones, comisionesInscriptoIds = formulario.comisionesInscripto),
            HttpStatus.OK
        )
    }

    @ApiOperation("Actualiza las comisiones que solicita el alumno.")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = FormularioDTO::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/solicitudes"], method = [RequestMethod.PATCH])
    fun actualizarFormulario(
        @ApiParam(hidden=true)
        @RequestHeader("Authorization")
        token: String,
        @ApiParam(value = "Lista de id de comisiones solicitadas y de comisiones en las que el alumno se encuentra inscripto.", required = true)
        @RequestBody formulario: FormularioCrearOActualizarFormulario,
    ): ResponseEntity<*> {
        val dni = jwtTokenUtil.obtenerDni(token)
        return ResponseEntity(
            alumnoService.actualizarFormulario(dni, formulario.comisiones, comisionesInscriptoIds = formulario.comisionesInscripto),
            HttpStatus.OK
        )
    }

    @ApiOperation("Obtiene las comisiones de las materias que puede cursar")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = MateriaComision::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/materias"], method = [RequestMethod.GET])
    fun materiasDisponibles(
        @ApiParam(hidden=true)
        @RequestHeader("Authorization")
        token: String,
    ): ResponseEntity<*> {
        val dni = jwtTokenUtil.obtenerDni(token)
        return ResponseEntity(
            alumnoService.materiasDisponibles(dni),
            HttpStatus.OK
        )
    }

    @ApiOperation("El alumno obtiene el formulario de sobrecupos del cuatrimestre actual")
    @ApiResponses(
            value = [
                ApiResponse(code = 200, message = "OK", response = FormularioDTO::class),
                ApiResponse(code = 400, message = "Algo salio mal")
            ]
    )
    @RequestMapping(value = ["/formulario"], method = [RequestMethod.GET])
    fun obtenerFormulario(@ApiParam(hidden=true) @RequestHeader("Authorization") token: String): ResponseEntity<*> {
        return ResponseEntity(
                alumnoService.obtenerFormulario(token),
                HttpStatus.OK
        )
    }

    @ApiOperation("Retorna informacion del cuatrimestre actual")
    @RequestMapping(value = ["/cuatrimestre"], method = [RequestMethod.GET])
    fun cuatrimestre(): ResponseEntity<*> {
        return ResponseEntity(
            cuatrimestreService.obtener(),
            HttpStatus.OK
        )
    }

    @ApiOperation("Endpoint para borrar el formulario de un alumno dado un cuatrimestre")
    @ApiResponses(
            value = [
                ApiResponse(code = 200, message = "OK"),
                ApiResponse(code = 400, message = "Algo salio mal")
            ]
    )
    @RequestMapping(value = ["/formulario"], method = [RequestMethod.DELETE])
    fun borrarFormulario(
            @ApiParam(hidden=true)
            @RequestHeader("Authorization") token: String

    ): ResponseEntity<Void> {
        alumnoService.borrarFormulario(jwt = token)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }



}



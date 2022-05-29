package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.model.EstadoSolicitud
import ar.edu.unq.postinscripciones.service.AlumnoService
import ar.edu.unq.postinscripciones.service.dto.ConflictoAlumnoDTO
import ar.edu.unq.postinscripciones.service.dto.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.FormularioDTO
import ar.edu.unq.postinscripciones.service.dto.SolicitudSobrecupoDTO
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@ServiceREST
@RequestMapping("/api/admin")
class AdminController {

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @ApiOperation("Endpoint que se usa para agregar una solicitud a un formulario de un alumno")
    @ApiResponses(
            value = [
                ApiResponse(code = 200, message = "OK", response = FormularioDTO::class),
                ApiResponse(code = 400, message = "Algo salio mal")
            ]
    )
    @RequestMapping(value = ["alumno/{dni}/formulario"], method = [RequestMethod.PUT])
    fun agregarSolicitud(
            @ApiParam(value = "Dni del alumno para cargar solicitudes", example = "12345678", required = true)
            @PathVariable dni: Int,
            @ApiParam(value = "Id de comision para agregar", example = "1",  required = true)
            @RequestParam idComision: Long
    ): ResponseEntity<*> {
        return ResponseEntity(
                alumnoService.agregarSolicitud(dni, idComision),
                HttpStatus.OK
        )
    }

    @ApiOperation("Endpoint que se usa para aprobar o rechazar una solicitud de un alumno")
    @ApiResponses(
            value = [
                ApiResponse(code = 200, message = "Solicitud modificada", response = SolicitudSobrecupoDTO::class),
                ApiResponse(code = 400, message = "Algo salio mal")
            ]
    )
    @RequestMapping(value = ["/alumno/solicitudes/{id}"], method = [RequestMethod.PUT])
    fun cambiarEstadoSolicitud(
            @ApiParam(value = "Id de la Solicitud", example = "1", required = true)
            @PathVariable
            id: Long,
            @ApiParam(value = "Estado a cambiar en la solicitud", example = "APROBADO", required = true)
            @RequestParam
            estado: EstadoSolicitud
    ): ResponseEntity<*> {
        return ResponseEntity(
                alumnoService.cambiarEstado(id, estado),
                HttpStatus.OK
        )
    }

}
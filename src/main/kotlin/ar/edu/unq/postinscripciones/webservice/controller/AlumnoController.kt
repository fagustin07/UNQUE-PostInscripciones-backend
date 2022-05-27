package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.model.EstadoSolicitud
import ar.edu.unq.postinscripciones.service.AlumnoService
import ar.edu.unq.postinscripciones.service.dto.*
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ServiceREST
@RequestMapping("/api/alumnos")
class AlumnoController {

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @ApiOperation("Endpoint que se usa para registrar una lista de alumnos en el sistema")
    @RequestMapping(value = [""], method = [RequestMethod.POST])
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "OK", response = ConflictoAlumnoDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    fun registrarAlumnos(@RequestBody planillaAlumnos: List<FormularioCrearAlumno>): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.registrarAlumnos(planillaAlumnos),
            HttpStatus.CREATED
        )
    }

    @ApiOperation("Endpoint que se usa para actualizar la historia academica de un alumno registrado en el sistema")
    @RequestMapping(value = ["/historia-academica/{alumnoDni}"], method = [RequestMethod.PUT])
    @ApiResponses(
            value = [
                ApiResponse(code = 200, message = "OK", response = AlumnoDTO::class, responseContainer = "List"),
                ApiResponse(code = 400, message = "Algo salio mal")
            ]
    )
    fun actualizarHistoriaAcademica(
            @ApiParam(value = "Dni del alumno para cargar historia academica", example = "12345677", required = true)
            @PathVariable alumnoDni: Int,
            @RequestBody historiaAcademica: List<MateriaCursadaDTO>
    ): ResponseEntity<*> {
        return ResponseEntity(
                alumnoService.actualizarHistoriaAcademica(alumnoDni, historiaAcademica),
                HttpStatus.CREATED
        )
    }

    @ApiOperation("Endpoint que se usa para cargar una solicitud de comisiones a un alumno.")
    @RequestMapping(value = ["/solicitudes/{dni}"], method = [RequestMethod.POST])
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Solicitudes cargadas correctamente", response = FormularioDTO::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    fun cargarSolicitudes(
        @ApiParam(value = "Dni del alumno para cargar solicitudes", example = "12345678", required = true)
        @PathVariable dni: Int,
        @ApiParam(value = "Lista de id de comisiones solicitadas. Ejemplo: [1,2]", required = true)
        @RequestBody comisiones: List<Long>
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.guardarSolicitudPara(dni, comisiones),
            HttpStatus.OK
        )
    }

    @ApiOperation("Endpoint que se usa para aprobar o rechazar una solicitud de un alumno")
    @RequestMapping(value = ["/solicitudes/{id}"], method = [RequestMethod.PUT])
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Solicitud modificada", response = SolicitudSobrecupoDTO::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
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

    @ApiOperation("Endpoint que se usa para obtener las materias que puede cursar un alumno")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = MateriaComision::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/materias/{dni}"], method = [RequestMethod.GET])
    fun materiasDisponibles(
        @ApiParam(value = "Dni del alumno", example = "12345678", required = true)
        @PathVariable
        dni: Int,
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.materiasDisponibles(dni),
            HttpStatus.OK
        )
    }

    @ApiOperation("#### Endpoint que se usa para obtener el formulario y un resumen de la historia academica del alumno dado ####")
    @RequestMapping(value = ["/{dni}"], method = [RequestMethod.GET])
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = ResumenAlumno::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    fun resumenAlumno(
        @ApiParam(value = "Dni del alumno", example = "12345677", required = true)
        @PathVariable
        dni: Int,
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.obtenerResumenAlumno(dni),
            HttpStatus.OK
        )
    }
}


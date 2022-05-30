package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.service.AlumnoService
import ar.edu.unq.postinscripciones.service.dto.*
import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ServiceREST
@RequestMapping("/api/alumno")
class AlumnoController {

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @ApiOperation("Endpoint que se usa para registrar una lista de alumnos en el sistema")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "OK", response = ConflictoAlumnoDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = [""], method = [RequestMethod.POST])
    fun registrarAlumnos(@RequestBody planillaAlumnos: List<FormularioCrearAlumno>): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.registrarAlumnos(planillaAlumnos),
            HttpStatus.CREATED
        )
    }

    @ApiOperation("Endpoint que se usa para actualizar la historia academica de un alumno registrado en el sistema")
    @ApiResponses(
            value = [
                ApiResponse(code = 200, message = "OK", response = AlumnoDTO::class, responseContainer = "List"),
                ApiResponse(code = 400, message = "Algo salio mal")
            ]
    )
    @RequestMapping(value = ["/{dni}/historia-academica"], method = [RequestMethod.PUT])
    fun actualizarHistoriaAcademica(
            @ApiParam(value = "Dni del alumno para cargar historia academica", example = "12345677", required = true)
            @PathVariable dni: Int,
            @RequestBody historiaAcademica: List<MateriaCursadaDTO>
    ): ResponseEntity<*> {
        return ResponseEntity(
                alumnoService.actualizarHistoriaAcademica(dni, historiaAcademica),
                HttpStatus.CREATED
        )
    }

    @ApiOperation("Endpoint que se usa para cargar una solicitud de comisiones a un alumno.")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Solicitudes cargadas correctamente", response = FormularioDTO::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/{dni}/solicitudes"], method = [RequestMethod.POST])
    fun cargarSolicitudes(
        @ApiParam(value = "Dni del alumno para cargar solicitudes", example = "12345678", required = true)
        @PathVariable dni: Int,
        @ApiParam(value = "Lista de id de comisiones solicitadas y de comisiones en las que el alumno se encuentra inscripto.", required = true)
        @RequestBody formulario: FormularioCrearOActualizarFormulario,
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.guardarFormulario(dni, formulario.comisiones, comisionesInscriptoIds = formulario.comisionesInscripto),
            HttpStatus.OK
        )
    }

    @ApiOperation("Endpoint que se usa para actualizar las solicitudes de un alumno.")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = FormularioDTO::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/{dni}/solicitudes"], method = [RequestMethod.PUT])
    fun actualizarFormulario(
        @ApiParam(value = "Dni del alumno", example = "12345677", required = true)
        @PathVariable dni: Int,
        @ApiParam(value = "Lista de id de comisiones solicitadas y de comisiones en las que el alumno se encuentra inscripto.", required = true)
        @RequestBody formulario: FormularioCrearOActualizarFormulario,
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.guardarFormulario(dni, formulario.comisiones, comisionesInscriptoIds = formulario.comisionesInscripto),
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
    @RequestMapping(value = ["/{dni}/materias"], method = [RequestMethod.GET])
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
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = ResumenAlumno::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/{dni}"], method = [RequestMethod.GET])
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



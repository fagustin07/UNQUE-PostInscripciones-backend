package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.model.EstadoSolicitud
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.service.AlumnoService
import ar.edu.unq.postinscripciones.service.ComisionService
import ar.edu.unq.postinscripciones.service.CuatrimestreService
import ar.edu.unq.postinscripciones.service.MateriaService
import ar.edu.unq.postinscripciones.service.dto.*
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*


@ServiceREST
@PreAuthorize("hasRole('DIRECTIVO')")
@RequestMapping("/api")
class DirectivoController {

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @Autowired
    private lateinit var comisionService: ComisionService

    @Autowired
    private lateinit var cuatrimestreService: CuatrimestreService

    @Autowired
    private lateinit var materiaService: MateriaService

    //   CONTROLADOR ALUMNOS

    @ApiOperation("Endpoint que se usa para registrar una lista de alumnos en el sistema")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "OK", response = ConflictoAlumnoDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/alumnos"], method = [RequestMethod.POST])
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
    @RequestMapping(value = ["/alumnos/{dni}/historia-academica"], method = [RequestMethod.PATCH])
    fun actualizarHistoriaAcademica(
        @ApiParam(value = "Dni del alumno para cargar historia academica", example = "12345677", required = true)
        @PathVariable dni: Int,
        @RequestBody historiaAcademica: List<MateriaCursadaDTO>
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.actualizarHistoriaAcademica(dni, historiaAcademica),
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
    @RequestMapping(value = ["/alumnos/{dni}"], method = [RequestMethod.GET])
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

    @ApiOperation("Endpoint que se usa para agregar una solicitud de comision  al  formulario de un alumno")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = FormularioDTO::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/alumnos/{dni}/formulario"], method = [RequestMethod.PATCH])
    fun agregarSolicitud(
        @ApiParam(value = "Dni del alumno para cargar solicitudes", example = "12345678", required = true)
        @PathVariable dni: Int,
        @ApiParam(value = "Id de comision para agregar", example = "1", required = true)
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
    @RequestMapping(value = ["/alumnos/{dni}/solicitudes/{id}"], method = [RequestMethod.PATCH])
    fun cambiarEstadoSolicitud(
        @ApiParam(value = "dni del alumno", example = "12345677", required = true)
        @PathVariable
        dni: Int,
        @ApiParam(value = "Id de la Solicitud", example = "1", required = true)
        @PathVariable
        id: Long,
        @ApiParam(value = "Estado a cambiar en la solicitud", example = "APROBADO", required = true)
        @RequestParam
        estado: EstadoSolicitud,
        @ApiParam(value = "Id del formulario", example = "1", required = true)
        @RequestParam
        formularioId: Long
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.cambiarEstadoSolicitud(id, estado, formularioId),
            HttpStatus.OK
        )
    }

    @ApiOperation("Endpoint que se usa para cerrar un formulario")
    @ApiResponses(
            value = [
                ApiResponse(code = 200, message = "Formulario Cerrado", response = FormularioDTO::class),
                ApiResponse(code = 400, message = "Algo salio mal")
            ]
    )
    @RequestMapping(value = ["/formulario/{id}/cerrar"], method = [RequestMethod.PATCH])
    fun cerrarFormulario(
            @ApiParam(value = "Id del formulario", example = "1", required = true)
            @PathVariable
            id: Long,
            @ApiParam(value = "Dni del alumno", example = "12345677", required = true)
            @RequestParam
            dni: Int
    ): ResponseEntity<*> {
        return ResponseEntity(
                alumnoService.cerrarFormulario(id, dni),
                HttpStatus.OK
        )
    }

//    CONTROLADOR COMISIONES

    @ApiOperation("Endpoint que se usa para registrar nuevas comisiones en el sistema o bien actualizar las fechas para recibir formularios de sobrecupos.")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = ConflictoComision::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/comisiones"], method = [RequestMethod.POST])
    fun actualizarOfertaAcademica(
        @RequestBody oferta: OfertaAcademicaDTO,
    ): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.actualizarOfertaAcademica(
                oferta.comisionesACargar,
                oferta.inicioInscripciones,
                oferta.finInscripciones
            ),
            HttpStatus.OK
        )
    }

    @ApiOperation("##### Endpoint que se usa para obtener los alumnos que solicitaron una comision, junto con el id del formulario y la solicitud #####")
    @ApiResponses(
        value = [
            ApiResponse(
                code = 200,
                message = "OK",
                response = AlumnoSolicitaComision::class,
                responseContainer = "List"
            ),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/comisiones/{id}/solicitantes"], method = [RequestMethod.GET])
    fun alumnosQueSolicitaron(
        @ApiParam(value = "Id de la comision", example = "1", required = true)
        @PathVariable
        id: Long,
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.alumnosQueSolicitaron(id),
            HttpStatus.OK
        )
    }

    @ApiOperation("##### Endpoint que se usa para obtener los alumnos que solicitaron una materia, junto con el id del formulario y la solicitud #####")
    @ApiResponses(
        value = [
            ApiResponse(
                code = 200,
                message = "OK",
                response = AlumnoSolicitaComision::class,
                responseContainer = "List"
            ),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/materia/{id}/solicitantes"], method = [RequestMethod.GET])
    fun alumnosQueSolicitaronMateria(
        @ApiParam(value = "Id de la materia", example = "80000", required = true)
        @PathVariable
        id: String,
        @ApiParam(value = "id de la comision", example = "1", required = false)
        @RequestParam
        comision: Long?
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.alumnosQueSolicitaron(id, comision),
            HttpStatus.OK
        )
    }

    @ApiOperation("Endpoint que se usa para modificar los horarios de una comision ya creada")
    @ApiResponses(
        value = [
            ApiResponse(
                code = 200,
                message = "OK",
                response = ComisionDTO::class,
                responseContainer = "List"
            ),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/comisiones/{id}/horarios"], method = [RequestMethod.PATCH])
    fun modificarHorarios(
        @ApiParam(value = "Id de la comision", example = "1", required = true)
        @PathVariable
        id: Long,
        @RequestBody
        nuevosHorarios: List<HorarioDTO>
    ): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.modificarHorarios(id, nuevosHorarios),
            HttpStatus.OK
        )
    }

//    CONTROLADOR MATERIAS

    @ApiOperation("Endpoint que se usa para registra nuevas  materias en el sistema")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "Materia creada", response = MateriaDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/materias"], method = [RequestMethod.POST])
    fun registrarMaterias(@RequestBody formulariosMaterias: List<FormularioMateria>): ResponseEntity<*> {
        return ResponseEntity(
            materiaService.crear(formulariosMaterias),
            HttpStatus.CREATED
        )
    }

    @ApiOperation(value = "##### Endpoint usado para listar todas las materias de un cuatrimestre ordenadas por cantidad de solicitudes #####")
    @ApiResponses(
            value = [
                ApiResponse(
                        code = 200,
                        message = "OK",
                        response = MateriaPorSolicitudes::class,
                        responseContainer = "List"
                ),
                ApiResponse(code = 400, message = "Algo salio mal")
            ]
    )
    @RequestMapping(value = ["/materias/solicitudes"], method = [RequestMethod.GET])
    fun materiaSolicitudes(
            @ApiParam(value = "Anio del cuatrimestre", example = "2022", required = true)
            @RequestParam
            anio: Int,
            @ApiParam(value = "Semestre del cuatrimestre", example = "S1", required = true)
            @RequestParam
            semestre: Semestre
    ): ResponseEntity<*> {
        return ResponseEntity(
                materiaService.materiasPorSolicitudes(),
                HttpStatus.OK
        )
    }

    @ApiOperation(value = "Endpoint usado para listar todas las materias disponibles")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = MateriaDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/materias"], method = [RequestMethod.GET])
    fun todas(): ResponseEntity<*> {
        return ResponseEntity(
            materiaService.todas(),
            HttpStatus.OK
        )
    }

    @ApiOperation(value = "Endpoint usado para actualizar las materias correlativas de una materia registrada")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = MateriaDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/materias/{codigo}/correlativas"], method = [RequestMethod.PATCH])
    fun actualizarCorrelativas(
        @PathVariable
        @ApiParam(value = "Codigo de materia", example = "80000", required = true)
        codigo: String,
        @RequestBody
        @ApiParam(value = "Lista de codigos de materias. Ejemplo: [80005, 01032]", required = true)
        correlativas: List<String>
    ): ResponseEntity<*> {
        return ResponseEntity(
            materiaService.actualizarCorrelativas(codigo, correlativas),
            HttpStatus.OK
        )
    }

    @ApiOperation(value = "Endpoint usado para listar todas las comisiones del cuatrimestre actual de una materia especifica")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = ComisionDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/materias/{codigo}/comision"], method = [RequestMethod.GET])
    fun materiasComision(
        @PathVariable
        @ApiParam(value = "Codigo de la materia", example = "01035", required = true)
        codigo: String
    ): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.obtenerComisionesMateria(codigo),
            HttpStatus.OK
        )
    }

//    CONTROLADOR CUATRIMESTRES

    @ApiOperation("Endpoint que se usa para obtener informacion basica de un cuatrimestre")
    @RequestMapping(value = ["/cuatrimestres"], method = [RequestMethod.GET])
    fun cuatrimestre(
        @ApiParam(value = "Anio del cuatrimestre", example = "2022", required = true)
        @RequestParam
        anio: Int,
        @ApiParam(value = "Semestre del cuatrimestre", example = "S1", required = true)
        @RequestParam
        semestre: Semestre
    ): ResponseEntity<*> {
        return ResponseEntity(
            cuatrimestreService.obtener(Cuatrimestre(anio, semestre)),
            HttpStatus.OK
        )
    }

    @ApiOperation("Endpoint que se usa para obtener la oferta academica de un cuatrimestre")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = ComisionDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/cuatrimestres/oferta"], method = [RequestMethod.GET])
    fun ofertaAcademica(
        @ApiParam(value = "Anio del cuatrimestre", example = "2022", required = true)
        @RequestParam
        anio: Int,
        @ApiParam(value = "Semestre del cuatrimestre", example = "S1", required = true)
        @RequestParam
        semestre: Semestre
    ): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.ofertaDelCuatrimestre(Cuatrimestre(anio, semestre)),
            HttpStatus.OK
        )
    }

}
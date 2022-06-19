package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.model.EstadoSolicitud
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.service.AlumnoService
import ar.edu.unq.postinscripciones.service.ComisionService
import ar.edu.unq.postinscripciones.service.CuatrimestreService
import ar.edu.unq.postinscripciones.service.MateriaService
import ar.edu.unq.postinscripciones.service.dto.alumno.*
import ar.edu.unq.postinscripciones.service.dto.comision.ComisionConHorarios
import ar.edu.unq.postinscripciones.service.dto.comision.ComisionDTO
import ar.edu.unq.postinscripciones.service.dto.comision.ConflictoComision
import ar.edu.unq.postinscripciones.service.dto.cuatrimestre.OfertaAcademicaDTO
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioDTO
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioMateria
import ar.edu.unq.postinscripciones.service.dto.formulario.SolicitudSobrecupoDTO
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaConCorrelativas
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaDTO
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaPorSolicitudes
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

    @ApiOperation("Registrar datos basicos de alumnos en el sistema")
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

    @ApiOperation("Actualizar la historia academica de alumnos existentes en el sistema")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = AlumnoDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/alumnos/historia-academica"], method = [RequestMethod.PATCH])
    fun actualizarHistoriaAcademica(
        @RequestBody alumnosConHistoriaAcademica: List<AlumnoConHistoriaAcademica>
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.actualizarHistoriaAcademica(alumnosConHistoriaAcademica),
            HttpStatus.OK
        )
    }

    @ApiOperation("#### Retorna el formulario actual y un resumen de la historia academica del alumno dado ####")
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

    @ApiOperation("Agrega una solicitud de comision  al  formulario de un alumno")
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

    @ApiOperation("Aprobar, rechazar o poner pendiente  una solicitud de sobrecupo")
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

    @ApiOperation("Cierra un formulario de sobrecupo")
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

    @ApiOperation("Cierra todos los formularios de sobrecupo")
    @ApiResponses(
            value = [
                ApiResponse(code = 200, message = "Formularios Cerrados"),
                ApiResponse(code = 400, message = "Algo salio mal")
            ]
    )
    @RequestMapping(value = ["/formulario/cerrar"], method = [RequestMethod.PATCH])
    fun cerrarFormularios(): ResponseEntity<*> {
        alumnoService.cerrarFormularios()
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null)
    }

    @ApiOperation("Listado de alumnos con detalle de formulario filtrado por dni")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok", response = AlumnoFormulario::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/alumnos/formulario"], method = [RequestMethod.GET])
    fun alumnosPorNombreOApellido(
        @ApiParam(value = "dni del alumno", example = "12345678", required = false)
        @RequestParam dni: Int?,
        @ApiParam(value = "booleano para filtrar alumnos sin procesar", example = "true", required = false)
        @RequestParam sinProcesar: Boolean?,
        @ApiParam(value = "booleano para filtrar alumnos con solicitudes pendientes", example = "true", required = false)
        @RequestParam pendiente: Boolean?
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.alumnosPorDni(dni, sinProcesar, pendiente),
            HttpStatus.OK
        )
    }

    @ApiOperation("Listado de alumnos con datos basicos, filtrarlos por comienzo de dni, ordenados por coeficiente")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok", response = AlumnoDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/alumnos"], method = [RequestMethod.GET])
    fun alumnos(
        @ApiParam(value = "patron del dni", example = "1234567", required = false)
        @RequestParam dni: String?
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.todos(dni ?: ""),
            HttpStatus.OK
        )
    }

//    CONTROLADOR COMISIONES

    @ApiOperation("Registra nuevas comisiones a la oferta academica y actualiza los plazos del periodo de inscripciones.")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = ConflictoComision::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/comisiones/oferta"], method = [RequestMethod.POST])
    fun actualizarOfertaAcademica(
        @RequestBody oferta: OfertaAcademicaDTO,
    ): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.actualizarOfertaAcademica(
                oferta.comisionesACargar ?: listOf(),
                oferta.inicioInscripciones,
                oferta.finInscripciones
            ),
            HttpStatus.OK
        )
    }

    @ApiOperation("Retorna los alumnos que solicitaron una comision")
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
            alumnoService.alumnosQueSolicitaronComision(id),
            HttpStatus.OK
        )
    }

    @ApiOperation("##### Retorna los alumnos que solicitaron sobrecupo en alguna comision de la materia, ordenados por coeficiente #####")
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
    @RequestMapping(value = ["/materia/{codigo}/solicitantes"], method = [RequestMethod.GET])
    fun alumnosQueSolicitaronMateria(
        @ApiParam(value = "codigo de la materia", example = "01035", required = true)
        @PathVariable
        codigo: String,
        @ApiParam(value = "numero de la comision para filtrar", example = "1", required = false)
        @RequestParam
        numero: Int?,
        @ApiParam(value = "estado de la solicitud para filtrar", example = "true", required = false)
        @RequestParam
        pendiente: Boolean?
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.alumnosQueSolicitaronMateria(codigo, numero, pendiente),
            HttpStatus.OK
        )
    }

    @ApiOperation("Actualiza los horarios de comisiones existentes")
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
    @RequestMapping(value = ["/comisiones/horarios"], method = [RequestMethod.PATCH])
    fun modificarHorarios(
        @RequestBody
        comisionesConHorarios: List<ComisionConHorarios>
    ): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.modificarHorarios(comisionesConHorarios),
            HttpStatus.OK
        )
    }

//    CONTROLADOR MATERIAS

    @ApiOperation("Registra nuevas  materias en el sistema")
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

    @ApiOperation(value = "##### Lista todas las materias de un cuatrimestre ordenadas por cantidad de solicitudes #####")
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

    @ApiOperation(value = "Lista todas las materias disponibles")
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

    @ApiOperation(value = "Actualiza las materias correlativas de materias registradas")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = MateriaDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/materias/correlativas"], method = [RequestMethod.PATCH])
    fun actualizarCorrelativas(
        @RequestBody
        @ApiParam(value = "lista de tuplas de (nombre de la materia, lista de nombres de sus correlativas), case insensitive", required = true)
        materiasConCorrelativas: List<MateriaConCorrelativas>
    ): ResponseEntity<*> {
        return ResponseEntity(
            materiaService.actualizarCorrelativas(materiasConCorrelativas),
            HttpStatus.OK
        )
    }

    @ApiOperation(value = "Retorna todas las comisiones del cuatrimestre actual de una materia especifica")
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

    @ApiOperation(value = "Rechaza todas las solicitudes pendientes de una materia especifica o una materia y comision especifica")
    @ApiResponses(
            value = [
                ApiResponse(code = 204, message = "NO CONTENT"),
                ApiResponse(code = 400, message = "Algo salio mal")
            ]
    )
    @RequestMapping(value = ["/materias/{codigo}/solicitudes/rechazar"], method = [RequestMethod.PATCH])
    fun rechazarSolicitudesMateria(
            @PathVariable
            @ApiParam(value = "Codigo de la materia", example = "01035", required = true)
            codigo: String,
            @ApiParam(value = "Numero de comision", example = "1", required = false)
            @RequestParam
            numero: Int?
    ): ResponseEntity<*> {
        alumnoService.rechazarSolicitudesPendientesMateria(codigo, numero)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null)
    }

//    CONTROLADOR CUATRIMESTRES

    @ApiOperation("Retorna informacion basica de un cuatrimestre")
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

    @ApiOperation("Retorna la oferta academica de un cuatrimestre por busqueda del patron de nombre de materia dado")
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
        semestre: Semestre,
        @ApiParam(value = "Patron de nombre de materia deseado", example = "DAT", required = false)
        @RequestParam
        nombre: String?
    ): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.ofertaDelCuatrimestre(nombre ?: "", cuatrimestre = Cuatrimestre(anio, semestre)),
            HttpStatus.OK
        )
    }

}
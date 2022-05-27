package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.service.AlumnoService
import ar.edu.unq.postinscripciones.service.ComisionService
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
@RequestMapping("/api/comision")
class ComisionController {
    @Autowired
    private lateinit var comisionService: ComisionService

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @ApiOperation("Endpoint que se usa para registrar nuevas comisiones en el sistema o bien actualizar las fechas para recibir formularios de sobrecupos.")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = ConflictoComision::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = [""], method = [RequestMethod.POST])
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

    @ApiOperation(value = "Endpoint usado para listar todas las comisiones del cuatrimestre actual de una materia especifica")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = ComisionDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = ["/materia/{codigoMateria}"], method = [RequestMethod.GET])
    fun materiasComision(
        @PathVariable
        @ApiParam(value = "Codigo de la materia", example = "01035", required = true)
        codigoMateria: String
    ): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.obtenerComisionesMateria(codigoMateria),
            HttpStatus.OK
        )
    }

    @ApiOperation(value = "##### Endpoint usado para listar todas las comisiones de un cuatrimestre ordenadas por cantidad de solicitudes #####")
    @RequestMapping(value = [""], method = [RequestMethod.GET])
    @ApiResponses(
        value = [
            ApiResponse(
                code = 200,
                message = "OK",
                response = ComisionPorSolicitudes::class,
                responseContainer = "List"
            ),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    fun comisionSolicitudes(
        @ApiParam(value = "Anio del cuatrimestre", example = "2022", required = true)
        @RequestParam
        anio: Int,
        @ApiParam(value = "Semestre del cuatrimestre", example = "S1", required = true)
        @RequestParam
        semestre: Semestre
    ): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.comisionesPorSolicitudes(),
            HttpStatus.OK
        )
    }

    @ApiOperation("##### Endpoint que se usa para obtener los alumnos que solicitaron una comision, junto con el id del formulario y la solicitud #####")
    @RequestMapping(value = ["/solicitantes"], method = [RequestMethod.GET])
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
    fun alumnosQueSolicitaron(
        @ApiParam(value = "Id de la comision", example = "1", required = true)
        @RequestParam
        comisionId: Long,
    ): ResponseEntity<*> {
        return ResponseEntity(
            alumnoService.alumnosQueSolicitaron(comisionId),
            HttpStatus.OK
        )
    }



    @ApiOperation("Endpoint que se usa para modificar los horarios de una comision ya creada")
    @RequestMapping(value = ["/horarios/{comisionId}"], method = [RequestMethod.PUT])
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
    fun modificarHorarios(
            @ApiParam(value = "Id de la comision", example = "1", required = true)
            @PathVariable
            comisionId: Long,
            @RequestBody
            nuevosHorarios: List<HorarioDTO>
    ): ResponseEntity<*> {
        return ResponseEntity(
                comisionService.modificarHorarios(comisionId, nuevosHorarios),
                HttpStatus.OK
        )
    }

}
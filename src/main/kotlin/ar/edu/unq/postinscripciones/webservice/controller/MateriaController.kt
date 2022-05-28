package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.service.ComisionService
import ar.edu.unq.postinscripciones.service.MateriaService
import ar.edu.unq.postinscripciones.service.dto.ComisionDTO
import ar.edu.unq.postinscripciones.service.dto.FormularioMateria
import ar.edu.unq.postinscripciones.service.dto.MateriaDTO
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@ServiceREST
@RequestMapping("/api/materia")
class MateriaController {
    @Autowired
    private lateinit var materiaService: MateriaService

    @Autowired
    private lateinit var comisionService: ComisionService

    @ApiOperation("Endpoint que se usa para registra una nueva materia en el sistema")
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "Materia creada", response = MateriaDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = [""], method = [RequestMethod.POST])
    fun registrarMateria(@RequestBody formulariosMaterias: List<FormularioMateria>): ResponseEntity<*> {
        return ResponseEntity(
            materiaService.crear(formulariosMaterias),
            HttpStatus.CREATED
        )
    }

    @ApiOperation(value = "Endpoint usado para listar todas las materias disponibles")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "OK", response = MateriaDTO::class, responseContainer = "List"),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    @RequestMapping(value = [""], method = [RequestMethod.GET])
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
    @RequestMapping(value = ["/{codigo}/correlativas"], method = [RequestMethod.PUT])
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
    @RequestMapping(value = ["/{codigo}/comision"], method = [RequestMethod.GET])
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
}

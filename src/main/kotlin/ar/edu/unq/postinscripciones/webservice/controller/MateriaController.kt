package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.service.MateriaService
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

    @ApiOperation("Endpoint que se usa para registra una nueva materia en el sistema")
    @RequestMapping(value = [""], method = [RequestMethod.POST])
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "Materia creada", response = MateriaDTO::class),
            ApiResponse(code = 400, message = "Algo salio mal")
        ]
    )
    fun registrarMateria(@RequestBody formularioMateria: FormularioMateria): ResponseEntity<*> {
        return ResponseEntity(
            materiaService.crear(
                formularioMateria.nombre,
                formularioMateria.codigo,
                formularioMateria.correlativas,
                formularioMateria.carrera
            ),
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
    @RequestMapping(value = ["/correlativas/{codigo}"], method = [RequestMethod.PUT])
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
}

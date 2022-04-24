package ar.edu.unq.postinscripciones.webservice.controller

import ar.edu.unq.postinscripciones.service.ComisionService
import ar.edu.unq.postinscripciones.service.FormularioComision
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@ServiceREST
@RequestMapping("/api/comision")
class ComisionController {
    @Autowired
    private lateinit var comisionService: ComisionService

    @ApiOperation("Endpoint que se usa para registrar una nueva comision en el sistema")
    @RequestMapping(value = ["/crear"], method = [RequestMethod.POST])
    fun registrarComision(@RequestBody formularioComision: FormularioComision): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.crear(formularioComision),
            HttpStatus.CREATED
        )
    }

    @ApiOperation(value = "Endpoint usado para listar todas las materias disponibles")
    @RequestMapping(value = ["/materia/{codigoMateria}"], method = [RequestMethod.GET])
    fun materiasComision(@PathVariable codigoMateria: String): ResponseEntity<*> {
        return ResponseEntity(
            comisionService.obtenerComisionesMateria(codigoMateria),
            HttpStatus.OK
        )
    }
}
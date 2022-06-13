package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioDTO
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaCursadaResumenDTO
import io.swagger.annotations.ApiModelProperty

data class ResumenAlumno(
    @ApiModelProperty(example = "Hilda")
        val nombre: String,
    @ApiModelProperty(example = "12345677")
        val dni: Int,
    val coeficiente: Double,
    val formulario: FormularioDTO,
    val resumenCursadas: List<MateriaCursadaResumenDTO>
)

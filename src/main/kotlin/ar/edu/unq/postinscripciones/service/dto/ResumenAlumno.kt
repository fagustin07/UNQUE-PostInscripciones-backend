package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty

data class ResumenAlumno(
        @ApiModelProperty(example = "Hilda")
        val nombre: String,
        @ApiModelProperty(example = "12345677")
        val dni: Int,
        val formulario: FormularioDTO,
        val resumenCursadas: List<MateriaCursadaResumenDTO>
)

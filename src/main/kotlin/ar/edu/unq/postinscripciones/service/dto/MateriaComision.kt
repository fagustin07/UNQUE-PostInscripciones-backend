package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty

data class MateriaComision(
    @ApiModelProperty(example = "01576")
    val codigo: String,
    @ApiModelProperty(example = "Algoritmos")
    val nombre: String,
    val comisiones: MutableList<ComisionParaAlumno>
)
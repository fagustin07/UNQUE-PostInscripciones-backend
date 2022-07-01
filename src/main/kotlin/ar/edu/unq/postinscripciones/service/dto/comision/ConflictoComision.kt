package ar.edu.unq.postinscripciones.service.dto.comision

import io.swagger.annotations.ApiModelProperty

data class ConflictoComision(
    @ApiModelProperty(example = "Algoritmos")
    val nombreMateria: String,
    @ApiModelProperty(example = "1")
    val comision: Int,
    @ApiModelProperty(example = "Ya existe la comision que se quiere cargar")
    val mensaje: String
)
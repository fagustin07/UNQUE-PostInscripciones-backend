package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty

data class MateriaConCorrelativas(
    @ApiModelProperty(example = "bases de datos")
    val nombre: String,
    val correlativas: List<Correlativa>
)

data class Correlativa(
    @ApiModelProperty(example = "matemática 1")
    val nombre: String
)

package ar.edu.unq.postinscripciones.service.dto.materia

import io.swagger.annotations.ApiModelProperty

data class MateriaConCorrelativas(
    @ApiModelProperty(example = "01035")
    val codigoMateria: String,
    val correlativas: List<Correlativa>
)

data class Correlativa(
    @ApiModelProperty(example = "01033")
    val codigoCorrelativa: String
)

package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty

data class ComisionACrear(
    @ApiModelProperty(example = "1", required = true)
    val numeroComision: Int,
    @ApiModelProperty(example = "01307", required = true)
    val codigoMateria: String,
    val horarios: List<HorarioDTO>,
    @ApiModelProperty(example = "30", required = true)
    val cuposTotales: Int,
    @ApiModelProperty(example = "8", required = true)
    val sobrecuposTotales: Int
)

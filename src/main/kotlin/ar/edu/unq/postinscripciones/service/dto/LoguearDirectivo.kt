package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty

data class LoguearDirectivo(
    @ApiModelProperty(example = "gabi@unq.edu.ar")
    val correo: String,
    @ApiModelProperty(example = "1234")
    val contrasenia: String
)

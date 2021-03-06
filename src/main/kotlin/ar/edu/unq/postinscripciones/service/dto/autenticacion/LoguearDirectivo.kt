package ar.edu.unq.postinscripciones.service.dto.autenticacion

import io.swagger.annotations.ApiModelProperty

data class LoguearDirectivo(
    @ApiModelProperty(example = "gabi@unque.edu.ar")
    val correo: String,
    @ApiModelProperty(example = "1234")
    val contrasenia: String
)

package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty

data class CreacionDirectivo(
    @ApiModelProperty(example = "fla@unque.edu.ar")
    val correo: String,
    @ApiModelProperty(example = "Flavia S")
    val nombre: String,
    @ApiModelProperty(example = "123456")
    val contrasenia: String
)
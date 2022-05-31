package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty

data class ConfirmacionCuenta(
    @ApiModelProperty(example = "1234577")
    val dni: Int,
    @ApiModelProperty(example = "7654321")
    val codigo: Int
)

package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty

data class FormularioRegistro(
    @ApiModelProperty(example = "12345677")
    val dni: Int,
    @ApiModelProperty(example = "seguridadSegura")
    val contrasenia: String,
    @ApiModelProperty(example = "seguridadSegura")
    val confirmacionContrasenia: String
)

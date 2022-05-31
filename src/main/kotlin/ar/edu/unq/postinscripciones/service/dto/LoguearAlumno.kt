package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty

data class LoguearAlumno(
    @ApiModelProperty(example = "1234577")
    val dni: Int,
    @ApiModelProperty(example = "seguridadSegura")
    val contrasenia: String
)

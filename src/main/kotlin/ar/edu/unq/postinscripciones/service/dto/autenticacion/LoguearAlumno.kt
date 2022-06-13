package ar.edu.unq.postinscripciones.service.dto.autenticacion

import io.swagger.annotations.ApiModelProperty

data class LoguearAlumno(
    @ApiModelProperty(example = "12345677")
    val dni: Int,
    @ApiModelProperty(example = "seguridadSegura")
    val contrasenia: String
)

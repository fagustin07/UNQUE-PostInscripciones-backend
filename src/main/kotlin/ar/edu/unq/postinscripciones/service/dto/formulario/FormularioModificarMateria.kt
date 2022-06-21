package ar.edu.unq.postinscripciones.service.dto.formulario

import ar.edu.unq.postinscripciones.model.Carrera
import io.swagger.annotations.ApiModelProperty

data class FormularioModificarMateria(
        @ApiModelProperty(example = "Intro", required = true)
        val nombre: String,
        @ApiModelProperty(example = "00487", required = true)
        val codigo: String,
        @ApiModelProperty(example = "LICENCIATURA", required = true)
        val carrera: Carrera
)

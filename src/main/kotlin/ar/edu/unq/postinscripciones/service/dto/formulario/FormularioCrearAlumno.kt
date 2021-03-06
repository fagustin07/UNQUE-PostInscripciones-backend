package ar.edu.unq.postinscripciones.service.dto.formulario

import ar.edu.unq.postinscripciones.model.Carrera
import io.swagger.annotations.ApiModelProperty

data class FormularioCrearAlumno(
        @ApiModelProperty(example = "1234567", required = true)
        val dni: Int,
        @ApiModelProperty(example = "Pepito", required = true)
        val nombre: String,
        @ApiModelProperty(example = "Gigoberto", required = true)
        val apellido: String,
        @ApiModelProperty(example = "pepito@ejemplo.com", required = true)
        val correo: String,
        @ApiModelProperty(example = "999", required = true)
        val legajo: Int,
        @ApiModelProperty(example = "TPI", required = true)
        val carrera: Carrera,
        @ApiModelProperty(example = "7.0", required = true)
        val coeficiente: Double,
)
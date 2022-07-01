package ar.edu.unq.postinscripciones.service.dto.formulario

import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import io.swagger.annotations.ApiModelProperty

data class FormularioCuatrimestre(
    @ApiModelProperty(example = "2022", required = true)
    val anio: Int,
    val semestre: Semestre
)
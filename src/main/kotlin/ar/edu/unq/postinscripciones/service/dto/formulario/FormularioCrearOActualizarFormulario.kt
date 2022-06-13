package ar.edu.unq.postinscripciones.service.dto.formulario

import io.swagger.annotations.ApiModelProperty

data class FormularioCrearOActualizarFormulario(
        @ApiModelProperty(value = "Lista de id de comisiones solicitadasa", required = true)
        val comisiones: List<Long>,
        @ApiModelProperty(value = "Lista de id de de comisiones en las que el alumno se encuentra inscripto", required = false)
        val comisionesInscripto: List<Long> = listOf()
)
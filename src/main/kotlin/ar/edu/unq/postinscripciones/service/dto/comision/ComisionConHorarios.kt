package ar.edu.unq.postinscripciones.service.dto.comision

import io.swagger.annotations.ApiModelProperty

data class ComisionConHorarios(
    @ApiModelProperty(value = "numero de la comision", example = "1")
    val comision: Int,
    @ApiModelProperty(value = "nombre de la materia", example = "bases de datos")
    val materia: String,
    val horarios: List<HorarioDTO>
)

package ar.edu.unq.postinscripciones.service.dto.alumno

import io.swagger.annotations.ApiModelProperty

data class ConflictoAlumno(
    @ApiModelProperty(example = "12345678")
    val dni: Int,
    @ApiModelProperty(example = "45965")
    val legajo: Int,
    @ApiModelProperty(example = "hay conflicto con el alumno ... y legajo ...")
    val mensaje: String
)

data class ConflictoHistoriaAcademica(
    @ApiModelProperty(example = "12345678")
    val dni: Int,
    @ApiModelProperty(example = "231321")
    val materia: String,
    @ApiModelProperty(example = "Materia no encontrada")
    val mensaje: String
)
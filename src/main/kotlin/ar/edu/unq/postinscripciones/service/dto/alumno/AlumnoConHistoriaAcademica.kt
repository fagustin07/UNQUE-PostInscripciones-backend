package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.service.dto.materia.MateriaCursadaDTO
import io.swagger.annotations.ApiModelProperty

data class AlumnoConHistoriaAcademica(
    @ApiModelProperty(example = "12345677")
    val dni: Int,
    val materiasCursadas: List<MateriaCursadaDTO>
)

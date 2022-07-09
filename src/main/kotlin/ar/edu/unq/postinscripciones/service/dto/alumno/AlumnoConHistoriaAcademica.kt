package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.model.EstadoMateria
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaCursadaDTO
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

data class AlumnoConHistoriaAcademica(
    @ApiModelProperty(example = "12345677")
    val dni: Int,
    val materiasCursadas: List<MateriaCursadaDTO>
)

data class AlumnoMateriaCursada(
    @ApiModelProperty(example = "12345677")
    val dni: Int,
    @ApiModelProperty(example = "00646")
    val codigo: String,
    @ApiModelProperty(example = "2022-03-15")
    val fecha: LocalDate,
    @ApiModelProperty(example = "APROBADO")
    val resultado: EstadoMateria,
    @ApiModelProperty(example = "31045")
    val fila: Int,
)

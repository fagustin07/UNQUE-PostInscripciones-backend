package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class OfertaAcademicaDTO(
    val comisionesACargar: List<ComisionACrear>,
    @ApiModelProperty(example = "2022-01-01T20:30", required = false)
    val inicioInscripciones: LocalDateTime?,
    @ApiModelProperty(example = "2022-07-30T23:59", required = false)
    val finInscripciones: LocalDateTime?,
)

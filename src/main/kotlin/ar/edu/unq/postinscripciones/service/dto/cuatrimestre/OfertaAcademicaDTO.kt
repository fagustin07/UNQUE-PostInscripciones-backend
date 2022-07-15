package ar.edu.unq.postinscripciones.service.dto.cuatrimestre

import ar.edu.unq.postinscripciones.service.dto.carga.datos.ComisionNueva
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class OfertaAcademicaDTO(
    val comisionesACargar: List<ComisionNueva>?,
    @ApiModelProperty(example = "2022-01-01T20:30", required = false)
    val inicioInscripciones: LocalDateTime?,
    @ApiModelProperty(example = "2022-07-30T23:59", required = false)
    val finInscripciones: LocalDateTime?,
)

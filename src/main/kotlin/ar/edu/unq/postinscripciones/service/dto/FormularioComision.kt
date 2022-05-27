package ar.edu.unq.postinscripciones.service.dto

import ar.edu.unq.postinscripciones.model.comision.Modalidad
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import io.swagger.annotations.ApiModelProperty

data class FormularioComision(
    @ApiModelProperty(example = "1", required = true)
    val numero: Int,
    @ApiModelProperty(example = "1", required = true)
    val codigoMateria: String,
    @ApiModelProperty(example = "2022", required = true)
    val anio: Int,
    @ApiModelProperty(example = "S1", required = true)
    val semestre: Semestre,
    @ApiModelProperty(example = "30", required = true)
    val cuposTotales: Int,
    @ApiModelProperty(example = "8", required = true)
    val sobreCuposTotales: Int,
    val horarios: List<HorarioDTO>,
    @ApiModelProperty(example = "PRESENCIAL", required = true)
    val modalidad: Modalidad
)
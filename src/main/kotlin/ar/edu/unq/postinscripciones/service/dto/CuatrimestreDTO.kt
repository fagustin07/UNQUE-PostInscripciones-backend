package ar.edu.unq.postinscripciones.service.dto

import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import io.swagger.annotations.ApiModelProperty

data class CuatrimestreDTO(
    @ApiModelProperty(example = "2022")
    val anio: Int,
    @ApiModelProperty(example = "S2")
    val semestre: Semestre,
    @ApiModelProperty(example = "2022-01-01T20:30")
    val inicio: String,
    @ApiModelProperty(example = "2022-01-30T20:30")
    val fin: String
) {
    companion object {
        fun desdeModelo(cuatrimestre: Cuatrimestre): CuatrimestreDTO {
            return CuatrimestreDTO(
                cuatrimestre.anio,
                cuatrimestre.semestre,
                cuatrimestre.inicioInscripciones.toString(),
                cuatrimestre.finInscripciones.toString()
            )
        }
    }
}
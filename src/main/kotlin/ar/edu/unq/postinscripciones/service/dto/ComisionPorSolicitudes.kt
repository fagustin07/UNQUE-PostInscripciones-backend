package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty
import javax.persistence.Tuple

data class ComisionPorSolicitudes(
    @ApiModelProperty(example = "65456")
    val id: Long,
    @ApiModelProperty(example = "1")
    val comision: Int,
    @ApiModelProperty(example = "Algoritmos")
    val materia: String,
    @ApiModelProperty(example = "12")
    val cantidadSolicitudes: Long,
    @ApiModelProperty(example = "3")
    val sobrecuposTotales: Int,
    @ApiModelProperty(example = "1")
    val sobrecuposDisponibles: Int
) {
    companion object {
        fun desdeTupla(tuple: Tuple): ComisionPorSolicitudes {
            return ComisionPorSolicitudes(
                tuple.get(0) as Long,
                tuple.get(1) as Int,
                tuple.get(2) as String,
                tuple.get(3) as Long,
                tuple.get(4) as Int,
                tuple.get(5) as Int
            )
        }
    }
}
package ar.edu.unq.postinscripciones.service.dto.materia

import io.swagger.annotations.ApiModelProperty
import javax.persistence.Tuple

data class MateriaPorSolicitudes(
    @ApiModelProperty(example = "65456")
    val codigo: String,
    @ApiModelProperty(example = "Algoritmos")
    val nombre: String,
    @ApiModelProperty(example = "1")
    val cantidadSolicitudes: Int,
    @ApiModelProperty(example = "1")
    val cantidadSolicitudesPendientes: Int
) {
    companion object {
        fun desdeTupla(tuple: Tuple): MateriaPorSolicitudes {
            return MateriaPorSolicitudes(
                tuple.get(0) as String,
                tuple.get(1) as String,
                (tuple.get(2) as Long).toInt(),
                (tuple.get(3) as Long).toInt()
            )
        }
    }
}
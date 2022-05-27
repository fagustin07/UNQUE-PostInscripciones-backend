package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty
import java.math.BigInteger
import javax.persistence.Tuple

data class AlumnoSolicitaComision(
    @ApiModelProperty(example = "1234577")
    val dni: Int,
    @ApiModelProperty(example = "15")
    val cantidadDeAprobadas: Int,
    @ApiModelProperty(example = "1201")
    val idFormulario: Long,
    @ApiModelProperty(example = "3213")
    val idSolicitud: Long
) {
    companion object {
        fun desdeTupla(tupla: Tuple): AlumnoSolicitaComision {
            return AlumnoSolicitaComision(
                tupla.get(0) as Int,
                (tupla.get(3) as BigInteger).toInt(),
                (tupla.get(1) as BigInteger).toLong(),
                (tupla.get(2) as BigInteger).toLong(),
            )
        }
    }
}
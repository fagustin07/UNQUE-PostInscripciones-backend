package ar.edu.unq.postinscripciones.service.dto

import io.swagger.annotations.ApiModelProperty
import javax.persistence.Tuple

data class AlumnoSolicitaMateria(
    @ApiModelProperty(example = "1234577")
    val dni: Int,
    @ApiModelProperty(example = "1201")
    val idFormulario: Long,
    @ApiModelProperty(example = "3213")
    val idSolicitud: Long,
    @ApiModelProperty(example = "1")
    val numeroComision: Int,
    @ApiModelProperty(example = "80000")
    val codigoMateria: String,
    @ApiModelProperty(example = "15")
    val cantidadDeAprobadas: Int,
    @ApiModelProperty(example = "4.33")
    val coeficiente: Double,

) {
    companion object {
        fun desdeTupla(tupla: Tuple): AlumnoSolicitaMateria {
            return AlumnoSolicitaMateria(
                tupla.get(0) as Int,
                tupla.get(1) as Long,
                tupla.get(2) as Long,
                tupla.get(3) as Int,
                tupla.get(4) as String,
                (tupla.get(5) as Long).toInt(),
                tupla.get(6) as Double
            )
        }
    }
}
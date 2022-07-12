package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.model.EstadoSolicitud
import io.swagger.annotations.ApiModelProperty
import javax.persistence.Tuple

data class AlumnoSolicitaMateria(
    @ApiModelProperty(example = "1234577")
    val dni: Int,
    @ApiModelProperty(example = "1234577")
    val nombreApellido: String,
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
    @ApiModelProperty(example = "PENDIENTE")
    val estado: EstadoSolicitud,
) {
    companion object {
        fun desdeTupla(tupla: Tuple): AlumnoSolicitaMateria {
            return AlumnoSolicitaMateria(
                tupla.get(0) as Int,
                (tupla.get(1) as String) + " " + (tupla.get(2) as String),
                tupla.get(3) as Long,
                tupla.get(4) as Long,
                tupla.get(5) as Int,
                tupla.get(6) as String,
                (tupla.get(7) as Long).toInt(),
                tupla.get(8) as EstadoSolicitud
                )
        }
    }
}
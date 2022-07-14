package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.model.Alumno
import ar.edu.unq.postinscripciones.model.Calidad
import ar.edu.unq.postinscripciones.model.EstadoInscripcion
import ar.edu.unq.postinscripciones.model.Regular
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Locacion
import io.swagger.annotations.ApiModelProperty

data class AlumnoDTO(
    @ApiModelProperty(example = "1234567")
    val dni: Int,
    @ApiModelProperty(example = "Santiago")
    val nombre: String,
    @ApiModelProperty(example = "Perez")
    val apellido: String,
    @ApiModelProperty(example = "correo@ejemplo.com.ar")
    val correo: String,
    @ApiModelProperty(example = "5")
    val cantidadAprobadas: Long,
    @ApiModelProperty(example = "Bernal")
    val locacion: Locacion,
    @ApiModelProperty(example = "S")
    val regular: Regular,
    @ApiModelProperty(example = "Activo")
    val calidad: Calidad,
    @ApiModelProperty(example = "Aceptado")
    val estado: EstadoInscripcion
) {
    companion object {
        fun desdeModelo(alumno: Alumno): AlumnoDTO {
            return AlumnoDTO(
                alumno.dni,
                alumno.nombre,
                alumno.apellido,
                alumno.correo,
                alumno.cantidadAprobadas().toLong(),
                alumno.locacion,
                alumno.regular,
                alumno.calidad,
                alumno.estadoInscripcion
            )
        }
    }
}

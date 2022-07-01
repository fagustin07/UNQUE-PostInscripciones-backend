package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.model.Alumno
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
    @ApiModelProperty(example = "12345")
    val legajo: Int,
    @ApiModelProperty(example = "5.33")
    val coeficiente: Double
) {
    companion object {
        fun desdeModelo(alumno: Alumno): AlumnoDTO {
            return AlumnoDTO(
                alumno.dni,
                alumno.nombre,
                alumno.apellido,
                alumno.correo,
                alumno.legajo,
                alumno.coeficiente
            )
        }
    }
}

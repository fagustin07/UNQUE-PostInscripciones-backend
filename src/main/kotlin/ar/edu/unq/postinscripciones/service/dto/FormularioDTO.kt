package ar.edu.unq.postinscripciones.service.dto

import ar.edu.unq.postinscripciones.model.EstadoFormulario
import ar.edu.unq.postinscripciones.model.Formulario
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import io.swagger.annotations.ApiModelProperty

data class FormularioDTO(
        @ApiModelProperty(example = "123")
        val id: Long,
        @ApiModelProperty(example = "12345677")
        val dniAlumno: Int,
        val solicitudes: List<SolicitudSobrecupoDTO>,
        @ApiModelProperty(example = "ABIERTO")
        val estado: EstadoFormulario
) {
    companion object {
        fun desdeModelo(formulario: Formulario, dni: Int): FormularioDTO {
            return FormularioDTO(
                    formulario.id!!,
                    dni,
                    formulario.solicitudes.map { SolicitudSobrecupoDTO.desdeModelo(it) },
                    formulario.estado
            )
        }
    }

}

package ar.edu.unq.postinscripciones.service.dto.formulario

import ar.edu.unq.postinscripciones.model.EstadoFormulario
import ar.edu.unq.postinscripciones.model.Formulario
import ar.edu.unq.postinscripciones.service.dto.comision.ComisionDTO
import io.swagger.annotations.ApiModelProperty

data class FormularioAlumnoDTO(
        @ApiModelProperty(example = "123")
        val id: Long,
        @ApiModelProperty(example = "12345677")
        val dniAlumno: Int,
        val solicitudes: List<SolicitudSobrecupoDTO>,
        @ApiModelProperty(example = "ABIERTO")
        val estado: EstadoFormulario,
        val comisionesInscripto: List<ComisionDTO>,
) {
    companion object {
        fun desdeModelo(formulario: Formulario, dniAlumno: Int): FormularioAlumnoDTO {
            return FormularioAlumnoDTO(
                    formulario.id!!,
                    dniAlumno,
                    formulario.solicitudes.map { SolicitudSobrecupoDTO.desdeModelo(it) },
                    formulario.estado,
                    formulario.comisionesInscripto.map { ComisionDTO.desdeModelo(it) },
            )
        }

        fun desdeModeloCerrado(formulario: Formulario, dni: Int): FormularioAlumnoDTO {
            return FormularioAlumnoDTO(
                    formulario.id!!,
                    dni,
                    formulario.solicitudes.map { SolicitudSobrecupoDTO.desdeModeloParaAlumno(it) },
                    formulario.estado,
                    formulario.comisionesInscripto.map { ComisionDTO.desdeModelo(it) }
            )
        }
    }
}
package ar.edu.unq.postinscripciones.service.dto

import ar.edu.unq.postinscripciones.model.EstadoFormulario
import ar.edu.unq.postinscripciones.model.EstadoSolicitud
import ar.edu.unq.postinscripciones.model.Formulario
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import io.swagger.annotations.ApiModelProperty

data class FormularioDTO(
        @ApiModelProperty(example = "123")
        val id: Long,
        @ApiModelProperty(example = "12345677")
        val dniAlumno: Int,
        val solicitudes: List<SolicitudSobrecupoDTO>,
        @ApiModelProperty(example = "ABIERTO")
        val estado: EstadoFormulario,
        val comisionesInscripto: List<ComisionDTO>
) {
    companion object {
        fun desdeModelo(formulario: Formulario, dni: Int): FormularioDTO {
            return FormularioDTO(
                    formulario.id!!,
                    dni,
                    formulario.solicitudes.map { SolicitudSobrecupoDTO.desdeModelo(it) },
                    formulario.estado,
                    formulario.comisionesInscripto.map { ComisionDTO.desdeModelo(it) }
            )
        }

        fun desdeModeloParaAlumno(formulario: Formulario, dni: Int): FormularioDTO {
            return FormularioDTO(
                    formulario.id!!,
                    dni,
                    formulario.solicitudes.map { SolicitudSobrecupoDTO.desdeModeloParaAlumno(it) },
                    formulario.estado,
                    formulario.comisionesInscripto.map { ComisionDTO.desdeModelo(it) }
            )
        }

    }

}

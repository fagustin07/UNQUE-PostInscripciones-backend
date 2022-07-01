package ar.edu.unq.postinscripciones.service.dto.formulario

import ar.edu.unq.postinscripciones.model.EstadoFormulario
import ar.edu.unq.postinscripciones.model.Formulario
import ar.edu.unq.postinscripciones.service.dto.comision.ComisionDTO
import io.swagger.annotations.ApiModelProperty

data class FormularioDirectorDTO(
        @ApiModelProperty(example = "123")
         val id: Long,
        @ApiModelProperty(example = "12345677")
         val dniAlumno: Int,
         val solicitudes: List<SolicitudSobrecupoDTO>,
        @ApiModelProperty(example = "ABIERTO")
         val estado: EstadoFormulario,
         val comisionesInscripto: List<ComisionDTO>,
        val comentarios: List<ComentarioDTO>
) {
    companion object {
        fun desdeModelo(formulario: Formulario, dniAlumno: Int): FormularioDirectorDTO {
            return FormularioDirectorDTO(
                    formulario.id!!,
                    dniAlumno,
                    formulario.solicitudes.map { SolicitudSobrecupoDTO.desdeModelo(it) },
                    formulario.estado,
                    formulario.comisionesInscripto.map { ComisionDTO.desdeModelo(it) },
                    formulario.comentarios.map { ComentarioDTO.desdeModelo(it) }
            )
        }
    }
}



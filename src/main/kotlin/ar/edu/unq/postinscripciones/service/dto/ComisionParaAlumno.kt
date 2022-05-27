package ar.edu.unq.postinscripciones.service.dto

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.comision.Modalidad
import io.swagger.annotations.ApiModelProperty

data class ComisionParaAlumno(
    @ApiModelProperty(example = "789")
    val id: Long,
    @ApiModelProperty(example = "2")
    val comision: Int,
    @ApiModelProperty(example = "PRESENCIAL")
    val modalidad: Modalidad,
    val horarios: List<HorarioDTO>
    ) {
    companion object {
        fun desdeModelo(comision: Comision): ComisionParaAlumno {
            return ComisionParaAlumno(
                comision.id!!,
                comision.numero,
                comision.modalidad,
                comision.horarios.map { HorarioDTO.desdeModelo(it) }
            )
        }
    }
}
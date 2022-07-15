package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.model.EstadoSolicitud
import ar.edu.unq.postinscripciones.model.SolicitudSobrecupo
import ar.edu.unq.postinscripciones.service.dto.cuatrimestre.CuatrimestreDTO
import io.swagger.annotations.ApiModelProperty

class ResumenSolicitudDTO(
    @ApiModelProperty(example = "Introduccion a la Programacion")
    val nombreMateria: String,
    val cuatrimestre: CuatrimestreDTO,
    @ApiModelProperty(example = "PENDIENTE")
    val estado: EstadoSolicitud,
) {
    companion object {
        fun desdeModelo(solicitudSobrecupo: SolicitudSobrecupo) : ResumenSolicitudDTO{
            return ResumenSolicitudDTO(solicitudSobrecupo.comision.materia.nombre, CuatrimestreDTO.desdeModelo(solicitudSobrecupo.comision.cuatrimestre), solicitudSobrecupo.estado)
        }
    }
}

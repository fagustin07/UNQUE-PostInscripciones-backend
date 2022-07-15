package ar.edu.unq.postinscripciones.service.dto.comision

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.comision.Modalidad
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Locacion
import io.swagger.annotations.ApiModelProperty

data class ComisionDTO(
    @ApiModelProperty(example = "132")
    val id: Long,
    @ApiModelProperty(example = "5")
    val numero: Int,
    @ApiModelProperty(example = "Estructuras de Datos")
    val materia: String,
    @ApiModelProperty(example = "30")
    val cuposTotales: Int,
    @ApiModelProperty(example = "10")
    val sobreCuposTotales: Int,
    @ApiModelProperty(example = "12")
    val cuposDisponibles: Int,
    val horarios: List<HorarioDTO>,
    @ApiModelProperty(example = "Bernal")
    val locacion: Locacion
) {

    companion object {
        fun desdeModelo(comision: Comision): ComisionDTO {
            return ComisionDTO(
                comision.id!!,
                comision.numero,
                comision.materia.nombre,
                comision.cuposTotales,
                comision.sobrecuposTotales,
                comision.sobrecuposDisponibles(),
                comision.horarios.map { HorarioDTO.desdeModelo(it) },
                comision.locacion
            )
        }
    }
}

data class ComisionInfoCursadaDTO(
    @ApiModelProperty(example = "132")
    val id: Long,
    @ApiModelProperty(example = "5")
    val numero: Int,
    @ApiModelProperty(example = "Estructuras de Datos")
    val materia: String,
    @ApiModelProperty(example = "VIRTUAL")
    val modalidad: Modalidad,
    @ApiModelProperty(example = "5")
    val sobrecuposTotales: Int,
    @ApiModelProperty(example = "3")
    val sobrecuposDisponibles: Int,
    val horarios: List<HorarioDTO>
) {
    companion object {
        fun desdeModelo(comision: Comision): ComisionInfoCursadaDTO {
            return ComisionInfoCursadaDTO(
                comision.id!!,
                comision.numero,
                comision.materia.nombre,
                comision.modalidad,
                comision.sobrecuposTotales,
                comision.sobrecuposDisponibles(),
                comision.horarios.map { HorarioDTO.desdeModelo(it) })
        }
    }
}
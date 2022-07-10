package ar.edu.unq.postinscripciones.service.dto.carga.datos

import ar.edu.unq.postinscripciones.model.comision.Modalidad
import ar.edu.unq.postinscripciones.service.dto.comision.HorarioDTO
import io.swagger.annotations.ApiModelProperty

data class ComisionNueva(
    @ApiModelProperty(name = "codigo de la materia", example = "1035")
    val codigo: String,
    @ApiModelProperty(name = "nombre de la materia", example = "Bases de datos")
    val actividad: String,
    @ApiModelProperty(name = "numero de comision", example = "5")
    val comision: Int,
    @ApiModelProperty(name = "modalidad de la comision", example = "VIRTUAL_ASINCRONICA")
    val modalidad: Modalidad,
    @ApiModelProperty(name = "lugar donde se dicta la comsion", example = "General_Belgrano")
    val locacion: Locacion,
    val horarios: List<HorarioDTO>,
    @ApiModelProperty(name = "fila del dato", example = "1234")
    val fila: Int,
    @ApiModelProperty(name = "cupos totales de la comision", example = "30", required = false)
    val cuposTotales: Int = 30,
    @ApiModelProperty(name = "sobrecupos asignados a la comision", example = "1035", required = false)
    val sobrecuposTotales: Int = 7
)

enum class Locacion {
    Bernal, Berazategui, General_Belgrano
}

data class Conflicto(
    val fila: Int,
    val mensaje: String
)
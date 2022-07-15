package ar.edu.unq.postinscripciones.service.dto.carga.datos

import ar.edu.unq.postinscripciones.model.CicloLI
import ar.edu.unq.postinscripciones.model.CicloTPI
import ar.edu.unq.postinscripciones.model.exception.ErrorDeNegocio
import io.swagger.annotations.ApiModelProperty

data class PlanillaMaterias(
    @ApiModelProperty(example = "TPI2010")
    val plan: Plan,
    val materias: List<MateriaParaCargar>
) {
    init {
        if ((plan==Plan.TPI2010 || plan==Plan.TPI2015) && materias.any { it.cicloTPI == CicloTPI.NO_PERTENECE } ) {
            throw ErrorDeNegocio("Hay materias que no poseen un ciclo para la tpi")
        }

        if (plan==Plan.LI && materias.any { it.cicloLI == CicloLI.NO_PERTENECE } ) {
            throw ErrorDeNegocio("Hay materias que no poseen un ciclo para la li")
        }
    }
}

enum class Plan {
    TPI2010, TPI2015, LI
}

data class MateriaParaCargar(
    @ApiModelProperty(example = "CI", required = false)
    val cicloTPI: CicloTPI = CicloTPI.NO_PERTENECE,
    @ApiModelProperty(example = "NBW", required = false)
    val cicloLI: CicloLI = CicloLI.NO_PERTENECE,
    @ApiModelProperty(example = "123456")
    val codigo: String,
    @ApiModelProperty(example = "16")
    val creditos: Int,
    @ApiModelProperty(example = "Arquitectura Orientada a Servicios")
    val materia: String,
    val correlativas: List<String> = listOf(),
    @ApiModelProperty(example = "20")
    val ci: Int = 0,
    @ApiModelProperty(example = "32")
    val co: Int = 0,
    @ApiModelProperty(example = "100")
    val ca: Int = 0,
    @ApiModelProperty(example = "71")
    val cc: Int = 0,
    @ApiModelProperty(example = "45")
    val cb: Int = 0,
    @ApiModelProperty(example = "12")
    val nbw: Int = 0,
    @ApiModelProperty(example = "3")
    val fila: Int
) {
    init {
        if (cicloTPI == CicloTPI.NO_PERTENECE && cicloLI == CicloLI.NO_PERTENECE) {
            throw ErrorDeNegocio("La materia $codigo debe pertenecer a un ciclo")
        }
    }
}
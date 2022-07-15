package ar.edu.unq.postinscripciones.service.dto.materia

import ar.edu.unq.postinscripciones.model.CicloLI
import ar.edu.unq.postinscripciones.model.CicloTPI
import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.RequisitoCiclo
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Plan
import io.swagger.annotations.ApiModelProperty

data class ConflictoMateria(
    @ApiModelProperty(example = "Base de datos")
    val nombre: String,
    @ApiModelProperty(example = "01035")
    val codigo: String,
    @ApiModelProperty(example = "la materia ... genera conflicto con ...")
    val mensaje: String
)

data class ConflictoCorrelativa(
    @ApiModelProperty(example = "01035")
    val codigoMateria: String,
    @ApiModelProperty(example = "0100000213")
    val codigoCorrelativa: String,
    @ApiModelProperty(example = "No se encontró la correlativa")
    val mensaje: String
)

data class MateriaDetalle(
    @ApiModelProperty(example = "12345")
    val codigo: String,
    @ApiModelProperty(example = "SARASA")
    val nombre: String,
    var correlativas: List<String>,
    @ApiModelProperty(example = "8")
    val creditos: Int,
    @ApiModelProperty(example = "CO")
    var tpi2015: CicloTPI,
    @ApiModelProperty(example = "NBW")
    var li: CicloLI,
    val requisitosCiclo: List<RequisitoCicloDTO>,
    @ApiModelProperty(example = "NO_PERTENECE")
    var tpi2010: CicloTPI
) {
    companion object {
        fun desdeModelo(materia: Materia): MateriaDetalle {
            return MateriaDetalle(
                materia.codigo,
                materia.nombre,
                materia.correlativas.map { it.nombre },
                materia.creditos,
                materia.tpi2015,
                materia.li,
                materia.requisitosCiclo.map { RequisitoCicloDTO.desdeModelo(it) },
                materia.tpi2010
            )
        }
    }
}

data class RequisitoCicloDTO(
    @ApiModelProperty(example = "CB")
    val ciclo: String,
    @ApiModelProperty(example = "LI")
    val carrera: Plan,
    @ApiModelProperty(example = "112")
    val creditos: Int
) {
    companion object {
        fun desdeModelo(requisitoCiclo: RequisitoCiclo): RequisitoCicloDTO {
            val ciclo = if (requisitoCiclo.cicloLI != CicloLI.NO_PERTENECE) {
                requisitoCiclo.cicloLI.toString()
            } else {
                requisitoCiclo.cicloTPI.toString()
            }

            val carrera = if (requisitoCiclo.cicloLI != CicloLI.NO_PERTENECE) {
                Plan.LI
            } else if (requisitoCiclo.esTPI2010) {
                Plan.TPI2010
            } else {
                Plan.TPI2015
            }

            return RequisitoCicloDTO(ciclo, carrera, requisitoCiclo.creditos)
        }
    }
}
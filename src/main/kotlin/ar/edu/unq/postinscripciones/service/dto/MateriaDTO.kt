package ar.edu.unq.postinscripciones.service.dto

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.Materia
import io.swagger.annotations.ApiModelProperty

data class MateriaDTO(
    @ApiModelProperty(example = "00132")
    val codigo: String,
    @ApiModelProperty(example = "Algoritmos")
    val nombre: String,
    @ApiModelProperty(example = "LI")
    val carrera: Carrera,
    @ApiModelProperty(example = "Programacion Funcional")
    val correlativas: List<String>
) {
    companion object {
        fun desdeModelo(materia: Materia): MateriaDTO {
            return MateriaDTO(materia.codigo, materia.nombre, materia.carrera, materia.correlativas.map { it.nombre })
        }
    }
}

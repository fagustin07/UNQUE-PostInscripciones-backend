package ar.edu.unq.postinscripciones.service.dto.materia

import ar.edu.unq.postinscripciones.service.dto.comision.ComisionParaAlumno
import io.swagger.annotations.ApiModelProperty

data class MateriaComision(
    @ApiModelProperty(example = "01576")
    val codigo: String,
    @ApiModelProperty(example = "Algoritmos")
    val nombre: String,
    val comisiones: MutableList<ComisionParaAlumno>
)
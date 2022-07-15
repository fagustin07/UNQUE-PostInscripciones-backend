package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioDirectorDTO
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaCursadaResumenDTO
import io.swagger.annotations.ApiModelProperty

data class ResumenAlumno(
    @ApiModelProperty(example = "Hilda")
    val nombre: String,
    @ApiModelProperty(example = "12345677")
    val dni: Int,
    val carrera: Carrera,
    val formulario: FormularioDirectorDTO,
    val resumenCursadas: List<MateriaCursadaResumenDTO>,
    val solicitudesAntiguas: List<ResumenSolicitudDTO>
)

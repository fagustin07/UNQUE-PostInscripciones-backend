package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.service.dto.materia.MateriaCursadaDTO

data class AlumnoConHistoriaAcademica(
    val dni: Int,
    val materiasCursadas: List<MateriaCursadaDTO>
)

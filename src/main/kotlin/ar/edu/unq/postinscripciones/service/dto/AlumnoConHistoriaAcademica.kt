package ar.edu.unq.postinscripciones.service.dto

data class AlumnoConHistoriaAcademica(
    val dni: Int,
    val materiasCursadas: List<MateriaCursadaDTO>
)

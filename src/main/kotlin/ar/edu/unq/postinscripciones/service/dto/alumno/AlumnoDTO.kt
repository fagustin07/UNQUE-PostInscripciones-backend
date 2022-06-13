package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.model.Alumno

data class AlumnoDTO(
        val dni: Int,
        val nombre: String,
        val apellido: String,
        val correo: String,
        val legajo: Int,
        val coeficiente: Double) {
    companion object {
        fun desdeModelo(alumno: Alumno): AlumnoDTO {
            return AlumnoDTO(
                alumno.dni,
                alumno.nombre,
                alumno.apellido,
                alumno.correo,
                alumno.legajo,
                    alumno.coeficiente
            )
        }
    }
}

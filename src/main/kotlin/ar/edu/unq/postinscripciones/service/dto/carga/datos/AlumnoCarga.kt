package ar.edu.unq.postinscripciones.service.dto.carga.datos

import ar.edu.unq.postinscripciones.model.*

data class AlumnoCarga(
    val dni: Int,
    val nombre: String,
    val apellido: String,
    val propuesta: Carrera,
    val plan: Int,
    val estado: EstadoInscripcion,
    val calidad: Calidad,
    val regular: Regular,
    val locacion: Locacion,
    val fila: Int
) {
    fun aModelo(): Alumno {
        return Alumno(
            dni,
            nombre,
            apellido,
            "${dni}_${apellido}@alu.unque.edu.ar",
            "",
            propuesta,
            propuesta == Carrera.P && plan == 2010,
            locacion,
            estado,
            calidad,
            regular
        )
    }
}

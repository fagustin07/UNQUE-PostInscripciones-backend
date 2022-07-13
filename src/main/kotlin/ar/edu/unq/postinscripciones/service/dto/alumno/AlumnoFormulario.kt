package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.model.EstadoFormulario
import javax.persistence.Tuple

data class AlumnoFormulario(
    val alumno: AlumnoDTO,
    val formularioId: Long,
    val estadoFormulario: EstadoFormulario,
    val cantComisionesInscripto : Int,
    val cantSolicitudesPendientes : Int,
    val cantSolicitudesAprobadas : Int,
) {
    companion object {
        fun fromTuple(tupla: Tuple): AlumnoFormulario {
            return AlumnoFormulario(
                AlumnoDTO(
                    tupla.get(0) as Int,
                    tupla.get(1) as String,
                    tupla.get(2) as String,
                    tupla.get(3) as String,
                    tupla.get(9) as Long
                ),
                tupla.get(4) as Long,
                tupla.get(5) as EstadoFormulario,
                tupla.get(6) as Int,
                (tupla.get(7) as Long).toInt(),
                (tupla.get(8) as Long).toInt(),
            )
        }
    }
}
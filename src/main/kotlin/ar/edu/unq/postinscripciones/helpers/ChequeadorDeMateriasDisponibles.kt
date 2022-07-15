package ar.edu.unq.postinscripciones.helpers

import ar.edu.unq.postinscripciones.model.*
import org.springframework.stereotype.Component

@Component
class ChequeadorDeMateriasDisponibles {

    fun materiasQuePuedeCursar(alumno: Alumno, materias: List<Materia>): List<Materia> {
        return materias.filter { materia -> !alumno.haAprobado(materia) && cumpleRequisitosDeMateria(alumno, materia) }
    }

    private fun cumpleRequisitosDeMateria(alumno: Alumno, materia: Materia): Boolean {
        return when (alumno.carrera) {
            Carrera.P -> cumpleTPI(materia, alumno)
            Carrera.W -> cumpleLI(materia, alumno)
            Carrera.PW -> cumpleTPI(materia, alumno) || cumpleLI(materia, alumno)
        }
    }

    private fun cumpleLI(materia: Materia, alumno: Alumno): Boolean {
        return materia.li != CicloLI.NO_PERTENECE &&
                materia.requisitosCicloLI().all { it.cumpleRequisito(alumno, Carrera.W) } &&
                (materia.li == CicloLI.CO || materia.cumpleCorrelativas(alumno))
    }

    private fun cumpleTPI(materia: Materia, alumno: Alumno): Boolean {
        return (!alumno.cursaTPI2010 &&  cumpleTPI2015(materia, alumno)) ||
                (alumno.cursaTPI2010 &&  cumpleTPI2010(materia, alumno))
    }

    private fun cumpleTPI2015(materia: Materia, alumno: Alumno) = materia.tpi2015 != CicloTPI.NO_PERTENECE &&
            materia.requisitosCicloTPI().all { it.cumpleRequisito(alumno, Carrera.P) } &&
            (materia.tpi2015 == CicloTPI.CC || materia.cumpleCorrelativas(alumno))

    private fun cumpleTPI2010(materia: Materia, alumno: Alumno) = materia.tpi2010 != CicloTPI.NO_PERTENECE &&
            materia.requisitosCicloTPI2010().all { it.cumpleRequisito(alumno, Carrera.P) }
}
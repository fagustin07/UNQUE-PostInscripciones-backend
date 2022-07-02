package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import javax.persistence.*

enum class CicloTPI {
    CI,  //    ciclo introductorio
    CO,  //    ciclo obligatorio
    CA,  //    ciclo avanzado
    CC,  //    complementarias
    OR,  //    otros requisitos
    NO_PERTENECE
}

enum class CicloLI {
    CI,  //    ciclo introductorio
    OR,  //    otros requisitos
    NFH, //    Taller formacion humanistica
    NBW, //    nucleo basico
    CB,  //    cursos basicos
    CA,  //    cursos avanzados
    SF,   //    seminario final
    CO,  //    cursos orientados
    NO_PERTENECE
}

@Entity
class RequisitoCiclo(
    val carrera: Carrera = Carrera.P,
    val cicloTPI: CicloTPI = CicloTPI.NO_PERTENECE,
    val cicloLI: CicloLI = CicloLI.CA,
    val cantidad: Int = 30,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun cumpleRequisito(alumno: Alumno, carrera: Carrera): Boolean {
        if (carrera == Carrera.PW) throw ExcepcionUNQUE("Invalido")

        return if (carrera == this.carrera && carrera == Carrera.P) {
            alumno.creditosParaCicloDeTPI(cicloTPI) >= cantidad
        } else if (carrera == this.carrera && carrera == Carrera.W) {
            alumno.creditosParaCicloDeLI(cicloLI) >= cantidad
        } else {
            throw ExcepcionUNQUE("Invalido")
        }
    }


    init {
        if (carrera == Carrera.PW ||  (cicloLI != CicloLI.NO_PERTENECE && cicloTPI != CicloTPI.NO_PERTENECE)){
            throw ExcepcionUNQUE("Un requisito debe pertenecer solo a una carrera")
        }

        if (cicloLI == CicloLI.NO_PERTENECE && cicloTPI == CicloTPI.NO_PERTENECE) {
            throw ExcepcionUNQUE("Un requisito debe pertenecer a una carrera")
        }
    }
}
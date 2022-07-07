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
    val esTPI2010: Boolean = false,
    val creditos: Int = 30,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun cumpleRequisito(alumno: Alumno, carrera: Carrera): Boolean {
        return if (carrera == this.carrera && carrera == Carrera.P && alumno.cursaTPI2010 && esTPI2010) {
            alumno.creditosParaCicloDeTPI2010(cicloTPI) >= creditos
        } else if (carrera == this.carrera && carrera == Carrera.P && !alumno.cursaTPI2010 && !esTPI2010) {
            alumno.creditosParaCicloDeTPI(cicloTPI) >= creditos
        } else if (carrera == this.carrera && carrera == Carrera.W) {
            alumno.creditosParaCicloDeLI(cicloLI) >= creditos
        } else {
            throw ExcepcionUNQUE("Invalido")
        }
    }

    fun esElRequisito(requisitoCiclo: RequisitoCiclo): Boolean {
        return this.carrera == requisitoCiclo.carrera && this.cicloTPI == requisitoCiclo.cicloTPI &&
                cicloLI == requisitoCiclo.cicloLI && this.esTPI2010 == requisitoCiclo.esTPI2010
    }

    init {
        if (carrera == Carrera.PW){
            throw ExcepcionUNQUE("Un requisito debe pertenecer solo a una carrera")
        }

        if (esTPI2010 && cicloTPI == CicloTPI.CI){
            throw ExcepcionUNQUE("No existe el ciclo introductorio en el plan 2010")
        }

        if (cicloLI == CicloLI.NO_PERTENECE && cicloTPI == CicloTPI.NO_PERTENECE) {
            throw ExcepcionUNQUE("Un requisito debe pertenecer a un ciclo")
        }

        if (cicloLI != CicloLI.NO_PERTENECE && cicloTPI != CicloTPI.NO_PERTENECE) {
            throw ExcepcionUNQUE("Un requisito solo puede pertenecer a un ciclo")
        }
    }
}
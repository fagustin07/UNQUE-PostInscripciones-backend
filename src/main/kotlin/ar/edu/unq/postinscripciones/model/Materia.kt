package ar.edu.unq.postinscripciones.model

import javax.persistence.*

@Entity
class Materia(
    @Id
    val codigo: String = "",
    @Column(unique=true, nullable = false)
    val nombre: String = "",
    @ManyToMany(fetch = FetchType.LAZY)
    var correlativas: MutableList<Materia> = mutableListOf(),
    @Column(nullable = false)
    val creditos: Int = 8,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var tpi2015: CicloTPI = CicloTPI.CO,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var li: CicloLI = CicloLI.CA,
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val requisitosCiclo: MutableList<RequisitoCiclo> = mutableListOf(),
    @Column(nullable = false)
    val tpi2010: CicloTPI = CicloTPI.NO_PERTENECE
) {

    fun esLaMateria(materia: Materia) = this.codigo == materia.codigo

    fun actualizarCorrelativas(correlativasDadas: MutableList<Materia>) {
        correlativas.clear()
        correlativas.addAll(correlativasDadas)
    }

    fun quitarCorrelativa(codigo: String) {
        correlativas.removeIf { it.codigo == codigo }
    }

    fun requisitosCicloTPI(): List<RequisitoCiclo> {
        return requisitosCiclo.filter { !it.esTPI2010 && it.carrera == Carrera.P }
    }

    fun requisitosCicloTPI2010(): List<RequisitoCiclo> {
        return requisitosCiclo.filter { it.esTPI2010 && it.carrera == Carrera.P }
    }

    fun cumpleCorrelativas(alumno: Alumno): Boolean {
        return alumno.aproboTodas(this.correlativas)
    }

    fun requisitosCicloLI(): List<RequisitoCiclo> {
        return requisitosCiclo.filter { it.carrera == Carrera.W }
    }

    fun carrera(): Carrera {
        return if (tpi2015 != CicloTPI.NO_PERTENECE && li != CicloLI.NO_PERTENECE) {
            Carrera.PW
        } else if (tpi2015 == CicloTPI.NO_PERTENECE) {
            Carrera.W
        } else {
            Carrera.P
        }
    }

//            TODO: Ponerlo cuando se refactorice el seeder
//    init {
//        if (li == CicloLI.NO_PERTENECE && tpi == CicloTPI.NO_PERTENECE) {
//            throw ExcepcionUNQUE("Una materia debe pertenecer a una carrera")
//        }
//    }
}

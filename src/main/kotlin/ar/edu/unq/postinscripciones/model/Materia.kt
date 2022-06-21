package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
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
    @Enumerated(EnumType.STRING)
    val carrera: Carrera = Carrera.SIMULTANEIDAD
) {

    fun esLaMateria(materia: Materia) = this.codigo == materia.codigo

    fun actualizarCorrelativas(correlativasDadas: MutableList<Materia>) {
        correlativas = correlativasDadas
    }
}

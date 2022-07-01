package ar.edu.unq.postinscripciones.model

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
class Materia(
    @Id
    val codigo: String = "",
    @Column(unique=true, nullable = false)
    val nombre: String = "",
    @ManyToMany(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    var correlativas: MutableList<Materia> = mutableListOf(),
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val carrera: Carrera = Carrera.SIMULTANEIDAD
) {

    fun esLaMateria(materia: Materia) = this.codigo == materia.codigo

    fun actualizarCorrelativas(correlativasDadas: MutableList<Materia>) {
        correlativas.clear()
        correlativas.addAll(correlativasDadas)
    }

    fun quitarCorrelativa(codigo: String) {
        correlativas.removeIf { it.codigo == codigo }
    }
}

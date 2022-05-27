package ar.edu.unq.postinscripciones.model

import java.time.LocalDate
import javax.persistence.*

@Entity
class MateriaCursada(
        @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        val materia: Materia,
        @Enumerated(EnumType.STRING)
        var estado: EstadoMateria = EstadoMateria.PA,
        var fechaDeCarga: LocalDate = LocalDate.now()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null



    fun cambiarEstado(nuevoEstado: EstadoMateria) {
        estado = nuevoEstado
        fechaDeCarga = LocalDate.now()
    }
}
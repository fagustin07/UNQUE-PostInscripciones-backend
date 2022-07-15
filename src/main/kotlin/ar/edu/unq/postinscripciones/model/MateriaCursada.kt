package ar.edu.unq.postinscripciones.model

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDate
import javax.persistence.*

@Entity
class MateriaCursada(
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val materia: Materia,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var estado: EstadoMateria = EstadoMateria.PA,
    @Column(nullable = false)
    var fechaDeCarga: LocalDate = LocalDate.now()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null



    fun cambiarEstado(nuevoEstado: EstadoMateria) {
        estado = nuevoEstado
        fechaDeCarga = LocalDate.now()
    }

    fun esElResultado(materia: Materia, fecha: LocalDate, resultado: EstadoMateria): Boolean {
        return this.materia.codigo == materia.codigo && this.fechaDeCarga == fecha && resultado == this.estado
    }

    fun fueSubido(materia: Materia, fecha: LocalDate): Boolean {
        return this.materia.codigo == materia.codigo && this.fechaDeCarga == fecha
    }
}
package ar.edu.unq.postinscripciones.model.comision

import java.time.LocalTime
import javax.persistence.*

@Entity
class Horario(
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val dia: Dia,
    @Column(nullable = false)
    val inicio: LocalTime,
    @Column(nullable = false)
    val fin: LocalTime
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

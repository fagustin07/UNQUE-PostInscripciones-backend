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
    fun tieneSuperposicionCon(comision: Comision): Boolean {
        val coincidenDias = comision.horarios.filter { it.dia == this.dia }
        return coincidenDias.any { this.superponeHorario(it) || it.superponeHorario(this) }
    }

    private fun superponeHorario(it: Horario) =
        (it.inicio >= this.inicio && it.inicio < this.fin) ||
                (it.fin <= this.fin && it.fin > this.inicio)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

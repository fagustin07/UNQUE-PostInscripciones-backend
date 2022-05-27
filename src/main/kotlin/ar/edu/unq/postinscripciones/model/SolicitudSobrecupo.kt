package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import javax.persistence.*

@Entity
class SolicitudSobrecupo(
    @ManyToOne(fetch = FetchType.EAGER)
    val comision: Comision = Comision()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    var estado: EstadoSolicitud = EstadoSolicitud.PENDIENTE

    fun cambiarEstado(estado: EstadoSolicitud){
        this.estado = estado
    }

    fun solicitaLaComision(comision: Comision) = this.comision.esLaComision(comision)
}

enum class EstadoSolicitud {
    PENDIENTE, APROBADO, RECHAZADO
}

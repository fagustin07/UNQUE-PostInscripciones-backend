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
        val estadoAnterior = this.estado
        this.estado = estado
        estado.asignarODesasignarCupo(comision, estadoAnterior)
    }

    fun solicitaLaComision(comision: Comision) = this.comision.esLaComision(comision)
}

enum class EstadoSolicitud {
    PENDIENTE {
        override fun asignarODesasignarCupo(comision: Comision, estadoAnterior: EstadoSolicitud) {
            if(estadoAnterior === APROBADO) {
                comision.quitarSobrecupo()
            }
        }
    }, APROBADO {
        override fun asignarODesasignarCupo(comision: Comision, estadoAnterior: EstadoSolicitud) {
            comision.asignarSobrecupo()
        }
    }, RECHAZADO {
        override fun asignarODesasignarCupo(comision: Comision, estadoAnterior: EstadoSolicitud) {
            if(estadoAnterior === APROBADO) {
                comision.quitarSobrecupo()
            }
        }
    };
    abstract fun asignarODesasignarCupo(comision: Comision, estadoAnterior: EstadoSolicitud)
}

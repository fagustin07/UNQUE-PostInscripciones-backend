package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import javax.persistence.*

@Entity
class Formulario(
    @ManyToOne(fetch = FetchType.EAGER)
    val cuatrimestre: Cuatrimestre = Cuatrimestre(2009, Semestre.S1),
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val solicitudes: List<SolicitudSobrecupo> = listOf()
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Enumerated(EnumType.STRING)
    var estado = EstadoFormulario.ABIERTO

    fun cambiarEstado() {
        estado.cambiarEstado(this)
    }

    fun cerrarFormulario() {
        solicitudes.forEach {
            if (it.estado == EstadoSolicitud.PENDIENTE){
                it.cambiarEstado(EstadoSolicitud.RECHAZADO)
            }
        }
        estado = EstadoFormulario.CERRADO
    }

    fun abrirFormulario() {
        estado = EstadoFormulario.ABIERTO
    }

    fun tieneLaComision(comision: Comision) = solicitudes.any { it.solicitaLaComision(comision) }
}


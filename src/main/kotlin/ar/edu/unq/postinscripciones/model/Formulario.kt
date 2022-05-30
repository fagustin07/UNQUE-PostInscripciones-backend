package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import javax.persistence.*

@Entity
class Formulario(
    @ManyToOne(fetch = FetchType.EAGER)
    val cuatrimestre: Cuatrimestre = Cuatrimestre(2009, Semestre.S1),
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val solicitudes: MutableList<SolicitudSobrecupo> = mutableListOf(),
    @ManyToMany(fetch = FetchType.LAZY)
    val comisionesInscripto: List<Comision> = listOf()
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Enumerated(EnumType.STRING)
    var estado = EstadoFormulario.ABIERTO

    fun cambiarEstado() {
        estado.cambiarEstado(this)
    }

    fun agregarSolicitud(solicitud: SolicitudSobrecupo) {
        solicitudes.add(solicitud)
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

package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.formulario.ComentarioDTO
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Formulario(
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val cuatrimestre: Cuatrimestre = Cuatrimestre(2009, Semestre.S1),
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name="formulario_id")
    val solicitudes: MutableList<SolicitudSobrecupo> = mutableListOf(),
    @ManyToMany(fetch = FetchType.LAZY)
    val comisionesInscripto: MutableList<Comision> = mutableListOf(),
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    var estado = EstadoFormulario.ABIERTO

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var comentarios: MutableList<Comentario> = mutableListOf()

    init {
        checkNoHaySuperposiciones(solicitudes, comisionesInscripto)
    }

    fun agregarSolicitud(solicitud: SolicitudSobrecupo) {
        solicitudes.add(solicitud)
    }

    fun agregarComentarios(descripcion: String, titulo: String, fechaDeCarga: LocalDateTime = LocalDateTime.now()) {
        val comentario = Comentario(this, titulo, descripcion, fechaDeCarga)
        comentarios.add(comentario)
        comentarios.sortByDescending { it.fechaDeCarga }
    }

    fun cerrarFormulario() {
        solicitudes.forEach {
            if (it.estado == EstadoSolicitud.PENDIENTE) {
                it.cambiarEstado(EstadoSolicitud.RECHAZADO)
            }
        }
        estado = EstadoFormulario.CERRADO
    }

    fun abrirFormulario() {
        estado = EstadoFormulario.ABIERTO
    }

    fun tieneLaComision(comision: Comision) = solicitudes.any { it.solicitaLaComision(comision) }

    fun tieneAprobadaAlgunaDe(materia: Materia) =
        solicitudes.any { it.comision.materia.esLaMateria(materia) && it.estado == EstadoSolicitud.APROBADO }

    private fun checkNoHaySuperposiciones(solicitudes: List<SolicitudSobrecupo>, comisionesInscripto: List<Comision>) {
        val materiasSuperpuestas =
            solicitudes.filter { solicitud ->
                comisionesInscripto.any { comision ->
                    comision.materia.esLaMateria(solicitud.comision.materia)
                }
            }
        if (materiasSuperpuestas.isNotEmpty()) {
            throw ExcepcionUNQUE("No podes solicitar comisiones de materias " +
                    "en las que ya estas inscripto por Guaraní")
        }

        val horariosSuperpuestosGuarani =
            solicitudes.filter { solicitud ->
                comisionesInscripto.any { comision ->
                    comision.tieneSuperposicionHoraria(solicitud.comision)
                }
            }

        if(horariosSuperpuestosGuarani.isNotEmpty()) {
            throw ExcepcionUNQUE("Tenes solicitudes de sobrecupos de " +
                    "comisiones que se superponen con las que estas inscripto en guaraní")
        }

    }

    fun quitarInscripcionDe(id: Long) {
        comisionesInscripto.removeIf { it.id == id }
    }
}

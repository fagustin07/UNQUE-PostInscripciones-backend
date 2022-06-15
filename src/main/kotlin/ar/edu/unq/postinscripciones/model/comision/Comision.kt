package ar.edu.unq.postinscripciones.model.comision

import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import javax.persistence.*

@Entity
class Comision(
    @ManyToOne(fetch = FetchType.EAGER)
    val materia: Materia = Materia("", ""),
    @Column(nullable = false)
    val numero: Int = 1,
    @ManyToOne(fetch = FetchType.EAGER)
    val cuatrimestre: Cuatrimestre = Cuatrimestre(2009, Semestre.S1),
    @Column(nullable = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var horarios: List<Horario> = listOf(),
    @Column(nullable = false)
    val cuposTotales: Int = 30,
    @Column(nullable = false)
    val sobrecuposTotales: Int = 5,
    @Column(nullable = false)
    val modalidad: Modalidad = Modalidad.PRESENCIAL
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(nullable = false)
    private var sobrecuposOcupados = 0

    fun sobrecuposDisponibles() = sobrecuposTotales - sobrecuposOcupados

    fun modificarHorarios(nuevosHorarios: List<Horario>) {
        horarios = nuevosHorarios
    }

    fun asignarSobrecupo() {
        if(sobrecuposDisponibles() > 0) {
            sobrecuposOcupados ++
        } else {
            throw ExcepcionUNQUE("No hay sobrecupos disponibles")
        }

    }

    fun quitarSobrecupo() {
        if(sobrecuposOcupados > 0) {
            sobrecuposOcupados --
        } else {
            throw ExcepcionUNQUE("No hay sobrecupos ocupados")
        }

    }

    fun esLaComision(comision: Comision): Boolean {
        return cuatrimestre.esElCuatrimestre(comision.cuatrimestre) &&
                this.coincideEn(comision.materia, comision.numero)
    }

    private fun coincideEn(materia: Materia, numero: Int) =
        this.materia.esLaMateria(materia) && this.numero == numero

    fun tieneSuperposicionHoraria(comision: Comision): Boolean {
        return this.horarios.any { it.tieneSuperposicionCon(comision) }
    }
}
package ar.edu.unq.postinscripciones.model.comision

import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ErrorDeNegocio
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Locacion
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
class Comision(
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val materia: Materia = Materia("", ""),
    @Column(nullable = false)
    val numero: Int = 1,
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val cuatrimestre: Cuatrimestre = Cuatrimestre(2009, Semestre.S1),
    @Column(nullable = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name ="comision_id")
    var horarios: MutableList<Horario> = mutableListOf(),
    @Column(nullable = false)
    var cuposTotales: Int = 30,
    @Column(nullable = false)
    var sobrecuposTotales: Int = 5,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val modalidad: Modalidad = Modalidad.PRESENCIAL,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val locacion: Locacion = Locacion.Bernal
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(nullable = false)
    private var sobrecuposOcupados = 0

    fun sobrecuposDisponibles() = sobrecuposTotales - sobrecuposOcupados

    fun modificarHorarios(nuevosHorarios: List<Horario>) {
        horarios.clear()
        horarios.addAll(nuevosHorarios)
    }

    fun modificarCuposTotales(cupos: Int) {
        this.cuposTotales = cupos
    }

    fun modificarSobreuposTotales(sobrecupos: Int) {
        if(sobrecuposOcupados > sobrecupos) {
            throw ErrorDeNegocio(
                    "No se puede modificar la cantidad de sobrecupos " +
                            "dado que la cantidad de sobrecupos ocupados es mayor"
            )
        }
        this.sobrecuposTotales = sobrecupos
    }

    fun asignarSobrecupo() {
        if(sobrecuposDisponibles() > 0) {
            sobrecuposOcupados ++
        } else {
            throw ErrorDeNegocio("No hay sobrecupos disponibles")
        }

    }

    fun quitarSobrecupo() {
        if(sobrecuposOcupados > 0) {
            sobrecuposOcupados --
        } else {
            throw ErrorDeNegocio("No hay sobrecupos ocupados")
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
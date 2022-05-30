package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import javax.persistence.*

@Entity
class Alumno(
    @Id
    val dni: Int = 1234,
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    @Column(unique = true)
    val legajo: Int = 4,
    val contrasenia: String = "",
    @Enumerated(EnumType.STRING)
    val carrera: Carrera = Carrera.SIMULTANEIDAD,
) {
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    private val formularios: MutableList<Formulario> = mutableListOf()

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var historiaAcademica: MutableList<MateriaCursada> = mutableListOf()

    fun guardarFormulario(formulario: Formulario, cuatrimestre: Cuatrimestre = Cuatrimestre.actual()) {
        if(existeFormulario(cuatrimestre)) {
            chequearSiSePuedeModificar(cuatrimestre.anio, cuatrimestre.semestre)
            formularios.removeIf { it.cuatrimestre.esElCuatrimestre(cuatrimestre.anio,cuatrimestre.semestre) }
        }
        formularios.add(formulario)
    }

    fun chequearSiSePuedeModificar(anio: Int, semestre: Semestre) {
        if(estaCerradoElFormularioActual(anio,semestre)) {
            throw ExcepcionUNQUE("El formulario se encuentra cerrado a modificaciones")
        }
    }
    fun existeFormulario(cuatrimestre: Cuatrimestre): Boolean {
        return formularios.any { formulario -> formulario.cuatrimestre.esElCuatrimestre(cuatrimestre) }
    }

    fun estaCerradoElFormularioActual(anio: Int, semestre: Semestre): Boolean {
        val formularioActual = obtenerFormulario(anio,semestre)
        return formularioActual.estado === EstadoFormulario.CERRADO
    }

    fun cargarHistoriaAcademica(materiaCursada: MateriaCursada) {
        historiaAcademica.add(materiaCursada)
        historiaAcademica.sortByDescending { it.fechaDeCarga }
    }

    fun actualizarHistoriaAcademica(historia: List<MateriaCursada>) {
        historiaAcademica = historia.toMutableList()
    }

    fun obtenerFormulario(anio: Int, semestre: Semestre): Formulario {
        val formulario = formularios.find { it.cuatrimestre.esElCuatrimestre(anio, semestre) }
        return formulario ?: throw ExcepcionUNQUE("No se encontró ningun formulario para el cuatrimestre dado")
    }

    fun haSolicitado(unaComision: Comision): Boolean {
        return formularios.any { formulario -> formulario.tieneLaComision(unaComision) }
    }

    fun llenoElFormularioDelCuatrimestre(cuatrimestre: Cuatrimestre): Boolean {
        return formularios.any { formulario -> formulario.cuatrimestre.esElCuatrimestre(cuatrimestre) }
    }

    fun materiasCursadasPorEstadoDeMateria(estadoMateria: EstadoMateria): List<MateriaCursada> {
        return historiaAcademica.filter { it.estado == estadoMateria }
    }

    fun materiasAprobadas(): List<Materia> {
        return materiasCursadasPorEstadoDeMateria(EstadoMateria.APROBADO).map { it.materia }
    }

    fun cantidadAprobadas() = historiaAcademica.count { it.estado == EstadoMateria.APROBADO }

    fun puedeCursar(solicitudes : List<Materia>, materiasDisponibles: List<String>) : Boolean {
        return solicitudes.all { materiasDisponibles.contains(it.codigo) }
    }
}
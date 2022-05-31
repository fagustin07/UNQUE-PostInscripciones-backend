package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import java.time.LocalDateTime
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
    var contrasenia: String = "",
    @Enumerated(EnumType.STRING)
    val carrera: Carrera = Carrera.SIMULTANEIDAD,
) {
    var codigo: Int? = null
    var cargaDeCodigo: LocalDateTime? = null
    @Enumerated(EnumType.STRING)
    var estadoCuenta: EstadoCuenta = EstadoCuenta.SIN_CONFIRMAR

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    private val formularios: MutableList<Formulario> = mutableListOf()

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var historiaAcademica: MutableList<MateriaCursada> = mutableListOf()

    fun guardarFormulario(formulario: Formulario) {
        chequearSiExiste(formulario)
        formularios.add(formulario)
    }

    fun cargarHistoriaAcademica(materiaCursada: MateriaCursada) {
        historiaAcademica.add(materiaCursada)
        historiaAcademica.sortByDescending { it.fechaDeCarga }
    }

    fun cambiarFormulario(anio: Int, semestre: Semestre, formulario: Formulario) {
        formularios.removeIf { it.cuatrimestre.esElCuatrimestre(anio,semestre) }
        formularios.add(formulario)
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

    fun yaGuardoUnFormulario(cuatrimestre: Cuatrimestre): Boolean {
        return formularios.any { formulario -> formulario.cuatrimestre.esElCuatrimestre(cuatrimestre) }
    }

    fun actualizarCodigoYContrasenia(codigo: Int, contrasenia: String, confirmarContrasenia: String, horaDeCarga: LocalDateTime) {
        checkEstadoCuenta()
        checkTiempoDeCodigo(horaDeCarga)
        if (contrasenia != confirmarContrasenia) throw ExcepcionUNQUE("La contrasenia no coincide.")

        this.cargaDeCodigo = horaDeCarga
        this.codigo = codigo
        this.contrasenia = contrasenia
    }

    fun confirmarCuenta(codigo: Int, carga: LocalDateTime) {
        if (cargaDeCodigo == null) throw ExcepcionUNQUE("Cree su cuenta. Si el problema persiste, comuniquese con el equipo directivo")
        this.checkEstadoCuenta()
        this.checkTiempoConfirmacionCodigo(carga)

        if (codigo == this.codigo) {
            this.estadoCuenta = EstadoCuenta.CONFIRMADA
        } else {
            throw ExcepcionUNQUE("Codigo incorrecto. Intente nuevamente")
        }
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

    private fun chequearSiExiste(formulario: Formulario) {
        if (yaGuardoUnFormulario(formulario.cuatrimestre)) {
            throw ExcepcionUNQUE("Ya has guardado un formulario para este cuatrimestre")
        }
    }

    private fun checkEstadoCuenta() {
        if (this.estadoCuenta == EstadoCuenta.CONFIRMADA) throw ExcepcionUNQUE("Ya posees una cuenta")
    }

    private fun checkTiempoDeCodigo(horaDeCarga: LocalDateTime) {
        if (this.cargaDeCodigo!= null && horaDeCarga.isBefore(this.cargaDeCodigo!!.plusMinutes(30))) {
            throw ExcepcionUNQUE("Usted posee un codigo que no expiró. " +
                    "Revise su correo y confirme su cuenta con el codigo dado")
        }
    }

    private fun checkTiempoConfirmacionCodigo(horaDeCarga: LocalDateTime) {
        if (this.cargaDeCodigo!= null && horaDeCarga.isAfter(this.cargaDeCodigo!!.plusMinutes(30))) {
            throw ExcepcionUNQUE("Su codigo ha expirado. Cree su cuenta nuevamente")
        }
    }
}

enum class EstadoCuenta {
    CONFIRMADA, SIN_CONFIRMAR
}
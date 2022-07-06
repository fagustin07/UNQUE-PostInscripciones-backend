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
    @Column(nullable = false)
    val nombre: String = "",
    @Column(nullable = false)
    val apellido: String = "",
    @Column(nullable = false)
    val correo: String = "",
    @Column(unique = true, nullable = false)
    val legajo: Int = 4,
    @Column(nullable = false)
    var contrasenia: String = "",
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val carrera: Carrera = Carrera.PW,
    @Column(nullable = false)
    var coeficiente: Double = 3.0,
    @Column(nullable = false)
    var cursaTPI2010: Boolean = false
) {
    var codigo: Int? = null
    var cargaDeCodigo: LocalDateTime? = null
    @Enumerated(EnumType.STRING)
    var estadoCuenta: EstadoCuenta = EstadoCuenta.SIN_CONFIRMAR
    @Enumerated(EnumType.STRING)
    val rol = Role.ROLE_ALUMNO

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    private val formularios: MutableList<Formulario> = mutableListOf()

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name="alumno_dni")
    var historiaAcademica: MutableList<MateriaCursada> = mutableListOf()

    fun guardarFormulario(formulario: Formulario) {
        chequearSiExiste(formulario)
        formularios.add(formulario)
    }

    fun borrarFormulario(anio: Int, semestre: Semestre) {
        formularios.removeIf { it.cuatrimestre.esElCuatrimestre(anio, semestre) }
    }

    fun cargarHistoriaAcademica(materiaCursada: MateriaCursada) {
        historiaAcademica.add(materiaCursada)
        historiaAcademica.sortByDescending { it.fechaDeCarga }
    }

    fun cambiarFormulario(anio: Int, semestre: Semestre, formulario: Formulario) {
        borrarFormulario(anio, semestre)
        formularios.add(formulario)
    }

    fun actualizarHistoriaAcademica(historia: List<MateriaCursada>) {
        historiaAcademica.clear()
        historiaAcademica.addAll(historia)
        historiaAcademica.sortByDescending { it.fechaDeCarga }
    }

    fun obtenerFormulario(anio: Int, semestre: Semestre): Formulario {
        val formulario = formularios.find { it.cuatrimestre.esElCuatrimestre(anio, semestre) }
        return formulario ?: throw ExcepcionUNQUE("No se encontró ningun formulario para el cuatrimestre dado")
    }

    fun agregarSolicitud(comision: Comision, cuatrimestre: Cuatrimestre): Formulario {
        if (tieneAprobado(comision.materia)) {
            throw ExcepcionUNQUE("El alumno ya ha aprobado la materia ${comision.materia.nombre}")
        }

        val formulario = this.obtenerFormulario(cuatrimestre.anio, cuatrimestre.semestre)
        if (formulario.comisionesInscripto.any { it.materia.esLaMateria(comision.materia) }) {
            throw ExcepcionUNQUE("El alumno ya se encuentra inscripto por Guaraní a la materia ${comision.materia.nombre} este cuatrimestre")
        }

        if (formulario.solicitudes.any { it.solicitaLaComision(comision) }) {
            throw ExcepcionUNQUE("El alumno ya ha solicitado la comision ${comision.numero} de la materia ${comision.materia.nombre} este cuatrimestre")
        }

        val solicitud = SolicitudSobrecupo(comision)
        formulario.agregarSolicitud(solicitud)

        return formulario
    }

    fun haSolicitado(unaComision: Comision): Boolean {
        return formularios.any { formulario -> formulario.tieneLaComision(unaComision) }
    }

    fun yaGuardoUnFormulario(cuatrimestre: Cuatrimestre): Boolean {
        return formularios.any { formulario -> formulario.cuatrimestre.esElCuatrimestre(cuatrimestre) }
    }

    fun actualizarCodigoYContrasenia(codigo: Int, contrasenia: String, horaDeCarga: LocalDateTime) {
        checkEstadoCuenta()
        checkTiempoDeCodigo(horaDeCarga)

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
        return materiasCursadasPorEstadoDeMateria(EstadoMateria.APROBADO).map { it.materia } + materiasCursadasPorEstadoDeMateria(EstadoMateria.PA).map { it.materia }
    }

    fun cantidadAprobadas() = historiaAcademica.count { it.estado == EstadoMateria.APROBADO }

    fun haAprobado(materia: Materia) = this.materiasAprobadas().any { it.esLaMateria(materia) }

    fun puedeCursar(solicitudes : List<Materia>, materiasDisponibles: List<String>) : Boolean {
        return solicitudes.all { materiasDisponibles.contains(it.codigo) }
    }

    fun cambiarCoeficiente(coeficiente: Double) {
        this.coeficiente = coeficiente
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
        if (this.cargaDeCodigo!= null && horaDeCarga.isAfter(this.cargaDeCodigo!!.plusMinutes(5))) {
            throw ExcepcionUNQUE("Su codigo ha expirado. Cree su cuenta nuevamente")
        }
    }

    private fun tieneAprobado(materia: Materia) =
        this.historiaAcademica.any { it.materia.esLaMateria(materia) && it.estado == EstadoMateria.APROBADO }

    fun aproboTodas(correlativas: List<Materia>): Boolean {
        val materiasAprobadas = this.materiasAprobadas()

        return correlativas.all { correlativa -> materiasAprobadas.any { it.esLaMateria(correlativa) } }
    }

    fun creditosParaCicloDeTPI(cicloTPI: CicloTPI): Int {
        return if (!this.cursaTPI2010) {
            this.materiasAprobadas().filter { it.tpi2015 == cicloTPI }.sumOf { it.creditos }
        } else {
            0
        }
    }

    fun creditosParaCicloDeLI(cicloLI: CicloLI): Int {
        return this.materiasAprobadas().filter { it.li == cicloLI }.sumOf { it.creditos }
    }

    fun creditosParaCicloDeTPI2010(cicloTPI: CicloTPI): Int {
        return if (this.cursaTPI2010) {
            this.materiasAprobadas().filter { it.tpi2010 == cicloTPI }.sumOf { it.creditos }
        } else {
            0
        }
    }
}

enum class EstadoCuenta {
    CONFIRMADA, SIN_CONFIRMAR
}
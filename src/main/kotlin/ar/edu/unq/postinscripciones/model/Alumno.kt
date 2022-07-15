package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ErrorDeNegocio
import ar.edu.unq.postinscripciones.service.dto.carga.datos.AlumnoCarga
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Locacion
import java.time.LocalDate
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
    @Column(nullable = false)
    var contrasenia: String = "",
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var carrera: Carrera = Carrera.PW,
    @Column(nullable = false)
    var cursaTPI2010: Boolean = false,
    @Enumerated(EnumType.STRING)
    val locacion: Locacion = Locacion.Bernal,
    var estadoInscripcion: EstadoInscripcion = EstadoInscripcion.Aceptado,
    var calidad: Calidad = Calidad.Activo,
    var regular: Regular = Regular.S
) {
    var codigo: Int? = null
    var cargaDeCodigo: LocalDateTime? = null

    @Enumerated(EnumType.STRING)
    var estadoCuenta: EstadoCuenta = EstadoCuenta.SIN_CONFIRMAR

    @Enumerated(EnumType.STRING)
    val rol = Role.ROLE_ALUMNO

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val formularios: MutableList<Formulario> = mutableListOf()

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "alumno_dni")
    var historiaAcademica: MutableList<MateriaCursada> = mutableListOf()

    fun guardarFormulario(formulario: Formulario) {
        chequearSiExiste(formulario)
        formularios.add(formulario)
    }

    fun obtenerHistorialSolicitudes(cuatrimestre: Cuatrimestre): List<SolicitudSobrecupo> {
        return formularios.filter { !it.cuatrimestre.esElCuatrimestre(cuatrimestre) }.flatMap { it.solicitudes }
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
        return formulario ?: throw ErrorDeNegocio("No se encontró ningun formulario para el cuatrimestre dado")
    }

    fun agregarSolicitud(comision: Comision, cuatrimestre: Cuatrimestre): Formulario {
        if (tieneAprobado(comision.materia)) {
            throw ErrorDeNegocio("El alumno ya ha aprobado la materia ${comision.materia.nombre}")
        }

        val formulario = this.obtenerFormulario(cuatrimestre.anio, cuatrimestre.semestre)
        if (formulario.comisionesInscripto.any { it.materia.esLaMateria(comision.materia) }) {
            throw ErrorDeNegocio("El alumno ya se encuentra inscripto por Guaraní a la materia ${comision.materia.nombre} este cuatrimestre")
        }

        if (formulario.solicitudes.any { it.solicitaLaComision(comision) }) {
            throw ErrorDeNegocio("El alumno ya ha solicitado la comision ${comision.numero} de la materia ${comision.materia.nombre} este cuatrimestre")
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
        if (cargaDeCodigo == null) throw ErrorDeNegocio("Cree su cuenta. Si el problema persiste, comuniquese con el equipo directivo")
        this.checkEstadoCuenta()
        this.checkTiempoConfirmacionCodigo(carga)

        if (codigo == this.codigo) {
            this.estadoCuenta = EstadoCuenta.CONFIRMADA
        } else {
            throw ErrorDeNegocio("Codigo incorrecto. Intente nuevamente")
        }
    }

    fun materiasCursadasPorEstadoDeMateria(estadoMateria: EstadoMateria): List<MateriaCursada> {
        return historiaAcademica.filter { it.estado == estadoMateria }
    }

    fun materiasAprobadas(): List<Materia> {
        return materiasCursadasPorEstadoDeMateria(EstadoMateria.APROBADO).map { it.materia } + materiasCursadasPorEstadoDeMateria(
            EstadoMateria.PA
        ).map { it.materia }
    }

    fun esRegular() = calidad == Calidad.Activo && regular == Regular.S && estadoInscripcion == EstadoInscripcion.Aceptado
    fun cantidadAprobadas() = historiaAcademica.count { it.estado == EstadoMateria.APROBADO }

    fun haAprobado(materia: Materia) = this.materiasAprobadas().any { it.esLaMateria(materia) }

    fun puedeCursar(solicitudes: List<Materia>, materiasDisponibles: List<String>): Boolean {
        return solicitudes.all { materiasDisponibles.contains(it.codigo) }
    }

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

    fun agregarMateriaCursada(materia: Materia, fecha: LocalDate, resultado: EstadoMateria) {
        this.historiaAcademica.removeIf { it.fueSubido(materia, fecha) }
        val fueCargada = this.historiaAcademica.any { it.esElResultado(materia, fecha, resultado) }

        if (!fueCargada) {
            if (resultado != EstadoMateria.APROBADO || !this.haAprobado(materia)) {
                this.historiaAcademica.add(MateriaCursada(materia, resultado, fecha))
            }
        }
    }

    fun actualizarDatos(datosActualizados: AlumnoCarga) {
        this.calidad = datosActualizados.calidad
        this.regular = datosActualizados.regular
        this.estadoInscripcion = datosActualizados.estado
        actualizarCarrera(datosActualizados)
    }

    private fun chequearSiExiste(formulario: Formulario) {
        if (yaGuardoUnFormulario(formulario.cuatrimestre)) {
            throw ErrorDeNegocio("Ya has guardado un formulario para este cuatrimestre")
        }
    }

    private fun checkEstadoCuenta() {
        if (this.estadoCuenta == EstadoCuenta.CONFIRMADA) throw ErrorDeNegocio("Ya posees una cuenta")
    }

    private fun checkTiempoDeCodigo(horaDeCarga: LocalDateTime) {
        if (this.cargaDeCodigo != null && horaDeCarga.isBefore(this.cargaDeCodigo!!.plusMinutes(30))) {
            throw ErrorDeNegocio(
                "Usted posee un codigo que no expiró. " +
                        "Revise su correo y confirme su cuenta con el codigo dado"
            )
        }
    }

    private fun checkTiempoConfirmacionCodigo(horaDeCarga: LocalDateTime) {
        if (this.cargaDeCodigo != null && horaDeCarga.isAfter(this.cargaDeCodigo!!.plusMinutes(5))) {
            throw ErrorDeNegocio("Su codigo ha expirado. Cree su cuenta nuevamente")
        }
    }

    private fun tieneAprobado(materia: Materia) =
        this.historiaAcademica.any { it.materia.esLaMateria(materia) && it.estado == EstadoMateria.APROBADO }

    private fun actualizarCarrera(datosActualizados: AlumnoCarga) {
        if (datosActualizados.propuesta == Carrera.P && this.carrera == Carrera.W) {
            this.carrera = Carrera.PW
            this.cursaTPI2010 = datosActualizados.plan == 2010
        }
        if (datosActualizados.propuesta == Carrera.W && this.carrera == Carrera.P) {
            this.carrera = Carrera.PW
        }

        if (datosActualizados.propuesta == Carrera.P && this.carrera == Carrera.P) {
            this.cursaTPI2010 = datosActualizados.plan == 2010
        }
    }

    fun cumpleLocacion(comision: Comision): Boolean {
        return if(this.locacion == Locacion.General_Belgrano){
            comision.locacion == Locacion.General_Belgrano
        } else if(comision.locacion == Locacion.General_Belgrano){
            this.locacion == Locacion.General_Belgrano
        } else {
            true
        }
    }
}

enum class EstadoCuenta {
    CONFIRMADA, SIN_CONFIRMAR
}
package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FormularioTest {
    lateinit var cuatrimestre: Cuatrimestre
    lateinit var formulario: Formulario
    lateinit var solicitud: SolicitudSobrecupo
    @BeforeEach
    fun `set up`() {
        solicitud = SolicitudSobrecupo()
        cuatrimestre = Cuatrimestre(2010, Semestre.S1)
        formulario = Formulario(cuatrimestre = cuatrimestre, solicitudes = mutableListOf(solicitud))
    }

    @Test
    fun `un formulario puede agregar una solicitud de cupo`() {
        assertThat(formulario.solicitudes).containsExactly(solicitud)
    }

    @Test
    fun `se puede agregar solicitudes a un formulario`() {
        val solicitud2 = SolicitudSobrecupo()
        formulario.agregarSolicitud(solicitud2)
        assertThat(formulario.solicitudes).containsExactly(solicitud, solicitud2)
    }

    @Test
    fun `se puede agregar las materias en las que se encuentra inscripto un alumno`() {
        val materia = Materia("BDD-102", "Base de datos")
        val comision = Comision(materia)
        formulario = Formulario(comisionesInscripto = listOf(comision))

        assertThat(formulario.comisionesInscripto).containsExactly(comision)
    }

    @Test
    fun `un formulario conoce su cuatrimestre`() {
        assertThat(formulario.cuatrimestre).isEqualTo(cuatrimestre)
    }

    @Test
    fun `un formulario inicialmente se encuentra abierto`() {
        assertThat(formulario.estado).isEqualTo(EstadoFormulario.ABIERTO)
    }

    @Test
    fun `se puede cerrar un formulario`() {
        formulario.cambiarEstado()
        assertThat(formulario.estado).isEqualTo(EstadoFormulario.CERRADO)
    }

    @Test
    fun `se puede abrir un formulario ya cerrado`() {
        formulario.cambiarEstado()
        val estadoDelFormularioDespuesDeCambiarEstado = formulario.estado
        formulario.cambiarEstado()

        assertThat(estadoDelFormularioDespuesDeCambiarEstado).isEqualTo(EstadoFormulario.CERRADO)
        assertThat(formulario.estado).isEqualTo(EstadoFormulario.ABIERTO)
    }

    @Test
    fun `cuando se cierra un formulario todas las solicitudes pendientes se rechazan`() {
        val solicitudes = mutableListOf(SolicitudSobrecupo(), SolicitudSobrecupo())
        val formulario = Formulario(cuatrimestre = cuatrimestre, solicitudes = solicitudes)

        formulario.cambiarEstado()

        assertThat(formulario.solicitudes.map { it.estado })
                .usingRecursiveComparison()
                .isEqualTo(listOf(EstadoSolicitud.RECHAZADO, EstadoSolicitud.RECHAZADO))
    }

    @Test
    fun `cuando se cierra un formulario todas las solicitudes aprobadas no se rechazan`() {
        val solicitudes = mutableListOf(SolicitudSobrecupo(), SolicitudSobrecupo())
        solicitudes.first().cambiarEstado(EstadoSolicitud.APROBADO)
        val formulario = Formulario(cuatrimestre = cuatrimestre, solicitudes = solicitudes)

        formulario.cambiarEstado()

        assertThat(formulario.solicitudes.map { it.estado })
                .usingRecursiveComparison()
                .isEqualTo(listOf(EstadoSolicitud.APROBADO, EstadoSolicitud.RECHAZADO))
    }
}

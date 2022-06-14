package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.comision.Comision
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SolicitudSobrecupoTest {

    lateinit var solicitud: SolicitudSobrecupo
    lateinit var comision: Comision

    @BeforeEach
    fun `set up`() {
        comision = Comision()
        solicitud = SolicitudSobrecupo(comision)
    }

    @Test
    fun `una solicitud conoce su comision`() {
        assertThat(solicitud.comision).isEqualTo(comision)
    }

    @Test
    fun `una solicitud tiene estado PENDIENTE al crearse`() {
        assertThat(solicitud.estado).isEqualTo(EstadoSolicitud.PENDIENTE)
    }

    @Test
    fun `se puede cambiar el estado de una solicitud a APROBADO`() {
        solicitud.cambiarEstado(EstadoSolicitud.APROBADO)
        assertThat(solicitud.estado).isEqualTo(EstadoSolicitud.APROBADO)
    }

    @Test
    fun `una solicitud puede cambiar su estado a RECHAZADO`() {
        solicitud.cambiarEstado(EstadoSolicitud.RECHAZADO)
        assertThat(solicitud.estado).isEqualTo(EstadoSolicitud.RECHAZADO)
    }

    @Test
    fun `cuando una solicitud cambia su estado a APROBADO el conteo de sobrecupos baja en uno`() {
        val sobrecuposAntes = solicitud.comision.sobrecuposDisponibles()
        solicitud.cambiarEstado(EstadoSolicitud.APROBADO)
        assertThat(solicitud.comision.sobrecuposDisponibles()).isEqualTo(sobrecuposAntes - 1)
    }

    @Test
    fun `cuando una solicitud cambia su estado de APROBADO a RECHAZADO el conteo de sobrecupos sube en uno`() {
        solicitud.cambiarEstado(EstadoSolicitud.APROBADO)
        val sobrecuposAntes = solicitud.comision.sobrecuposDisponibles()

        solicitud.cambiarEstado(EstadoSolicitud.RECHAZADO)

        assertThat(solicitud.comision.sobrecuposDisponibles()).isEqualTo(sobrecuposAntes + 1)
    }

    @Test
    fun `cuando una solicitud cambia su estado de APROBADO a PENDIENTE el conteo de sobrecupos sube en uno`() {
        solicitud.cambiarEstado(EstadoSolicitud.APROBADO)
        val sobrecuposAntes = solicitud.comision.sobrecuposDisponibles()

        solicitud.cambiarEstado(EstadoSolicitud.PENDIENTE)

        assertThat(solicitud.comision.sobrecuposDisponibles()).isEqualTo(sobrecuposAntes + 1)
    }
}

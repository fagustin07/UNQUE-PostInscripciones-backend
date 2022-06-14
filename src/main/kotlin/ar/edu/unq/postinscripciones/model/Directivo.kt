package ar.edu.unq.postinscripciones.model

import javax.persistence.*

@Entity
class Directivo(
    @Id
    val correo: String= "",
    @Column(nullable = false)
    val nombre: String = "",
    @Column(nullable = false)
    val contrasenia: String = ""
) {
    @Enumerated(EnumType.STRING)
    val rol = Role.ROLE_DIRECTIVO

    fun decidir(solicitudSobrecupo: SolicitudSobrecupo, decision: EstadoSolicitud) {
        solicitudSobrecupo.estado = decision
    }
}
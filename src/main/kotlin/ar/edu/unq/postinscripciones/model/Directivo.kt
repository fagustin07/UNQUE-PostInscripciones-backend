package ar.edu.unq.postinscripciones.model

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
class Directivo(
    @Id
    val correo: String= "",
    val nombre: String = "",
    val contrasenia: String = ""
) {
    @Enumerated(EnumType.STRING)
    val rol = Role.ROLE_DIRECTIVO

    fun decidir(solicitudSobrecupo: SolicitudSobrecupo, decision: EstadoSolicitud) {
        solicitudSobrecupo.estado = decision
    }
}
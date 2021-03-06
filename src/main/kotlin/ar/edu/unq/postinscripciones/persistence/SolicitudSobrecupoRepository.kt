package ar.edu.unq.postinscripciones.persistence

import ar.edu.unq.postinscripciones.model.SolicitudSobrecupo
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.Tuple

@Repository
interface SolicitudSobrecupoRepository: CrudRepository<SolicitudSobrecupo, Long> {

    @Query(
            "SELECT ss.id, ss.formulario_id " +
            "FROM solicitud_sobrecupo ss " +
            "JOIN comision AS c ON c.id = ss.comision_id " +
            "WHERE estado = 'PENDIENTE' AND c.materia_codigo = :codigo AND (:numero IS NULL OR c.numero = :numero)",
            nativeQuery = true
    )
    fun findByMateria(codigo: String, numero: Int?): List<Tuple>
}
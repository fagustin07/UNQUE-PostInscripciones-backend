package ar.edu.unq.postinscripciones.persistence

import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.Tuple

@Repository
interface ComisionRespository : CrudRepository<Comision, Long> {

    fun findAllByMateriaAndCuatrimestreAnioAndCuatrimestreSemestre(
        materia: Materia,
        cuatrimestre_anio: Int,
        cuatrimestre_semestre: Semestre
    ): List<Comision>
    fun findByCuatrimestreAnioAndCuatrimestreSemestre(anio: Int, semestre: Semestre): List<Comision>
    fun findByNumeroAndMateriaAndCuatrimestre(
        numeroComision: Int,
        materia: Materia,
        cuatrimestre: Cuatrimestre
    ): Optional<Comision>

    @Query(
        "SELECT c.id, c.numero, c.materia.nombre, count(s), c.sobrecuposTotales, (c.sobrecuposTotales - c.sobrecuposOcupados) " +
                "FROM Comision AS c " +
                "LEFT JOIN SolicitudSobrecupo AS s " +
                "ON s.comision.id = c.id " +
                "WHERE c.cuatrimestre.anio = ?1 " +
                "AND c.cuatrimestre.semestre = ?2 " +
                "GROUP BY c.id " +
                "ORDER BY count(s) DESC"
    )
    fun findByCuatrimestreAnioAndCuatrimestreSemestreOrderByCountSolicitudes(anio: Int, semestre: Semestre): List<Tuple>
}
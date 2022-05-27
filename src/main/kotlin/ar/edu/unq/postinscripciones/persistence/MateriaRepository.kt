package ar.edu.unq.postinscripciones.persistence

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.Tuple

@Repository
interface MateriaRepository: CrudRepository<Materia, String> {

    fun findMateriaByCodigo(codigo: String): Optional<Materia>
    fun findByNombreIgnoringCaseOrCodigoIgnoringCase(nombre: String, codigo: String): Optional<Materia>
    fun findAllByCodigoIn(materias: List<String>): List<Materia>
    @Query(
        "SELECT m.codigo, m.nombre, com " +
        "FROM Materia as m " +
        "LEFT JOIN Comision as com ON com.materia.codigo = m.codigo " +
        "WHERE (m NOT IN ?1) " +
        "AND ((SELECT count(c) FROM m.correlativas as c WHERE c NOT IN ?1) = 0) " +
        "AND (m.carrera = ?2 OR (m.carrera = ar.edu.unq.postinscripciones.model.Carrera.SIMULTANEIDAD)) " +
        "AND com.cuatrimestre.anio = ?3 AND com.cuatrimestre.semestre = ?4 "
    )
    fun findMateriasDisponibles(materiasAprobadas : List<Materia>, carreraAlumno: Carrera, anio: Int, semestre: Semestre) : List<Tuple>

}

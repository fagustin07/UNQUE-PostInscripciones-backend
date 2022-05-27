package ar.edu.unq.postinscripciones.persistence

import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.MateriaCursada
import org.springframework.data.repository.CrudRepository
import java.util.*

interface MateriaCursadaRepository: CrudRepository<MateriaCursada, Long> {
    fun findByMateria(materia: Materia): Optional<MateriaCursada>
}
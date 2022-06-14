package ar.edu.unq.postinscripciones.persistence

import ar.edu.unq.postinscripciones.model.Directivo
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DirectivoRepository: CrudRepository<Directivo, String> {

    fun findByCorreo(correo: String): Directivo?

}
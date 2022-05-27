package ar.edu.unq.postinscripciones.persistence

import ar.edu.unq.postinscripciones.model.Formulario
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FormularioRepository: CrudRepository<Formulario, Long>
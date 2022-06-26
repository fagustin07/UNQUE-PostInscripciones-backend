package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Comentario(
        @ManyToOne(fetch = FetchType.EAGER)
        val formulario: Formulario = Formulario(Cuatrimestre.actual(), mutableListOf(), mutableListOf()),
        val autor: String = "",
        val descripcion: String = "",
        val fechaDeCarga: LocalDateTime = LocalDateTime.now()
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

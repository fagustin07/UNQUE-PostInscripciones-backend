package ar.edu.unq.postinscripciones.service.dto.formulario

import ar.edu.unq.postinscripciones.model.Comentario
import java.time.LocalDateTime

data class ComentarioDTO(
        val titulo: String,
        val descrpcion: String,
        val fecha: LocalDateTime
){
    companion object {
        fun desdeModelo(comentario: Comentario): ComentarioDTO {
            return ComentarioDTO(
                    comentario.titulo,
                    comentario.descripcion,
                    comentario.fechaDeCarga
            )
        }
    }
}
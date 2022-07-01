package ar.edu.unq.postinscripciones.service.dto.formulario

import ar.edu.unq.postinscripciones.model.Comentario
import java.time.LocalDateTime

data class ComentarioDTO(
        val autor: String,
        val descripcion: String,
        val fecha: LocalDateTime
){
    companion object {
        fun desdeModelo(comentario: Comentario): ComentarioDTO {
            return ComentarioDTO(
                    comentario.autor,
                    comentario.descripcion,
                    comentario.fechaDeCarga
            )
        }
    }
}
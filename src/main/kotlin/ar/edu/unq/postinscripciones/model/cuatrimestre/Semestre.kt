package ar.edu.unq.postinscripciones.model.cuatrimestre

import java.time.LocalDate

enum class Semestre {
    S1, S2;

    companion object {
        fun actual() = if (LocalDate.now().monthValue <= 6) {
            S1
        } else {
            S2
        }
    }
}
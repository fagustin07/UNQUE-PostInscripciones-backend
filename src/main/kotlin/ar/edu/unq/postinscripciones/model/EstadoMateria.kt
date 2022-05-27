package ar.edu.unq.postinscripciones.model

enum class EstadoMateria {
    APROBADO, DESAPROBADO, PA;

    companion object {
        fun desdeString(estadoString: String) = valueOf(estadoString)
    }

}
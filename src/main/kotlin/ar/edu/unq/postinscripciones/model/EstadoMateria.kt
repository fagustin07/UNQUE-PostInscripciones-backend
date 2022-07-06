package ar.edu.unq.postinscripciones.model

enum class EstadoMateria {
    APROBADO, DESAPROBADO, PA, AUSENTE;

    companion object {
        fun desdeString(estadoString: String) = valueOf(estadoString)
    }

}
package ar.edu.unq.postinscripciones.model.exception

abstract class ExcepcionUNQUE(mensaje: String) : RuntimeException(mensaje){
    fun toMap(): Map<String, String> {
        return mapOf(
                Pair("exception", this.javaClass.simpleName),
                Pair("message", this.message!!)
        )
    }
}
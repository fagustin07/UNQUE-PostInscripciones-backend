package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE

enum class Role {
    ROLE_ALUMNO, ROLE_DIRECTIVO;

    companion object {
        fun fromString(roleString: String): Role {
            return when (roleString.uppercase()) {
                ROLE_ALUMNO.toString() -> ROLE_ALUMNO
                ROLE_DIRECTIVO.toString() -> ROLE_DIRECTIVO
                else -> throw RolInvalido(roleString)
            }
        }
    }
}

class RolInvalido(invalido: String): ExcepcionUNQUE("Rol ${invalido} es invalido")
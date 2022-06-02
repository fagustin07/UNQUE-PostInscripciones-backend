package ar.edu.unq.postinscripciones.webservice.config

import ar.edu.unq.postinscripciones.model.Alumno
import ar.edu.unq.postinscripciones.model.Directivo
import ar.edu.unq.postinscripciones.model.Role
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTTokenUtil {
    @Value("\${jwt.secret}")
    lateinit var secret: String

    fun generarTokenAlumno(alumno: Alumno): String {
        val token = Jwts
            .builder()
            .setSubject("Postinscripciones JWTToken")
            .claim("alumno", alumno.dni.toString())
            .claim("authorities", listOf(alumno.rol.toString()))
            .setIssuedAt(Date(System.currentTimeMillis()))
            //el token de un alumno vence cada 3 dias.
            .setExpiration(Date(System.currentTimeMillis().plus(1000 * 60 * 60 * 24 * 3)))
            .signWith(
                SignatureAlgorithm.HS512,
                Base64.getEncoder().encode(this.secret.toByteArray())
            ).compact()

        return "Bearer $token"
    }

    fun generarTokenDirectivo(directivo: Directivo): String {
        val token = Jwts
            .builder()
            .setSubject("Postinscripciones JWTToken")
            .claim("directivo", directivo.correo)
            .claim("authorities", listOf(directivo.rol.toString()))
            .setIssuedAt(Date(System.currentTimeMillis()))
            //el token de un directivo vence cada un dia.
            .setExpiration(Date(System.currentTimeMillis().plus(1000 * 60 * 60 * 24)))
            .signWith(
                SignatureAlgorithm.HS512,
                Base64.getEncoder().encode(this.secret.toByteArray())
            ).compact()

        return "Bearer $token"
    }

    fun obtainClaims(bearerToken: String): Claims {
        val token = bearerToken.replace("Bearer ", "")

        return Jwts
            .parser()
            .setSigningKey(this.secret)
            .parseClaimsJws(token).body
    }

    fun obtenerDni(bearerToken: String): Int {
        return obtainClaims(bearerToken)["alumno"].toString().toInt()
    }

    fun esAlumno(token: String) = getRole(token) == Role.ROLE_ALUMNO

    fun getRole(bearerToken: String): Role {
        val authority = obtainClaims(bearerToken)["authorities"] as List<String>

        return Role.fromString(authority[0])
    }

    fun esTokenValido(bearerToken: String): Boolean {
        val claims = obtainClaims(bearerToken)
        return bearerToken.startsWith("Bearer ") &&
                claims["authorities"] != null &&
                claims["username"] != null
    }
}
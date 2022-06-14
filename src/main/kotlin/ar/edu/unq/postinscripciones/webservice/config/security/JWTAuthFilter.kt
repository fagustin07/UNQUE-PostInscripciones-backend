package ar.edu.unq.postinscripciones.webservice.config.security

import ar.edu.unq.postinscripciones.model.Alumno
import ar.edu.unq.postinscripciones.model.Directivo
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.persistence.AlumnoRepository
import ar.edu.unq.postinscripciones.persistence.DirectivoRepository
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import org.springframework.security.core.userdetails.User as SpringUserDetails

@Component
class JWTAuthFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var jwtUtil: JWTTokenUtil

    @Autowired
    private lateinit var jwtUserDetailsService: JwtUserDetailsService

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        try {
            validateToken(request)
            chain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
            return
        } catch (e: UnsupportedJwtException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
            return
        } catch (e: MalformedJwtException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
            return
        }
    }

    private fun validateToken(request: HttpServletRequest) {
        var bearerToken: String? = null
        if (existJWTToken(request)) bearerToken = request.getHeader("authorization")

        if (bearerToken != null && jwtUtil.esTokenValido(bearerToken)) {
            setUpSpringAuthentication(bearerToken)
        } else {
            SecurityContextHolder.clearContext()
        }
    }

    private fun existJWTToken(request: HttpServletRequest): Boolean {
        val authenticationHeader = request.getHeader("Authorization")
        return authenticationHeader != null && authenticationHeader.startsWith("Bearer ")
    }

    private fun setUpSpringAuthentication(bearerToken: String) {
        val userDetails: UserDetails = if (jwtUtil.esDirectivo(bearerToken)) {
            jwtUserDetailsService.cargarDirectivo(jwtUtil.obtenerCorreo(bearerToken))
        } else {
            jwtUserDetailsService.cargarAlumno(jwtUtil.obtenerDni(bearerToken))
        }
        val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = auth
    }
}

@Service
class JwtUserDetailsService {

    @Autowired
    private lateinit var alumnoRepository: AlumnoRepository

    @Autowired
    private lateinit var directivoRepository: DirectivoRepository

    @Transactional
    fun cargarAlumno(dni: Int): UserDetails {
        val alumno: Alumno = alumnoRepository.findById(dni).orElseThrow { ExcepcionUNQUE("Alumno no encontrado") }

        return SpringUserDetails(
            alumno.dni.toString(), alumno.contrasenia,
            AuthorityUtils.commaSeparatedStringToAuthorityList(alumno.rol.toString())
        )
    }

    @Transactional
    fun cargarDirectivo(correo: String): UserDetails {
        val directivo: Directivo =
            directivoRepository.findById(correo).orElseThrow { ExcepcionUNQUE("Directivo no encontrado") }

        return SpringUserDetails(
            directivo.correo, directivo.contrasenia,
            AuthorityUtils.commaSeparatedStringToAuthorityList(directivo.rol.toString())
        )
    }
}
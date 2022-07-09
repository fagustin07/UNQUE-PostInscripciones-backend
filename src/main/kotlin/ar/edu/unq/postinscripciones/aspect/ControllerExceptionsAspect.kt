package ar.edu.unq.postinscripciones.aspect

import ar.edu.unq.postinscripciones.model.exception.ConflictoConExistente
import ar.edu.unq.postinscripciones.model.exception.ErrorDeNegocio
import ar.edu.unq.postinscripciones.model.exception.RecursoNoEncontrado
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Aspect
@Component
@Order(0)
class ControllerExceptionsAspect  {
    @Around("execution(* ar.edu.unq.postinscripciones.webservice.controller.*.*(..))")
    fun manageReturn(proceedingJoinPoint: ProceedingJoinPoint): Any {
        return try {
            proceedingJoinPoint.proceed()
        } catch (exception: ErrorDeNegocio) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.toMap())
        } catch (exception: ConflictoConExistente) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(exception.toMap())
        } catch (exception: RecursoNoEncontrado) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.toMap())
        } catch (exception: RuntimeException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.message)
        }
    }
}
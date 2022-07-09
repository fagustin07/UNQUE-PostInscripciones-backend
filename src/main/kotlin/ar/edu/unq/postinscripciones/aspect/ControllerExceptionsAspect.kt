package ar.edu.unq.postinscripciones.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.core.annotation.Order
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
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(createExceptionResponse(e))
        }
    }

    fun createExceptionResponse(e: Exception): MutableMap<String, String> {
        val exceptionResponse = mutableMapOf<String, String>()
        exceptionResponse["error"] = e::class.simpleName.toString()
        exceptionResponse["message"] = e.message.toString()

        return exceptionResponse
    }
}
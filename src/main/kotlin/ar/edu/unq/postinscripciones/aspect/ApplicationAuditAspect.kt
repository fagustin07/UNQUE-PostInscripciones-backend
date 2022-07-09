package ar.edu.unq.postinscripciones.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory
import org.springframework.util.StopWatch


@Aspect
@Component
@Order(1)
class ApplicationAuditAspect {
    private val logger = LoggerFactory.getLogger(ApplicationAuditAspect::class.java)

    @Around("execution(* ar.edu.unq.postinscripciones.webservice.controller.DirectivoController.*(..))")
    fun audit(proceedingJoinPoint: ProceedingJoinPoint): Any {
        val methodSignature: MethodSignature = proceedingJoinPoint.signature as MethodSignature

        //Get intercepted method details
        val className: String = methodSignature.declaringType.simpleName
        val methodName: String = methodSignature.name
        val parameters: String =
                methodSignature.parameterNames
                        .map { it.toString() }
                        .toString()
                        .replace("[", "(")
                        .replace("]", ")")
        val arguments: String =
                proceedingJoinPoint
                        .args
                        .toList()
                        .toString()
                        .replace("[", ">> ")
                        .replace("]", " <<")
                        .replace("null,","")

        val stopWatch = StopWatch()
        //Measure method execution time
        stopWatch.start()
        val result = proceedingJoinPoint.proceed()
        stopWatch.stop()

        //Log method execution time
        logger.info("CLASSNAME: $className ;; METHOD: $methodName$parameters ;; ARGUMENTS: $arguments ;; EXECUTION TIME: ${stopWatch.totalTimeMillis} ms.")

        return result
    }

}
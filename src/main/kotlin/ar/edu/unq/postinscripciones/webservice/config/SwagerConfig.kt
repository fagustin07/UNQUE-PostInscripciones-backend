package ar.edu.unq.postinscripciones.webservice.config

import ar.edu.unq.postinscripciones.webservice.controller.ServiceREST
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwagerConfig {

    @Bean
    fun todos(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .securityContexts(listOf(securityContext()))
            .securitySchemes(listOf(bearerToken()))
            .groupName("todos")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(ServiceREST::class.java))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(metaInfo())
    }

    @Bean
    fun alumno(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .securityContexts(listOf(securityContext()))
                .securitySchemes(listOf(bearerToken()))
                .groupName("usuario alumno")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(ServiceREST::class.java))
                .paths(PathSelectors.ant("/api/alumno/**"))
                .build()
                .apiInfo(metaInfo())
    }

    @Bean
    fun materias(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .securityContexts(listOf(securityContext()))
                .securitySchemes(listOf(bearerToken()))
                .groupName("materias")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(ServiceREST::class.java))
                .paths(PathSelectors.ant("/api/materias/**"))
                .build()
                .apiInfo(metaInfo())
    }

    @Bean
    fun cuatrimestres(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .securityContexts(listOf(securityContext()))
                .securitySchemes(listOf(bearerToken()))
                .groupName("cuatrimestres")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(ServiceREST::class.java))
                .paths(PathSelectors.ant("/api/cuatrimestres/**"))
                .build()
                .apiInfo(metaInfo())
    }

    @Bean
    fun comisiones(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .securityContexts(listOf(securityContext()))
                .securitySchemes(listOf(bearerToken()))
                .groupName("comisiones")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(ServiceREST::class.java))
                .paths(PathSelectors.ant("/api/comisiones/**"))
                .build()
                .apiInfo(metaInfo())
    }
    @Bean
    fun alumnos(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .securityContexts(listOf(securityContext()))
                .securitySchemes(listOf(bearerToken()))
                .groupName("alumnos")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(ServiceREST::class.java))
                .paths(PathSelectors.ant("/api/alumnos/**"))
                .build()
                .apiInfo(metaInfo())
    }

    @Bean
    fun auth(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .securityContexts(listOf(securityContext()))
                .securitySchemes(listOf(bearerToken()))
                .groupName("auth")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(ServiceREST::class.java))
                .paths(PathSelectors.ant("/api/auth/**"))
                .build()
                .apiInfo(metaInfo())
    }

    @Bean
    fun formulario(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .securityContexts(listOf(securityContext()))
                .securitySchemes(listOf(bearerToken()))
                .groupName("formulario")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(ServiceREST::class.java))
                .paths(PathSelectors.ant("/api/formulario/**"))
                .build()
                .apiInfo(metaInfo())
    }

    private fun metaInfo(): ApiInfo {
        return ApiInfo(
            "Post Inscripciones API REST",
            "Proyecto creado para gestionar los formularios de solicitud de sobrecupos",
            "1.0",
            "https://www.youtube.com/watch?v=a1i3KkGVF8c",
            Contact(
                "Algolosos",
                "https://github.com/fagustin07/UNQUE-PostInscripciones-backend/blob/dev/docker-compose.yaml",
                "martinez.nicolas0510@gmail.com"
            ),
            "Apache License Version 2.0",
            "https://www.apache.org/licenses/LICENSE-2.0", ArrayList()
        )
    }

    private fun securityContext(): SecurityContext {
        return SecurityContext.builder().securityReferences(defaultAuth()).build()
    }

    private fun bearerToken(): ApiKey {
        return ApiKey("JWT", "Authorization", "header")
    }

    private fun defaultAuth(): List<SecurityReference?> {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        val authorizationScopes: Array<AuthorizationScope?> = arrayOfNulls(1)
        authorizationScopes[0] = authorizationScope
        return listOf(
            SecurityReference("JWT", authorizationScopes)
        )
    }
}
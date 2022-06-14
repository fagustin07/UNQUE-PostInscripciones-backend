package ar.edu.unq.postinscripciones.webservice.config

import ar.edu.unq.postinscripciones.webservice.controller.ServiceREST
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwagerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .securityContexts(listOf(securityContext()))
            .securitySchemes(listOf(bearerToken()))
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(ServiceREST::class.java))
            .paths(PathSelectors.any())
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
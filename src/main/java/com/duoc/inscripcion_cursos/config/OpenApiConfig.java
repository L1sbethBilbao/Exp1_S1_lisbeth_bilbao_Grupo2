package com.duoc.inscripcion_cursos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BASIC_AUTH = "basicAuth";

    @Bean
    public OpenAPI inscripcionCursosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Inscripcion de Cursos")
                        .description("Microservicio Spring Boot para plataforma educativa virtual - Semana 1 Cloud Native. "
                                + "Roles: ADMIN (crear cursos, reset), ESTUDIANTE (inscripciones). "
                                + "GET /api/cursos es publico.")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH))
                .schemaRequirement(BASIC_AUTH, new SecurityScheme()
                        .name(BASIC_AUTH)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic"));
    }
}

package com.logant.BookingSystem.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        // Bearer authentication
        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

        // For all operations
        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList("BearerAuth");

        return new OpenAPI()
            .info(new Info().title("Your API").version("v1.0"))
            .addSecurityItem(securityRequirement)
            .schemaRequirement("BearerAuth", securityScheme);
    }
}


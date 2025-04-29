package com.innov4africa.api_gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour OpenAPI/Swagger
 */
@Configuration
public class OpenAPIConfig {

    @Value("${openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server()
            .url(devUrl)
            .description("Serveur de développement");

        Contact contact = new Contact()
            .name("Innov4Africa")
            .email("contact@innov4africa.sn")
            .url("https://innov4africa.sn");

        License license = new License()
            .name("Propriétaire")
            .url("https://innov4africa.sn/terms");

        Info info = new Info()
            .title("API Gateway Innov4Africa")
            .version("1.0")
            .contact(contact)
            .description("API Gateway pour l'intégration des services financiers iPay et iBanking. " +
                        "Cette API permet de gérer l'authentification unifiée et la synchronisation " +
                        "des comptes entre les différents services.")
            .license(license);

        // Configuration du schéma de sécurité JWT
        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

        return new OpenAPI()
            .info(info)
            .addServersItem(devServer)
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", securityScheme)
                .addSchemas("ErrorResponse", new Schema<>()
                    .type("object")
                    .addProperties("status", new Schema<>().type("string"))
                    .addProperties("message", new Schema<>().type("string"))
                    .addProperties("timestamp", new Schema<>().type("string").format("date-time"))
                )
            );
    }
}
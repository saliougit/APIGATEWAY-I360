package com.innov4africa.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * Configuration pour OpenAPI/Swagger
 */
@Configuration
public class OpenAPIConfig {

    @Value("${openapi.dev-url}")
    private String devUrl;    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server()
            .url(devUrl)
            .description("Serveur de développement");
        
        Server prodServer = new Server()
            .url("https://api.innov4africa.sn")
            .description("Serveur de production");

        // Exemple de schémas de réponses communes
        Schema<?> errorSchema = new Schema<>()
            .type("object")
            .addProperties("status", new Schema<>().type("string").example("error"))
            .addProperties("message", new Schema<>().type("string"))
            .addProperties("code", new Schema<>().type("integer"))
            .addProperties("data", new Schema<>().type("null"));

        // Configuration générique de réponse API
        Schema<?> apiResponseSchema = new Schema<>()
            .type("object")
            .addProperties("status", new Schema<>().type("string").example("success"))
            .addProperties("message", new Schema<>().type("string"))
            .addProperties("code", new Schema<>().type("integer").example(200))
            .addProperties("data", new Schema<>().type("object"));

        Components components = new Components()
            .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization"))
            .addSchemas("ErrorResponse", errorSchema)
            .addSchemas("ApiResponse", apiResponseSchema);

        // Ajout des tags pour regrouper les endpoints        // Tags pour la documentation
        Tag authTag = new Tag().name("Authentication").description("Endpoints d'authentification globale");
        Tag shopTag = new Tag().name("IShop").description("Services de la marketplace iShop");
        Tag bankingTag = new Tag().name("IBanking").description("Services bancaires ibanking");
        Tag payTag = new Tag().name("IPay").description("Services de paiement iPay");
        Tag waveTag = new Tag().name("Wave").description("Intégration des services Wave");
        Tag orangeTag = new Tag().name("Orange Money").description("Intégration Orange Money");
        Tag productTag = new Tag().name("Products").description("Gestion des produits iShop");
        Tag orderTag = new Tag().name("Orders").description("Gestion des commandes iShop");
        Tag userTag = new Tag().name("Users").description("Gestion des utilisateurs");

        return new OpenAPI()
            .info(new Info()
                .title("API Gateway iShop")
                .version("1.0")
                .description("API Gateway pour les services iShop. Cette documentation permet l'intégration rapide avec les clients mobiles.")
                .contact(new Contact()
                    .name("Innov4Africa")
                    .email("contact@innov4africa.sn")
                    .url("https://innov4africa.sn"))
                .license(new License()
                    .name("Propriétaire")
                    .url("https://innov4africa.sn/terms")))
            .addServersItem(devServer)
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(components)            .addTagsItem(authTag)
            .addTagsItem(shopTag)
            .addTagsItem(bankingTag)
            .addTagsItem(payTag)
            .addTagsItem(waveTag)
            .addTagsItem(orangeTag)
            .addTagsItem(productTag)
            .addTagsItem(orderTag)
            .addTagsItem(userTag)
            .addServersItem(prodServer);
    }
}
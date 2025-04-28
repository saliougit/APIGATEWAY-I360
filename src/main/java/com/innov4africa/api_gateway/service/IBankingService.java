package com.innov4africa.api_gateway.service;

import com.innov4africa.api_gateway.model.IBankingTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class IBankingService {
    private static final Logger logger = LoggerFactory.getLogger(IBankingService.class);
    private final WebClient webClient;
    
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.resource}")
    private String clientId;

    public IBankingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<Boolean> verifyUserExists(String email, String telephone) {
        logger.info("Vérification de l'existence de l'utilisateur - email: {}, telephone: {}", email, telephone);
        
        // Obtenir d'abord un token admin
        return getAdminToken()
            .flatMap(token -> {
                String searchUrl = String.format("%s/admin/realms/%s/users?email=%s", authServerUrl, realm, email);
                
                return webClient.get()
                    .uri(searchUrl)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        // Si la réponse n'est pas vide, l'utilisateur existe
                        return !response.equals("[]");
                    })
                    .onErrorResume(e -> {
                        logger.error("Erreur lors de la vérification de l'utilisateur", e);
                        return Mono.just(false);
                    });
            });
    }

    private Mono<String> getAdminToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("username", "admin");
        formData.add("password", "admin");

        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", authServerUrl, realm);

        return webClient.post()
            .uri(tokenUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(IBankingTokenResponse.class)
            .map(IBankingTokenResponse::getAccessToken);
    }
}
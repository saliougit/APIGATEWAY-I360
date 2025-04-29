package com.innov4africa.api_gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.innov4africa.api_gateway.model.IBankingTokenResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;

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

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    public IBankingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    private Mono<String> getAdminToken() {
        String tokenUrl = String.format("%s/realms/master/protocol/openid-connect/token", authServerUrl);
        
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", "admin-cli");
        formData.add("username", adminUsername);
        formData.add("password", adminPassword);
        
        logger.info("Obtention du token admin - URL: {}", tokenUrl);
        
        return webClient.post()
            .uri(tokenUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(IBankingTokenResponse.class)
            .map(response -> {
                logger.info("Token admin obtenu avec succès");
                return response.getAccessToken();
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de l'obtention du token admin: {}", e.getMessage());
                return Mono.error(e);
            });
    }

    public Mono<Boolean> verifyUserExists(String email, String telephone) {
        logger.info("Vérification de l'existence de l'utilisateur - email: {}, telephone: {}", email, telephone);
        
        return getAdminToken()
            .flatMap(token -> {
                // Si les deux paramètres sont fournis
                if (email != null && !email.isBlank() && telephone != null && !telephone.isBlank()) {
                    // Faire d'abord une recherche par email
                    return searchUser(token, String.format("?email=%s", email))
                        .flatMap(foundByEmail -> {
                            if (foundByEmail) {
                                logger.info("Utilisateur trouvé par email");
                                return Mono.just(true);
                            }
                            // Si pas trouvé par email, chercher par téléphone
                            logger.info("Utilisateur non trouvé par email, recherche par téléphone");
                            return searchUser(token, String.format("?q=telephone:%s", telephone));
                        });
                }
                // Si seulement email
                else if (email != null && !email.isBlank()) {
                    return searchUser(token, String.format("?email=%s", email));
                }
                // Si seulement téléphone
                else if (telephone != null && !telephone.isBlank()) {
                    return searchUser(token, String.format("?q=telephone:%s", telephone));
                }
                // Si aucun paramètre
                else {
                    return Mono.just(false);
                }
            });
    }

    private Mono<Boolean> searchUser(String token, String searchParams) {
        String searchUrl = String.format("%s/admin/realms/%s/users%s", authServerUrl, realm, searchParams);
        logger.info("URL de recherche: {}", searchUrl);
        
        return webClient.get()
            .uri(searchUrl)
            .header("Authorization", "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> {
                logger.info("Réponse reçue: {}", response);
                return !response.equals("[]");
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de la recherche: {}", e.getMessage());
                return Mono.just(false);
            });
    }

    public Mono<Boolean> createUser(String email, String telephone, String firstName, String lastName, String password) {
        logger.info("Création d'un utilisateur - email: {}, telephone: {}, nom: {}, prénom: {}", 
                   email, telephone, lastName, firstName);
        
        return getAdminToken()
            .flatMap(token -> {
                String createUrl = String.format("%s/admin/realms/%s/users", authServerUrl, realm);
                
                // Construire le corps de la requête pour la création d'utilisateur
                Map<String, Object> userData = new HashMap<>();
                userData.put("username", email);
                userData.put("email", email);
                userData.put("firstName", firstName);
                userData.put("lastName", lastName);
                userData.put("enabled", true);
                userData.put("emailVerified", true);
                
                // Ajouter les attributs personnalisés
                Map<String, List<String>> attributes = new HashMap<>();
                if (telephone != null && !telephone.isBlank()) {
                    attributes.put("phone", Collections.singletonList(telephone));
                }
                userData.put("attributes", attributes);
                
                // Utiliser le même mot de passe que iPay
                Map<String, Object> credential = new HashMap<>();
                credential.put("type", "password");
                credential.put("value", password);
                credential.put("temporary", false);  // Mot de passe non temporaire puisque c'est celui de l'utilisateur
                userData.put("credentials", Collections.singletonList(credential));
                
                logger.info("Création de l'utilisateur - URL: {}", createUrl);
                
                return webClient.post()
                    .uri(createUrl)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(userData)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> {
                        logger.info("Utilisateur créé avec succès");
                        return true;
                    })
                    .onErrorResume(e -> {
                        logger.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage());
                        return Mono.just(false);
                    });
            });
    }
}
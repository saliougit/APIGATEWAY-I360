package com.innov4africa.api_gateway.service;

import com.innov4africa.api_gateway.model.IShopLoginRequest;
import com.innov4africa.api_gateway.model.IShopLoginResponse;
import com.innov4africa.api_gateway.model.IShopErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class IShopService {
    private static final Logger logger = LoggerFactory.getLogger(IShopService.class);
    private final WebClient webClient;

    @Value("${ishop.base-url}")
    private String baseUrl;

    public IShopService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<Object> testLogin(IShopLoginRequest request) {
        String url = baseUrl + "/mobile-ws/user/login";
        logger.info("Tentative d'authentification i-shop pour l'utilisateur: {} vers {}", 
            request.getEmail(), baseUrl);

        return webClient.post()
            .uri(url)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(IShopLoginResponse.class)
            .map(response -> {
                if (!"success".equals(response.getStatus())) {
                    // En cas d'erreur, on retourne un IShopErrorResponse
                    return new IShopErrorResponse(
                        "error",
                        response.getMessage(),
                        "403"
                    );
                }
                // En cas de succès, on retourne la réponse complète
                response.setCode("200");
                return response;
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de l'authentification i-shop", e);
                return Mono.just(new IShopErrorResponse(
                    "error",
                    "Service i-shop temporairement indisponible",
                    "500"
                ));
            });
    }
}
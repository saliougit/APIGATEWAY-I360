package com.innov4africa.api_gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Duration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.innov4africa.api_gateway.model.IShopLoginRequest;
import com.innov4africa.api_gateway.model.IShopLoginResponse;

import io.netty.handler.timeout.TimeoutException;
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

    public Mono<IShopLoginResponse> login(IShopLoginRequest request) {
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
                    IShopLoginResponse errorResponse = new IShopLoginResponse();
                    errorResponse.setStatus("error");
                    errorResponse.setMessage(response.getMessage());
                    errorResponse.setCode("403");
                    return errorResponse;
                }
                return response;
            })
            .doOnNext(response -> {
                logger.debug("Réponse i-shop reçue pour l'utilisateur {}: user_id={}, domaines={}", 
                    request.getEmail(), response.getUser_id(), 
                    response.getDomaineList() != null ? response.getDomaineList().size() : 0);
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de l'authentification i-shop", e);
                IShopLoginResponse errorResponse = new IShopLoginResponse();
                errorResponse.setStatus("error");
                errorResponse.setMessage("Service i-shop temporairement indisponible");
                errorResponse.setCode("500");
                return Mono.just(errorResponse);
            });
    }

    private IShopLoginResponse createErrorResponse(String message) {
        IShopLoginResponse response = new IShopLoginResponse();
        response.setStatus("error");
        response.setMessage(message);
        response.setCode("500");
        return response;
    }
}


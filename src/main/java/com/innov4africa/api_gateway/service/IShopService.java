package com.innov4africa.api_gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.innov4africa.api_gateway.model.IShopAddressRequest;
import com.innov4africa.api_gateway.model.IShopAddressResponse;
import com.innov4africa.api_gateway.model.IShopLoginRequest;
import com.innov4africa.api_gateway.model.IShopLoginResponse;
import com.innov4africa.api_gateway.model.IShopNotificationRequest;
import com.innov4africa.api_gateway.model.IShopNotificationResponse;
import com.innov4africa.api_gateway.model.IShopOrderResponse;

import reactor.core.publisher.Mono;

@Service
public class IShopService {
    private static final Logger logger = LoggerFactory.getLogger(IShopService.class);
    private final WebClient webClient;

    @Value("${ishop.base-url}")
    private String baseUrl;    public IShopService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://ibusinesscompanies.com:8443")
            .build();
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

    public Mono<IShopAddressResponse> listAddresseSeller(IShopAddressRequest request) {
        String url = baseUrl + "/mobile-ws/product/list_addresse_seller";
        logger.info("Appel distant iShop pour la liste des adresses vendeur: {}", request.getUser_id());
        return webClient.post()
            .uri(url)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(IShopAddressResponse.class)
            .doOnNext(response -> logger.debug("Réponse iShop adresses: {}", response))
            .onErrorResume(e -> {
                logger.error("Erreur lors de la récupération des adresses vendeur iShop", e);
                return Mono.error(new RuntimeException("Erreur lors de la récupération des adresses vendeur iShop"));
            });
    }

    public Mono<IShopNotificationResponse> listNotifications(IShopNotificationRequest request) {
        String url = baseUrl + "/mobile-ws/product/notification_list";
        logger.info("Appel distant iShop pour la liste des notifications: {}", request.getUser_id());
        
        return webClient.post()
            .uri(url)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(IShopNotificationResponse.class)
            .doOnNext(response -> {
                logger.debug("Réponse iShop notifications reçue");
                if ("error".equals(response.getStatus())) {
                    logger.error("Erreur dans la réponse iShop: {}", response.getMessage());
                }
            })
            .onErrorMap(WebClientResponseException.class, e -> {
                logger.error("Erreur HTTP {} - Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
                return new RuntimeException("Erreur lors de l'appel au service iShop: " + e.getMessage());
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de la récupération des notifications", e);
                IShopNotificationResponse errorResponse = new IShopNotificationResponse();
                errorResponse.setStatus("error");
                errorResponse.setMessage("Erreur technique: " + e.getMessage());
                return Mono.just(errorResponse);
            });
    }    public Mono<IShopOrderResponse> listSellerOrders(Integer userId, String type) {
        logger.info("Appel distant iShop pour la liste des commandes vendeur: user_id={}, type={}", 
            userId, type);
        
        return webClient.get()
            .uri(baseUrl + "/mobile-ws/product/myorders_seller?user_id={userId}&type={type}", 
                 userId, type)
            .retrieve()
            .bodyToMono(IShopOrderResponse.class)
            .doOnNext(response -> logger.debug("Réponse iShop commandes: {}", response))
            .onErrorResume(e -> {
                logger.error("Erreur lors de la récupération des commandes vendeur iShop", e);
                return Mono.error(new RuntimeException("Erreur lors de la récupération des commandes vendeur iShop")); 
            });
    }
}


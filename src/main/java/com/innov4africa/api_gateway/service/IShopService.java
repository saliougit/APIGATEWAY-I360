package com.innov4africa.api_gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.innov4africa.api_gateway.model.IShopOrder;
import com.innov4africa.api_gateway.model.IShopProductRequest;
import com.innov4africa.api_gateway.model.IShopProductResponse;
import com.innov4africa.api_gateway.model.IShopProduct;
import com.innov4africa.api_gateway.model.PaginationMetadata;
import java.util.List;
import java.util.stream.Collectors;
import reactor.core.publisher.Mono;

@Service
public class IShopService {
    private static final Logger logger = LoggerFactory.getLogger(IShopService.class);
    private final WebClient webClient;

    // Constantes pour la standardisation des valeurs
    private static final String DEFAULT_CURRENCY = "XOF";
    private static final double MIN_RATING = 0.0;
    private static final double MAX_RATING = 5.0;    private static final double DEFAULT_RATING = 0.0;
    private static final int DEFAULT_LIMIT = 20;
    private static final String DEFAULT_LANGUAGE = "fr";

    @Value("${ishop.base-url}")
    private String baseUrl;        @Autowired
    private IShopProductCacheService productCache;
    
    @Autowired
    private InMemoryProductCache tempCache;
    
    public IShopService(WebClient.Builder webClientBuilder) {
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
    }    
    public Mono<IShopOrderResponse> listSellerOrders(Integer userId, String type) {
        logger.info("Appel distant iShop pour la liste des commandes vendeur: user_id={}, type={}", 
            userId, type);
        return webClient.get()
            .uri(baseUrl + "/mobile-ws/product/myorders_seller?user_id={userId}&type={type}", 
                 userId, type)
            .retrieve()
            .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<IShopOrder>>() {})
            .map(orders -> {
                IShopOrderResponse response = new IShopOrderResponse();
                response.setStatus("success");
                response.setMessage("Commandes récupérées avec succès");
                response.setData(orders);
                response.setCode(200);
                return response;
            })
            .doOnNext(response -> logger.debug("Réponse iShop commandes: {}", response))
            .onErrorResume(e -> {
                logger.error("Erreur lors de la récupération des commandes vendeur iShop", e);
                IShopOrderResponse errorResponse = new IShopOrderResponse();
                errorResponse.setStatus("error");
                errorResponse.setMessage("Erreur lors de la récupération des commandes : " + e.getMessage());
                errorResponse.setCode(500);
                return Mono.just(errorResponse);
            });
    }    public Mono<IShopProductResponse> listProducts(IShopProductRequest request) {
        logger.info("Récupération des produits pour user_id={}, next_offset={}", 
            request.getUser_id(), request.getNext_offset());
          
        // Essayer d'abord Redis
        List<IShopProduct> products = null;
        try {
            products = productCache.getCachedProducts(request.getUser_id(), request.getLanguage());
        } catch (Exception e) {
            logger.warn("Redis indisponible: {}", e.getMessage());
            // Si Redis est down, essayer le cache temporaire
            products = tempCache.get(String.valueOf(request.getUser_id()), request.getLanguage());
        }
          if (products != null) {
            logger.debug("Produits trouvés dans le cache, total items: {}", products.size());
            IShopProductResponse response = new IShopProductResponse();
            response.setStatus("success");
            response.setCode(200);
            
            // Calculer la sous-liste pour la page demandée
            int start = request.getNext_offset();
            int end = Math.min(start + DEFAULT_LIMIT, products.size());
            response.setResult(products.subList(start, end));
            
            // Configurer la pagination avec le total exact du cache
            Integer nextPage = end < products.size() ? end : null;
            response.setPagination(new PaginationMetadata(
                request.getNext_offset(),
                DEFAULT_LIMIT,
                products.size(),
                nextPage
            ));
            return Mono.just(response);
        }

        // Si pas dans le cache, appeler le serveur
        logger.info("Cache miss - Appel distant iShop");
        return webClient.post()
            .uri(baseUrl + "/mobile-ws/product/list")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(IShopProductResponse.class)
            .map(response -> {
                // Normaliser les données
                if (response.getResult() != null) {
                    List<IShopProduct> normalizedProducts = response.getResult().stream()
                        .map(this::normalizeProduct)
                        .collect(Collectors.toList());
                      // Essayer de mettre en cache Redis
                    try {
                        productCache.cacheProducts(request.getUser_id(), request.getLanguage(), normalizedProducts);
                    } catch (Exception e) {
                        logger.warn("Impossible de mettre en cache Redis: {}", e.getMessage());
                        // Utiliser le cache temporaire comme fallback
                        tempCache.store(String.valueOf(request.getUser_id()), request.getLanguage(), normalizedProducts);
                    }
                    
                    // Créer la réponse paginée
                    return createPaginatedResponse(normalizedProducts, request.getNext_offset());
                }
                return response;
            })
            .doOnNext(response -> logger.debug("Réponse iShop produits: total={}", 
                response.getPagination().getTotalItems()))
            .onErrorResume(e -> {
                logger.error("Erreur lors de la récupération des produits", e);
                IShopProductResponse errorResponse = new IShopProductResponse();
                errorResponse.setStatus("error");
                errorResponse.setMessage("Erreur lors de la récupération des produits : " + e.getMessage());
                errorResponse.setCode(500);
                return Mono.just(errorResponse);
            });
    }

    private IShopProduct normalizeProduct(IShopProduct product) {
        // Correction du rating si égal au product_id
        if (product.getAvg_rating() != null && 
            Math.abs(product.getAvg_rating() - product.getProduct_id()) < 0.0001) {
            product.setAvg_rating(DEFAULT_RATING);
        }
        
        // S'assurer que le rating est entre MIN_RATING et MAX_RATING
        if (product.getAvg_rating() != null) {
            product.setAvg_rating(
                Math.min(Math.max(product.getAvg_rating(), MIN_RATING), MAX_RATING)
            );
        }

        // Définir la devise par défaut si null
        if (product.getCurrency() == null) {
            product.setCurrency(DEFAULT_CURRENCY);
        }

        return product;
    }    private IShopProductResponse createPaginatedResponse(List<IShopProduct> products, Integer nextOffset) {
        int start = nextOffset;
        int end = Math.min(start + DEFAULT_LIMIT, products.size());
        
        IShopProductResponse response = new IShopProductResponse();
        response.setStatus("success");
        response.setCode(200);
        response.setResult(products.subList(start, end));
        
        // Pour les appels API, on utilise la taille actuelle comme total
        // car ces produits seront mis en cache ensuite
        Integer nextPage = end < products.size() ? end : null;
        response.setPagination(new PaginationMetadata(
            nextOffset,
            DEFAULT_LIMIT,
            products.size(), // total exact de cette réponse API
            nextPage
        ));
        
        return response;
    }
}


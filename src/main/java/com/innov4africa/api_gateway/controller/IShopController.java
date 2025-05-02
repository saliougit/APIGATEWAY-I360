package com.innov4africa.api_gateway.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.bind.annotation.GetMapping;

import com.innov4africa.api_gateway.model.IShopAddressRequest;
import com.innov4africa.api_gateway.model.IShopAddressResponse;
import com.innov4africa.api_gateway.model.IShopLoginRequest;
import com.innov4africa.api_gateway.model.IShopLoginResponse;
import com.innov4africa.api_gateway.model.IShopNotificationResponse;
import com.innov4africa.api_gateway.model.IShopNotificationRequest;
import com.innov4africa.api_gateway.service.IShopService;
import com.innov4africa.api_gateway.service.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ishop")
@Tag(name = "IShop", description = "API pour la gestion des services i-shop")
public class IShopController {
    
    private static final Logger logger = LoggerFactory.getLogger(IShopController.class);
    
    @Autowired
    private IShopService iShopService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Authentification i-shop",
              description = "Authentifie un utilisateur auprès du service i-shop")
    @PostMapping("/login")
    public Mono<ResponseEntity<IShopLoginResponse>> login(@RequestBody IShopLoginRequest request) {
        logger.info("Login i-shop reçu pour: {}", request.getEmail());
        
        // Validation des champs obligatoires
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            IShopLoginResponse errorResponse = new IShopLoginResponse();
            errorResponse.setStatus("error");
            errorResponse.setMessage("Email et mot de passe requis");
            errorResponse.setCode("400");
            return Mono.just(ResponseEntity.badRequest().body(errorResponse));
        }
        
        return iShopService.login(request)
            .map(response -> {
                if ("error".equals(response.getStatus())) {
                    // Si c'est une erreur, on renvoie un 403
                    return ResponseEntity.status(403).body(response);
                } else {
                    // Si c'est un succès, on renvoie un 200
                    return ResponseEntity.ok(response);
                }
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors du login i-shop", e);
                IShopLoginResponse errorResponse = new IShopLoginResponse();
                errorResponse.setStatus("error");
                errorResponse.setMessage("Service i-shop temporairement indisponible");
                errorResponse.setCode("500");
                return Mono.just(ResponseEntity.status(500).body(errorResponse));
            });
    }

    @Operation(summary = "Liste des adresses vendeur i-shop", description = "Récupère la liste des adresses d'un vendeur i-shop à partir du token JWT")
    @PostMapping("/list_addresse_seller")
    public Mono<ResponseEntity<IShopAddressResponse>> listAddresseSeller(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) Map<String, Object> body) {
        // Vérification du token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            IShopAddressResponse error = new IShopAddressResponse();
            error.setStatus("error");
            error.setMessage("Token d'authentification manquant ou invalide");
            error.setCode(401);
            return Mono.just(ResponseEntity.status(401).body(error));
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            IShopAddressResponse error = new IShopAddressResponse();
            error.setStatus("error");
            error.setMessage("Token d'authentification manquant ou invalide");
            error.setCode(401);
            return Mono.just(ResponseEntity.status(401).body(error));
        }
        // Récupérer le user_id iShop depuis le token
        Integer ishopUserId = null;
        try {
            var ishopInfo = jwtUtil.extractIShopInfo(token);
            // if (ishopInfo == null || ishopInfo.getUser_id() == null) {
            //     IShopAddressResponse error = new IShopAddressResponse();
            //     error.setStatus("error");
            //     error.setMessage("Accès refusé : utilisateur iShop introuvable dans le token.");
            //     error.setCode(403);
            //     return Mono.just(ResponseEntity.status(403).body(error));
            // }
            // ishopUserId = ishopInfo.getUser_id();
            ishopUserId = 725; // Valeur de test pour le développement
        } catch (Exception e) {
            IShopAddressResponse error = new IShopAddressResponse();
            error.setStatus("error");
            error.setMessage("Erreur interne lors de l'extraction des informations utilisateur.");
            error.setCode(500);
            return Mono.just(ResponseEntity.status(500).body(error));
        }
        // Récupérer le language du body ou mettre "fr" par défaut
        String language = "fr";
        if (body != null && body.get("language") != null) {
            language = String.valueOf(body.get("language"));
        }
        IShopAddressRequest req = new IShopAddressRequest(ishopUserId, language);
        return iShopService.listAddresseSeller(req)
            .map(response -> ResponseEntity.ok(response))
            .onErrorResume(e -> {
                IShopAddressResponse error = new IShopAddressResponse();
                error.setStatus("error");
                error.setMessage("Erreur lors de la récupération des adresses vendeur iShop : " + e.getMessage());
                error.setCode(500);
                return Mono.just(ResponseEntity.status(500).body(error));
            });
    }

    // @Operation(summary = "Liste des notifications i-shop", description = "Récupère la liste des notifications d'un utilisateur i-shop à partir du token JWT")
    // @PostMapping("/notifications")
    // public Mono<ResponseEntity<IShopNotificationResponse>> listNotifications(
    //         @RequestHeader(value = "Authorization", required = false) String authHeader,
    //         @RequestBody(required = false) Map<String, Object> body) {
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         IShopNotificationResponse error = new IShopNotificationResponse();
    //         error.setStatus("error");
    //         return Mono.just(ResponseEntity.status(401).body(error));
    //     }
    //     String token = authHeader.substring(7);
    //     if (!jwtUtil.validateToken(token)) {
    //         IShopNotificationResponse error = new IShopNotificationResponse();
    //         error.setStatus("error");
    //         return Mono.just(ResponseEntity.status(401).body(error));
    //     }
    //     Integer ishopUserId = null;
    //     try {
    //         var ishopInfo = jwtUtil.extractIShopInfo(token);
    //         // if (ishopInfo == null || ishopInfo.getUser_id() == null) {
    //         //     IShopNotificationResponse error = new IShopNotificationResponse();
    //         //     error.setStatus("error");
    //         //     error.setMessage("Accès refusé : utilisateur iShop introuvable dans le token.");
    //         //     return Mono.just(ResponseEntity.status(403).body(error));
    //         // }
    //         // ishopUserId = ishopInfo.getUser_id();
    //         ishopUserId = 725; // Valeur de test pour le développement
    //     } catch (Exception e) {
    //         IShopNotificationResponse error = new IShopNotificationResponse();
    //         error.setStatus("error");
    //         error.setMessage("Erreur interne lors de l'extraction des informations utilisateur.");
    //         return Mono.just(ResponseEntity.status(500).body(error));
    //     }
    //     String language = "fr";
    //     if (body != null && body.get("language") != null) {
    //         language = String.valueOf(body.get("language"));
    //     }
    //     IShopNotificationRequest req = new IShopNotificationRequest(ishopUserId, language);
    //     return iShopService.listNotifications(req)
    //         .map(response -> ResponseEntity.ok(response))
    //         .onErrorResume(e -> {
    //             IShopNotificationResponse error = new IShopNotificationResponse();
    //             error.setStatus("error");
    //             return Mono.just(ResponseEntity.status(500).body(error));
    //         });
    // }

     @Operation(summary = "Liste des notifications i-shop", 
              description = "Récupère la liste des notifications d'un utilisateur i-shop à partir du token JWT")
    @PostMapping("/notifications")
    public Mono<ResponseEntity<IShopNotificationResponse>> listNotifications(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) Map<String, Object> body) {
        
        // Vérification du token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just(buildErrorResponse(401, "Token d'authentification manquant ou invalide"));
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return Mono.just(buildErrorResponse(401, "Token invalide ou expiré"));
        }

        // Extraction des infos utilisateur
        Integer ishopUserId;
        try {
            var ishopInfo = jwtUtil.extractIShopInfo(token);
           // if (ishopInfo == null || ishopInfo.getUser_id() == null) {
            //     IShopAddressResponse error = new IShopAddressResponse();
            //     error.setStatus("error");
            //     error.setMessage("Accès refusé : utilisateur iShop introuvable dans le token.");
            //     error.setCode(403);
            //     return Mono.just(ResponseEntity.status(403).body(error));
            // }
            // ishopUserId = ishopInfo.getUser_id();
            ishopUserId = 725; // Valeur de test pour le développement
        } catch (Exception e) {
            return Mono.just(buildErrorResponse(500, "Erreur lors de l'extraction des informations utilisateur"));
        }

        // Gestion de la langue
        String language = "fr";
        if (body != null && body.get("language") != null) {
            language = String.valueOf(body.get("language"));
        }

        // Appel du service
        IShopNotificationRequest req = new IShopNotificationRequest(ishopUserId, language);
        return iShopService.listNotifications(req)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                logger.error("Erreur lors de la récupération des notifications", e);
                if (e instanceof WebClientResponseException) {
                    WebClientResponseException wcre = (WebClientResponseException) e;
                    return Mono.just(buildErrorResponse(wcre.getStatusCode().value(), 
                        "Erreur du serveur distant: " + wcre.getResponseBodyAsString()));
                }
                return Mono.just(buildErrorResponse(500, 
                    "Erreur interne du serveur: " + e.getMessage()));
            });
    }

    private ResponseEntity<IShopNotificationResponse> buildErrorResponse(int status, String message) {
        IShopNotificationResponse error = new IShopNotificationResponse();
        error.setStatus("error");
        error.setMessage(message);
        return ResponseEntity.status(status).body(error);
    }
}

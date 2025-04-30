package com.innov4africa.api_gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innov4africa.api_gateway.model.IShopErrorResponse;
import com.innov4africa.api_gateway.model.IShopLoginRequest;
import com.innov4africa.api_gateway.service.IShopService;

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

    @Operation(summary = "Authentification i-shop",
              description = "Authentifie un utilisateur auprès du service i-shop")
    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody IShopLoginRequest request) {
        logger.info("Login i-shop reçu pour: {}", request.getEmail());
        
        // Validation des champs obligatoires
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            return Mono.just(ResponseEntity.badRequest().body(
                new IShopErrorResponse("error", "Email et mot de passe requis", "400")
            ));
        }
        
        return iShopService.login(request)
            .map(response -> {
                if (response instanceof IShopErrorResponse) {
                    // Si c'est une erreur, on renvoie un 403
                    return ResponseEntity.status(403).body(response);
                } else {
                    // Si c'est un succès, on renvoie un 200
                    return ResponseEntity.ok(response);
                }
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors du login i-shop", e);
                return Mono.just(ResponseEntity
                    .status(500)
                    .body(new IShopErrorResponse(
                        "error",
                        "Service i-shop temporairement indisponible",
                        "500"
                    )));
            });
    }
}
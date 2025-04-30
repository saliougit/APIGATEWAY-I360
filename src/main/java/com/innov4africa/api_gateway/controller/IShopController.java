package com.innov4africa.api_gateway.controller;

import com.innov4africa.api_gateway.model.IShopLoginRequest;
import com.innov4africa.api_gateway.model.IShopErrorResponse;
import com.innov4africa.api_gateway.service.IShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/ishop")
@Tag(name = "IShop", description = "API pour la gestion des services i-shop")
public class IShopController {
    
    private static final Logger logger = LoggerFactory.getLogger(IShopController.class);
    
    @Autowired
    private IShopService iShopService;

    @Operation(summary = "Tester l'authentification i-shop",
              description = "Vérifie les identifiants de connexion auprès du service i-shop")
    @PostMapping("/test-login")
    public Mono<ResponseEntity<Object>> testLogin(@RequestBody IShopLoginRequest request) {
        logger.info("Test de login i-shop reçu pour: {}", request.getEmail());
        
        return iShopService.testLogin(request)
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
                logger.error("Erreur lors du test de login i-shop", e);
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
package com.innov4africa.api_gateway.controller;

import com.innov4africa.api_gateway.model.IBankingUserCheckResponse;
import com.innov4africa.api_gateway.service.IBankingService;
import com.innov4africa.api_gateway.service.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ibanking")
public class IBankingController {
    
    private static final Logger logger = LoggerFactory.getLogger(IBankingController.class);
    
    @Autowired
    private IBankingService iBankingService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/user-check")
    public Mono<ResponseEntity<IBankingUserCheckResponse>> checkUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 1. Vérification de la présence du header Authorization
        if (authHeader == null || authHeader.isBlank()) {
            logger.warn("Tentative de vérification sans header Authorization");
            return Mono.just(ResponseEntity.status(401).body(
                new IBankingUserCheckResponse("error", "Token d'authentification manquant", false)
            ));
        }

        // 2. Vérification du format Bearer
        if (!authHeader.startsWith("Bearer ")) {
            logger.warn("Format de token invalide: {}", authHeader);
            return Mono.just(ResponseEntity.status(401).body(
                new IBankingUserCheckResponse("error", "Format de token invalide", false)
            ));
        }

        String jwt = authHeader.substring(7);
        
        // 3. Validation du token JWT
        if (!jwtUtil.validateToken(jwt)) {
            logger.warn("Token JWT invalide ou expiré");
            return Mono.just(ResponseEntity.status(401).body(
                new IBankingUserCheckResponse("error", "Token invalide ou expiré", false)
            ));
        }

        // 4. Extraction des informations nécessaires du JWT
        String email = jwtUtil.extractEmail(jwt);
        String telephone = jwtUtil.extractTelephone(jwt);

        if (email == null && telephone == null) {
            logger.warn("Token ne contient pas d'email ni de téléphone");
            return Mono.just(ResponseEntity.badRequest().body(
                new IBankingUserCheckResponse("error", "Informations d'identification manquantes", false)
            ));
        }

        // 5. Vérification de l'existence de l'utilisateur
        return iBankingService.verifyUserExists(email, telephone)
            .map(exists -> {
                String message = exists ? 
                    "Utilisateur trouvé dans iBanking" : 
                    "Utilisateur non trouvé dans iBanking";
                
                return ResponseEntity.ok(new IBankingUserCheckResponse(
                    exists ? "success" : "not_found",
                    message,
                    exists
                ));
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de la vérification de l'utilisateur", e);
                return Mono.just(ResponseEntity.internalServerError().body(
                    new IBankingUserCheckResponse("error", "Erreur technique", false)
                ));
            });
    }
}
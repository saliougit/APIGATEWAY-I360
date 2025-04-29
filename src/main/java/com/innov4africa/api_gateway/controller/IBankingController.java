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
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telephone) {
        
        logger.info("Vérification utilisateur - email: {}, telephone: {}", email, telephone);

        if ((email == null || email.isBlank()) && (telephone == null || telephone.isBlank())) {
            return Mono.just(ResponseEntity.badRequest().body(
                new IBankingUserCheckResponse("error", "Email ou téléphone requis", false)
            ));
        }

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
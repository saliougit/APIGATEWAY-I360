package com.innov4africa.api_gateway.controller;

import com.innov4africa.api_gateway.model.GlobalBalanceResponse;
import com.innov4africa.api_gateway.model.ServiceStatus;
import com.innov4africa.api_gateway.service.AggregationService;
import com.innov4africa.api_gateway.service.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "API pour les fonctionnalités agrégées")
public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    @Autowired
    private AggregationService aggregationService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
        summary = "Obtenir le solde global",
        description = "Récupère et agrège les soldes des comptes iPay et iBanking de l'utilisateur"
    )
    
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solde global récupéré avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé ou token invalide"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération des soldes")
    })
    @GetMapping("/solde")
    public Mono<ResponseEntity<GlobalBalanceResponse>> getGlobalBalance(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 1. Vérifier la présence du header Authorization
        if (authHeader == null || authHeader.isBlank()) {
            logger.warn("Tentative d'accès sans header Authorization");            return Mono.just(ResponseEntity.status(401).body(
                new GlobalBalanceResponse(
                    "error",
                    "Token d'authentification manquant",
                    "0.00", // totalMontant
                    "0.00", // montantIPay
                    "0.00", // montantIBanking
                    List.of(new ServiceStatus("i-pay", false, "Non autorisé"),
                           new ServiceStatus("i-banking", false, "Non autorisé"))
                )
            ));
        }

        // 2. Vérifier le format Bearer
        if (!authHeader.startsWith("Bearer ")) {
            logger.warn("Format de token invalide: {}", authHeader);            return Mono.just(ResponseEntity.status(401).body(
                new GlobalBalanceResponse(
                    "error",
                    "Format de token invalide",
                    "0.00", // totalMontant
                    "0.00", // montantIPay
                    "0.00", // montantIBanking
                    List.of(new ServiceStatus("i-pay", false, "Non autorisé"),
                           new ServiceStatus("i-banking", false, "Non autorisé"))
                )
            ));
        }

        String jwt = authHeader.substring(7);
        
        // 3. Valider le token JWT
        if (!jwtUtil.validateToken(jwt)) {
            logger.warn("Token JWT invalide ou expiré");            return Mono.just(ResponseEntity.status(401).body(
                new GlobalBalanceResponse(
                    "error",
                    "Token invalide ou expiré",
                    "0.00", // totalMontant
                    "0.00", // montantIPay
                    "0.00", // montantIBanking
                    List.of(new ServiceStatus("i-pay", false, "Non autorisé"),
                           new ServiceStatus("i-banking", false, "Non autorisé"))
                )
            ));
        }        // 4. Extraire les claims nécessaires
        String telephone = jwtUtil.extractTelephone(jwt);
        String email = jwtUtil.extractEmail(jwt);
        String ipayToken = jwtUtil.extractIpayToken(jwt);

        if (telephone == null || ipayToken == null || email == null) {
            logger.warn("Token ne contient pas les claims requis - telephone: {}, email: {}, ipayToken: {}", 
                      telephone, email, ipayToken);            return Mono.just(ResponseEntity.status(401).body(
                new GlobalBalanceResponse(
                    "error",
                    "Token incomplet",
                    "0.00", // totalMontant
                    "0.00", // montantIPay
                    "0.00", // montantIBanking
                    List.of(new ServiceStatus("i-pay", false, "Non autorisé"),
                           new ServiceStatus("i-banking", false, "Non autorisé"))
                )
            ));
        }        // 5. Appeler le service d'agrégation
        return aggregationService.getGlobalBalance(telephone, email, ipayToken)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                logger.error("Erreur lors de la récupération du solde global", e);                return Mono.just(ResponseEntity.internalServerError().body(
                    new GlobalBalanceResponse(
                        "error",
                        "Erreur technique",
                        "0.00", // totalMontant
                        "0.00", // montantIPay
                        "0.00", // montantIBanking
                        List.of(new ServiceStatus("i-pay", false, "Service indisponible"),
                               new ServiceStatus("i-banking", false, "Service indisponible"))
                    )
                ));
            });
    }
}

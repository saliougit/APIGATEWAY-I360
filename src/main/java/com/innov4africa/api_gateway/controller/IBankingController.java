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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/ibanking")
@Tag(name = "IBanking", description = "API pour la gestion des comptes iBanking et la synchronisation avec iPay")
public class IBankingController {
    
    private static final Logger logger = LoggerFactory.getLogger(IBankingController.class);
    
    @Autowired
    private IBankingService iBankingService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
        summary = "Vérifier l'existence d'un utilisateur",
        description = "Vérifie si un utilisateur existe dans iBanking en utilisant son email ou son numéro de téléphone. " +
                     "Au moins l'un des deux paramètres doit être fourni."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Vérification effectuée avec succès",
            content = @Content(schema = @Schema(implementation = IBankingUserCheckResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Paramètres invalides - email ou téléphone requis",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur serveur lors de la vérification",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))
        )
    })
    @GetMapping("/user-check")
    public Mono<ResponseEntity<IBankingUserCheckResponse>> checkUser(
            @Parameter(description = "Email de l'utilisateur à vérifier")
            @RequestParam(required = false) String email,
            
            @Parameter(description = "Numéro de téléphone de l'utilisateur à vérifier")
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

    @Operation(
        summary = "Créer un utilisateur iBanking",
        description = "Crée un nouveau compte utilisateur dans iBanking avec les informations fournies. " +
                     "Le mot de passe peut être synchronisé avec celui d'iPay pour une meilleure expérience utilisateur."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Utilisateur créé avec succès",
            content = @Content(schema = @Schema(implementation = IBankingUserCheckResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données invalides ou manquantes",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Un utilisateur avec cet email ou ce téléphone existe déjà",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur serveur lors de la création",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))
        )
    })
    @PostMapping("/user")
    public Mono<ResponseEntity<IBankingUserCheckResponse>> createUser(
            @Parameter(description = "Email de l'utilisateur", required = true)
            @RequestParam String email,
            
            @Parameter(description = "Numéro de téléphone", required = true)
            @RequestParam String telephone,
            
            @Parameter(description = "Prénom de l'utilisateur", required = true)
            @RequestParam(name = "firstname") String firstName,
            
            @Parameter(description = "Nom de l'utilisateur", required = false)
            @RequestParam(name = "lastname", required = false) String lastName,
            
            @Parameter(
                description = "Mot de passe (utiliser le même que iPay pour la synchronisation)",
                required = true
            )
            @RequestParam String password) {
        
        logger.info("Création d'un utilisateur - email: {}, telephone: {}, prénom: {}, nom: {}", 
                   email, telephone, firstName, lastName != null ? lastName : "");

        String finalLastName = lastName != null ? lastName : "";
        
        return iBankingService.createUser(email, telephone, firstName, finalLastName, password)
            .map(created -> {
                String message = created ? 
                    "Utilisateur créé avec succès" : 
                    "Erreur lors de la création de l'utilisateur";
                
                return ResponseEntity.ok(new IBankingUserCheckResponse(
                    created ? "success" : "error",
                    message,
                    created
                ));
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de la création de l'utilisateur", e);
                return Mono.just(ResponseEntity.internalServerError().body(
                    new IBankingUserCheckResponse("error", "Erreur technique", false)
                ));
            });
    }
}
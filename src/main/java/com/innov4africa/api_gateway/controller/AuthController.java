package com.innov4africa.api_gateway.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innov4africa.api_gateway.model.AuthRequest;
import com.innov4africa.api_gateway.model.AuthResponse;
import com.innov4africa.api_gateway.model.IShopInfo;
import com.innov4africa.api_gateway.model.JwtInfoResponse;
import com.innov4africa.api_gateway.model.LogoutResponse;
import com.innov4africa.api_gateway.model.ServiceStatus;
import com.innov4africa.api_gateway.service.AuthService;
import com.innov4africa.api_gateway.service.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

     @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        return authService.authenticate(request)
            .map(response -> {
                if ("success".equals(response.getStatus())) {
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(401).body(response);
                }
            });
    }

    //     @Operation(summary = "Extraire les infos du token JWT",
    //         description = "Renvoie les informations contenues dans le token JWT")
    // @GetMapping("/extract-jwt-info")
    // public ResponseEntity<JwtInfoResponse> extractJwtInfo(
    //     @RequestHeader("Authorization") String authHeader) {
        
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         return ResponseEntity.badRequest().body(new JwtInfoResponse("error", "Token manquant ou invalide"));
    //     }
        
    //     String token = authHeader.substring(7);
        
    //     if (!jwtUtil.validateToken(token)) {
    //         return ResponseEntity.status(401).body(new JwtInfoResponse("error", "Token invalide ou expiré"));
    //     }
        
    //     try {
    //         JwtInfoResponse response = new JwtInfoResponse();
    //         response.setStatus("success");
    //         response.setEmail(jwtUtil.extractUsername(token));
    //         response.setTelephone(jwtUtil.extractTelephone(token));
    //         response.setUserId(jwtUtil.extractUserId(token));
    //         response.setSeller(jwtUtil.extractUserType(token) != null && 
    //                         jwtUtil.extractUserType(token).equalsIgnoreCase("Seller"));
            
    //         if (response.isSeller()) {
    //              ishopInfo = jwtUtil.extractIShopInfo(token);
    //             response.setIshopInfo(ishopInfo);
    //         }
            
    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(500).body(new JwtInfoResponse("error", "Erreur lors de l'extraction du token"));
    //     }
    // }
        @Operation(summary = "Extraire toutes les infos du token JWT",
        description = "Renvoie toutes les informations contenues dans le token JWT")
    @GetMapping("/extract-jwt-info")
    public ResponseEntity<Map<String, Object>> extractAllJwtInfo(
        @RequestHeader("Authorization") String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Token manquant ou invalide"
            ));
        }
        
        String token = authHeader.substring(7);
        
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of(
                "status", "error",
                "message", "Token invalide ou expiré"
            ));
        }
        
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Ajout du statut
            response.put("status", "success");
            
            // Extraction des informations de base
            response.put("email", jwtUtil.extractUsername(token));
            response.put("telephone", jwtUtil.extractTelephone(token));
            response.put("ipayUserId", jwtUtil.extractUserId(token));
            response.put("accountIdIPay", jwtUtil.extractAccountIdIPay(token));
            response.put("ipayToken", jwtUtil.extractIpayToken(token));
            
            // Informations optionnelles
            String userType = jwtUtil.extractUserType(token);
            if (userType != null) {
                response.put("userType", userType);
            }
            
            String nom = jwtUtil.extractNom(token);
            if (nom != null) {
                response.put("nom", nom);
            }
            
            String prenom = jwtUtil.extractPrenom(token);
            if (prenom != null) {
                response.put("prenom", prenom);
            }
            
            // Si c'est un seller, ajouter les infos iShop
            if ("Seller".equalsIgnoreCase(userType)) {
                IShopInfo ishopInfo = jwtUtil.extractIShopInfo(token);
                if (ishopInfo != null) {
                    response.put("ishopUserId", ishopInfo.getUser_id());
                    response.put("sellerCredit", ishopInfo.getCredit());
                    if (ishopInfo.getDomaineList() != null && !ishopInfo.getDomaineList().isEmpty()) {
                        response.put("primaryDomaine", ishopInfo.getDomaineList().get(0).getLibelle());
                    }
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "Erreur lors de l'extraction du token: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Endpoint pour la déconnexion globale de l'utilisateur de tous les services
     * @param authHeader Le header d'autorisation contenant le JWT
     * @return Une réponse indiquant le succès ou l'échec de la déconnexion globale
     */
    @PostMapping("/logout")
    public Mono<ResponseEntity<LogoutResponse>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Vérification de la présence du header Authorization
        if (authHeader == null || authHeader.isBlank()) {
            logger.warn("Tentative de déconnexion sans header Authorization");
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new LogoutResponse("error", "Token d'authentification manquant",
                    List.of(new ServiceStatus("global", false, "Non autorisé")))
            ));
        }

        // Vérification du format Bearer
        if (!authHeader.startsWith("Bearer ")) {
            logger.warn("Format de token invalide lors de la déconnexion: {}", authHeader);
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new LogoutResponse("error", "Format de token invalide",
                    List.of(new ServiceStatus("global", false, "Non autorisé")))
            ));
        }

        String jwt = authHeader.substring(7);
        logger.info("Demande de déconnexion globale reçue");
        
        // Appel du service pour la déconnexion globale
        return authService.logout(jwt)
            .map(response -> {
                if ("success".equals(response.getStatus())) {
                    return ResponseEntity.ok(response);
                } else if ("partial".equals(response.getStatus())) {
                    return ResponseEntity.ok(response); // Considérer succès partiel comme un succès HTTP 200
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            });
    }

    
    /**
     * Endpoint pour reconnection automatique en cas de session déjà en cours
     * @param request Les identifiants pour se reconnecter
     * @return Une réponse d'authentification avec un nouveau token
     */
    @PostMapping("/reconnect")
    public Mono<ResponseEntity<AuthResponse>> reconnect(@RequestBody AuthRequest request) {
        logger.info("Demande de reconnexion reçue pour: {}", request.getEmail());
        
        return authService.authenticate(request)
            .map(response -> {
                if ("success".equals(response.getStatus())) {
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(401).body(response);
                }
            });
    }
}

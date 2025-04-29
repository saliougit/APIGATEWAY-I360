package com.innov4africa.api_gateway.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.innov4africa.api_gateway.model.AuthRequest;
import com.innov4africa.api_gateway.model.AuthResponse;
import com.innov4africa.api_gateway.model.AuthResult;
import com.innov4africa.api_gateway.model.LogoutResponse;
import com.innov4africa.api_gateway.model.ServiceStatus;
import com.innov4africa.api_gateway.repository.TokenRepository;
import com.innov4africa.api_gateway.repository.UserSessionRepository;

import reactor.core.publisher.Mono;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private IPayService ipayService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private IBankingService iBankingService;
    
    @Autowired(required = false)
    private TokenRepository tokenRepository;
    
    @Autowired
    private UserSessionRepository userSessionRepository;

    public Mono<AuthResponse> authenticate(AuthRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
    
        return ipayService.authenticate(email, password)
            .flatMap(authResult -> {
                if (isSessionEnCours(authResult)) {
                    logger.info("Session déjà en cours détectée pour: {}", email);
                    return forceDisconnectAndReconnect(authResult.getToken(), email, password);
                } 
                else if (authResult.isSuccess()) {
                    logger.info("Authentification réussie pour: {}", email);
                    return processSuccessfulAuthentication(authResult, email, password);
                } 
                else {
                    logger.warn("Erreur d'authentification pour: {}: {}", email, authResult.getMessage());
                    return Mono.just(buildErrorResponse(authResult.getMessage()));
                }
            })
            .onErrorResume(e -> {
                logger.error("Erreur technique lors de l'authentification pour: {}", email, e);
                return Mono.just(buildErrorResponse("Erreur technique: " + e.getMessage()));
            });
    }

    private boolean isSessionEnCours(AuthResult authResult) {
        return authResult.getMessage() != null && 
               authResult.getMessage().contains("session en cours") &&
               authResult.getToken() != null;
    }

    private Mono<AuthResponse> processSuccessfulAuthentication(AuthResult authResult, String email, String password) {
        String ipayToken = authResult.getToken();
        String telephone = authResult.getTelephone();
        String userId = authResult.getIduser();
        
        // Sauvegarder les informations de session iPay
        if (ipayToken != null && (userId != null || telephone != null)) {
            userSessionRepository.saveUserSession(ipayToken, userId, telephone);
        }

        // Vérifier l'existence dans iBanking et créer si nécessaire
        return iBankingService.verifyUserExists(email, telephone)
            .flatMap(existsInIBanking -> {
                if (!existsInIBanking) {
                    // Créer le compte iBanking avec les mêmes credentials
                    return iBankingService.createUser(
                        email, 
                        telephone, 
                        authResult.getPrenom(),
                        authResult.getNom(),
                        password  // Utiliser le même mot de passe que iPay
                    ).map(created -> {
                        List<ServiceStatus> services = new ArrayList<>();
                        services.add(new ServiceStatus("i-pay", true, authResult.getMessage()));
                        
                        String ibankingMessage = created ? 
                            "Compte iBanking créé avec succès" : 
                            "Échec de la création du compte iBanking";
                        services.add(new ServiceStatus("i-banking", created, ibankingMessage));

                        String jwtToken = jwtUtil.generateIpayToken(email, ipayToken, telephone, userId);
                        return new AuthResponse("success", authResult.getMessage(), jwtToken, services);
                    });
                } else {
                    // Le compte existe déjà
                    List<ServiceStatus> services = new ArrayList<>();
                    services.add(new ServiceStatus("i-pay", true, authResult.getMessage()));
                    services.add(new ServiceStatus("i-banking", true, "Compte iBanking disponible"));

                    String jwtToken = jwtUtil.generateIpayToken(email, ipayToken, telephone, userId);
                    return Mono.just(new AuthResponse("success", authResult.getMessage(), jwtToken, services));
                }
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de la vérification/création iBanking", e);
                List<ServiceStatus> services = new ArrayList<>();
                services.add(new ServiceStatus("i-pay", true, authResult.getMessage()));
                services.add(new ServiceStatus("i-banking", false, "Service iBanking temporairement indisponible"));
                
                String jwtToken = jwtUtil.generateIpayToken(email, ipayToken, telephone, userId);
                return Mono.just(new AuthResponse("success", authResult.getMessage(), jwtToken, services));
            });
    }

    private Mono<AuthResponse> forceDisconnectAndReconnect(String existingToken, String email, String password) {
        return ipayService.deconnexionUser(existingToken)
            .flatMap(deconnectResponse -> {
                logger.info("Déconnexion forcée effectuée pour: {}", email);
                return Mono.delay(Duration.ofMillis(1500))
                    .then(ipayService.authenticate(email, password));
            })
            .flatMap(newAuthResult -> {
                if (newAuthResult.isSuccess()) {
                    logger.info("Reconnexion réussie pour: {}", email);
                    return processSuccessfulAuthentication(newAuthResult, email, password);
                } else {
                    logger.warn("Échec de la reconnexion pour: {}: {}", email, newAuthResult.getMessage());
                    return Mono.just(buildErrorResponse(newAuthResult.getMessage()));
                }
            })
            .onErrorResume(e -> {
                logger.error("Erreur technique lors de la reconnexion pour: {}", email, e);
                return Mono.just(buildErrorResponse("Erreur technique lors de la reconnexion: " + e.getMessage()));
            });
    }

    public Mono<LogoutResponse> logout(String jwt) {
        if (!jwtUtil.validateToken(jwt)) {
            return Mono.just(new LogoutResponse(
                "error", 
                "Token invalide ou expiré",
                List.of(new ServiceStatus("global", false, "Non autorisé"))
            ));
        }

        if (tokenRepository != null) {
            tokenRepository.saveRevokedToken(jwt, jwtUtil.getExpirationDateFromToken(jwt));
        }

        String ipayToken = jwtUtil.extractIpayToken(jwt);
        if (ipayToken == null) {
            return Mono.just(new LogoutResponse(
                "error", 
                "Token incomplet", 
                List.of(new ServiceStatus("i-pay", false, "Token iPay non disponible"))
            ));
        }

        return ipayService.deconnexionUser(ipayToken)
            .map(response -> {
                List<ServiceStatus> services = new ArrayList<>();
                services.add(new ServiceStatus("i-pay", true, "Déconnecté"));
                services.add(new ServiceStatus("i-banking", true, "Déconnecté"));
                return new LogoutResponse("success", "Déconnexion globale réussie", services);
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de la déconnexion", e);
                List<ServiceStatus> services = new ArrayList<>();
                services.add(new ServiceStatus("i-pay", false, "Erreur de déconnexion"));
                services.add(new ServiceStatus("i-banking", true, "Déconnecté"));
                return Mono.just(new LogoutResponse("partial", "Déconnexion partielle", services));
            });
    }

    private AuthResponse buildErrorResponse(String message) {
        List<ServiceStatus> services = new ArrayList<>();
        services.add(new ServiceStatus("i-pay", false, message));
        services.add(new ServiceStatus("i-banking", false, "Service non disponible"));
        return new AuthResponse("error", message, null, services);
    }
}

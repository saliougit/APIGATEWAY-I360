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
import com.innov4africa.api_gateway.model.IShopInfo;
import com.innov4africa.api_gateway.model.IShopLoginRequest;
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
    
    @Autowired
    private IShopService iShopService;
    
    @Autowired(required = false)
    private TokenRepository tokenRepository;
    
    @Autowired
    private UserSessionRepository userSessionRepository;        public Mono<AuthResponse> authenticate(AuthRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // Préparer la requête iShop qui sera utilisée dans les deux cas
        Mono<com.innov4africa.api_gateway.model.IShopLoginResponse> ishopMono = 
            iShopService.login(new IShopLoginRequest(email, password));

        // Vérifier iPay
        return ipayService.authenticate(email, password)
            .flatMap(initialAuthResult -> {
                if (isSessionEnCours(initialAuthResult)) {
                    logger.info("Session en cours détectée pour {}, lancement parallèle", email);
                    // Lancer la déconnexion/reconnexion iPay en parallèle avec iShop
                    Mono<AuthResult> reconnectMono = forceDisconnectAndReconnect(initialAuthResult.getToken(), email, password);
                    
                    // Attendre les deux résultats
                    return Mono.zip(reconnectMono, ishopMono)
                        .flatMap(tuple -> {
                            AuthResult newAuthResult = tuple.getT1();
                            com.innov4africa.api_gateway.model.IShopLoginResponse ishopResponse = tuple.getT2();
                            return processAuthenticationWithIShop(email, password, newAuthResult, ishopResponse);
                        });
                } else {
                    // Cas normal : utiliser directement le résultat iShop
                    return ishopMono.flatMap(ishopResponse ->
                        processAuthenticationWithIShop(email, password, initialAuthResult, ishopResponse));
                }
            });
    }

    private Mono<AuthResponse> processAuthenticationWithIShop(
            String email, 
            String password, 
            AuthResult ipayResult,
            com.innov4africa.api_gateway.model.IShopLoginResponse ishopResponse) {
        
        if (!ipayResult.isSuccess()) {
            return Mono.just(buildErrorResponse(ipayResult.getMessage()));
        }

        String ipayToken = ipayResult.getToken();
        String telephone = ipayResult.getTelephone();
        String userId = ipayResult.getIduser();

        // Sauvegarder la session iPay
        if (ipayToken != null && (userId != null || telephone != null)) {
            userSessionRepository.saveUserSession(ipayToken, userId, telephone);
        }

        // Préparer les données iShop (déjà reçues en parallèle)
        boolean ishopSuccess = ishopResponse != null && "success".equalsIgnoreCase(ishopResponse.getStatus());
        boolean isSeller = ishopSuccess && ishopResponse.getUser_type() != null && 
                          "Seller".equalsIgnoreCase(ishopResponse.getUser_type());

        List<ServiceStatus> services = new ArrayList<>();
        services.add(new ServiceStatus("i-pay", true, "Authentification iPay réussie"));
        if (ishopSuccess) {
            services.add(new ServiceStatus("i-shop", true, 
                isSeller ? "Compte seller iShop validé" : "Compte buyer iShop"));
        } else {
            String ishopMsg = (ishopResponse != null && ishopResponse.getMessage() != null) ? 
                ishopResponse.getMessage() : "Erreur d'authentification iShop";
            services.add(new ServiceStatus("i-shop", false, ishopMsg));
        }

        // Exécuter getAllListAccount et iBanking en parallèle
        return Mono.zip(
            // Premier flux : récupération de l'accountId avec retry
            ipayService.getAllListAccount(ipayToken, telephone)
                .map(xml -> ipayService.extractAccountIdFromResponse(xml))
                .retryWhen(
                    reactor.util.retry.Retry.backoff(2, java.time.Duration.ofMillis(300))
                        .maxBackoff(java.time.Duration.ofSeconds(1))
                ),
            // Deuxième flux : vérification/création iBanking
            iBankingService.verifyUserExists(email, telephone)
                .flatMap(exists -> {
                    if (!exists) {
                        return iBankingService.createUser(
                            email, telephone, ipayResult.getPrenom(),
                            ipayResult.getNom(), password
                        ).map(created -> {
                            services.add(new ServiceStatus("i-banking", created,
                                created ? "Compte iBanking créé avec succès" : "Échec de la création du compte iBanking"));
                            return created;
                        });
                    } else {
                        services.add(new ServiceStatus("i-banking", true, "Compte iBanking disponible"));
                        return Mono.just(true);
                    }
                })
        ).flatMap(tuple -> {
            String accountIdIPay = tuple.getT1();
            boolean ibankingResult = tuple.getT2();

            // Génération finale du token avec toutes les infos
            String jwtToken;
            IShopInfo ishopInfo = null;
            String globalMessage;

            if (isSeller && ishopSuccess) {
                ishopInfo = IShopInfo.fromLoginResponse(ishopResponse);
                jwtToken = jwtUtil.generateCompleteSellerToken(
                    email, ipayToken, telephone, userId, accountIdIPay, ishopInfo);
                globalMessage = "Authentification SSO réussie (Seller)";
            } else {
                jwtToken = jwtUtil.generateIpayTokenWithAccount(
                    email, ipayToken, telephone, userId, accountIdIPay);
                globalMessage = "Authentification réussie";
            }

            return Mono.just(new AuthResponse(
                "success",
                globalMessage,
                jwtToken,
                services,
                ishopInfo,
                isSeller
            ));
        }).onErrorResume(e -> {
            logger.error("Erreur lors du processus d'authentification", e);
            services.add(new ServiceStatus("i-banking", false, 
                "Service iBanking temporairement indisponible"));
            
            String jwtToken = isSeller ? 
                jwtUtil.generateTokenWithIShopInfo(email, ipayToken, telephone, userId, 
                    IShopInfo.fromLoginResponse(ishopResponse)) :
                jwtUtil.generateIpayToken(email, ipayToken, telephone, userId);
            
            return Mono.just(new AuthResponse(
                "success",
                "Authentification réussie (sans accountId)",
                jwtToken,
                services,
                isSeller ? IShopInfo.fromLoginResponse(ishopResponse) : null,
                isSeller
            ));
        });
    }

    private Mono<AuthResponse> processAuthentication(String email, String password, AuthResult ipayResult) {
        if (!ipayResult.isSuccess()) {
            return Mono.just(buildErrorResponse(ipayResult.getMessage()));
        }

        // Lancer iShop après la confirmation iPay
        return iShopService.login(new IShopLoginRequest(email, password))
            .flatMap(ishopResponse -> {
                boolean ishopSuccess = ishopResponse != null && "success".equalsIgnoreCase(ishopResponse.getStatus());
                boolean isSeller = ishopSuccess && ishopResponse.getUser_type() != null && "Seller".equalsIgnoreCase(ishopResponse.getUser_type());

                String ipayToken = ipayResult.getToken();
                String telephone = ipayResult.getTelephone();
                String userId = ipayResult.getIduser();

                // Sauvegarder la session iPay
                if (ipayToken != null && (userId != null || telephone != null)) {
                    userSessionRepository.saveUserSession(ipayToken, userId, telephone);
                }

                // Préparer la liste des statuts de service
                List<ServiceStatus> services = new ArrayList<>();
                services.add(new ServiceStatus("i-pay", true, "Authentification iPay réussie"));
                if (ishopSuccess) {
                    services.add(new ServiceStatus("i-shop", true, isSeller ? "Compte seller iShop validé" : "Compte buyer iShop"));
                } else {
                    String ishopMsg = (ishopResponse != null && ishopResponse.getMessage() != null) ? ishopResponse.getMessage() : "Erreur d'authentification iShop";
                    services.add(new ServiceStatus("i-shop", false, ishopMsg));
                }

                // Vérifier/créer e-banking
                return iBankingService.verifyUserExists(email, telephone)
                    .flatMap(existsInIBanking -> {
                        if (!existsInIBanking) {
                            return iBankingService.createUser(
                                email,
                                telephone,
                                ipayResult.getPrenom(),
                                ipayResult.getNom(),
                                password
                            ).flatMap(created -> {
                                services.add(new ServiceStatus("i-banking", created, created ? "Compte iBanking créé avec succès" : "Échec de la création du compte iBanking"));
                                return buildFinalAuthResponse(email, ipayToken, telephone, userId, ishopSuccess, isSeller, ishopResponse, services);
                            });
                        } else {
                            services.add(new ServiceStatus("i-banking", true, "Compte iBanking disponible"));
                            return buildFinalAuthResponse(email, ipayToken, telephone, userId, ishopSuccess, isSeller, ishopResponse, services);
                        }
                    })
                    .onErrorResume(e -> {
                        logger.error("Erreur lors de la vérification/création iBanking", e);
                        services.add(new ServiceStatus("i-banking", false, "Service iBanking temporairement indisponible"));
                        return buildFinalAuthResponse(email, ipayToken, telephone, userId, ishopSuccess, isSeller, ishopResponse, services);
                    });
            })
            .onErrorResume(e -> {
                logger.error("Erreur technique lors de l'authentification iShop", e);
                return Mono.just(buildErrorResponse("Erreur technique: " + e.getMessage()));
            });
    }

    private Mono<AuthResponse> buildFinalAuthResponse(String email, String ipayToken, String telephone, 
                                         String userId, boolean ishopSuccess, boolean isSeller, 
                                         com.innov4africa.api_gateway.model.IShopLoginResponse ishopResponse, 
                                         List<ServiceStatus> services) {
    
    // Récupérer l'accountId depuis iPay (comme dans l'ancienne version)
    return ipayService.getAllListAccount(ipayToken, telephone)
        .flatMap(xmlResponse -> {
            String accountIdIPay = ipayService.extractAccountIdFromResponse(xmlResponse);
            
            // Génération du token adapté
            String jwtToken;
            IShopInfo ishopInfo = null;
            String globalMessage;
            
            if (isSeller && ishopSuccess) {
                // Cas Seller - Token complet avec toutes les infos
                ishopInfo = IShopInfo.fromLoginResponse(ishopResponse);
                jwtToken = jwtUtil.generateCompleteSellerToken(
                    email, 
                    ipayToken, 
                    telephone, 
                    userId, 
                    accountIdIPay,
                    ishopInfo
                );
                globalMessage = "Authentification SSO réussie (Seller)";
            } else {
                // Cas Buyer - Token avec juste les infos iPay + accountId
                jwtToken = jwtUtil.generateIpayTokenWithAccount(
                    email, 
                    ipayToken, 
                    telephone, 
                    userId, 
                    accountIdIPay
                );
                globalMessage = "Authentification réussie";
            }
            
            return Mono.just(new AuthResponse(
                "success",
                globalMessage,
                jwtToken,
                services,
                ishopInfo,
                isSeller
            ));
        })
        .onErrorResume(e -> {
            logger.error("Erreur récupération accountId", e);
            // Fallback sans accountId
            String jwtToken = isSeller ? 
                jwtUtil.generateTokenWithIShopInfo(email, ipayToken, telephone, userId, IShopInfo.fromLoginResponse(ishopResponse)) :
                jwtUtil.generateIpayToken(email, ipayToken, telephone, userId);
            
            return Mono.just(new AuthResponse(
                "success",
                "Authentification réussie (sans accountId)",
                jwtToken,
                services,
                isSeller ? IShopInfo.fromLoginResponse(ishopResponse) : null,
                isSeller
            ));
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
        services.add(new ServiceStatus("i-shop", false, "Service non disponible"));
        return new AuthResponse("error", message, null, services);
    }

    private boolean isSessionEnCours(AuthResult authResult) {
        return authResult.getMessage() != null && 
               authResult.getMessage().contains("session en cours") &&
               authResult.getToken() != null;
    }    private Mono<AuthResult> forceDisconnectAndReconnect(String existingToken, String email, String password) {
        return ipayService.deconnexionUser(existingToken)
            .flatMap(deconnectResponse -> {
                logger.info("Déconnexion forcée effectuée pour: {}", email);
                return ipayService.authenticate(email, password)
                    .retryWhen(reactor.util.retry.Retry.backoff(3, Duration.ofMillis(500))
                        .maxBackoff(Duration.ofSeconds(2))
                        .doBeforeRetry(retrySignal -> 
                            logger.info("Tentative de reconnexion {} pour {}", 
                                retrySignal.totalRetries() + 1, email)));
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de la déconnexion forcée", e);
                return ipayService.authenticate(email, password)
                    .retryWhen(reactor.util.retry.Retry.backoff(2, Duration.ofMillis(500))
                        .maxBackoff(Duration.ofSeconds(1)));
            });
    }
}

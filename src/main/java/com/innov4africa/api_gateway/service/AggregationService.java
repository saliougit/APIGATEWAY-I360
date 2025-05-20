package com.innov4africa.api_gateway.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov4africa.api_gateway.model.GlobalBalanceResponse;
import com.innov4africa.api_gateway.model.IBankingBalanceResponse;
import com.innov4africa.api_gateway.model.ServiceStatus;

import reactor.core.publisher.Mono;

@Service
public class AggregationService {
    private static final Logger logger = LoggerFactory.getLogger(AggregationService.class);
    private static final String CACHE_KEY_PREFIX = "balance:";
    private static final long CACHE_TTL_SECONDS = 300; // 5 minutes
    
    @Autowired
    private IPayService ipayService;
    
    @Autowired
    private IBankingService iBankingService;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;/**
     * Agrège les soldes de iPay et iBanking pour un utilisateur
     */    public Mono<GlobalBalanceResponse> getGlobalBalance(String telephone, String email, String ipayToken) {
        String cacheKey = CACHE_KEY_PREFIX + telephone + ":" + email;
        
        // Vérifier le cache d'abord
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue != null) {
            try {
                logger.info("Utilisation du cache pour le solde global - telephone: {}", telephone);
                ObjectMapper mapper = new ObjectMapper();
                return Mono.just(mapper.readValue(cachedValue, GlobalBalanceResponse.class));
            } catch (Exception e) {
                logger.warn("Erreur lors de la lecture du cache", e);
            }
        }

        List<ServiceStatus> services = new ArrayList<>();
        // 1. Appel parallèle des services iPay et iBanking
        return Mono.zip(
            ipayService.getSolde(telephone, ipayToken),
            iBankingService.getSolde(email)
        ).flatMap(tuple -> {
            String ipayXmlResponse = tuple.getT1();
            IBankingBalanceResponse iBankingResponse = tuple.getT2();
            String montantIPay = "0.00";
            String montantIBanking = "0.00";

            try {
                // Parser la réponse XML iPay
                Document doc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(new InputSource(new StringReader(ipayXmlResponse)));
                
                var xpath = XPathFactory.newInstance().newXPath();
                String error = xpath.evaluate("//return/error", doc);
                String message = xpath.evaluate("//return/message", doc);
                
                if (!"0".equals(error)) {
                    services.add(new ServiceStatus("i-pay", false, message));
                } else {
                    montantIPay = xpath.evaluate("//return/montant", doc);
                    services.add(new ServiceStatus("i-pay", true, "Solde récupéré"));
                }

                // Traiter la réponse iBanking
                if ("success".equals(iBankingResponse.getStatus())) {
                    montantIBanking = iBankingResponse.getMontant();
                    services.add(new ServiceStatus("i-banking", true, "Solde récupéré"));
                } else {
                    services.add(new ServiceStatus("i-banking", false, iBankingResponse.getMessage()));
                }

                // Calculer le total
                double total = Double.parseDouble(montantIPay.replace(",", ".")) 
                            + Double.parseDouble(montantIBanking.replace(",", "."));
                
                return Mono.just(new GlobalBalanceResponse(
                    "success",
                    "Solde global récupéré",
                    String.format("%.2f", total),
                    montantIPay,                    montantIBanking,
                    services
                )).doOnSuccess(response -> {
                    try {
                        // Mettre en cache la réponse
                        ObjectMapper mapper = new ObjectMapper();
                        String jsonValue = mapper.writeValueAsString(response);
                        redisTemplate.opsForValue().set(cacheKey, jsonValue, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        logger.warn("Erreur lors de la mise en cache", e);
                    }
                });
                } catch (Exception e) {
                    logger.error("Erreur de traitement de la réponse iPay", e);
                    return Mono.just(new GlobalBalanceResponse(
                        "error",
                        "Erreur technique",
                        "0.00",
                        "0.00",
                        "0.00",
                        List.of(new ServiceStatus("i-pay", false, "Erreur de traitement"),
                               new ServiceStatus("i-banking", false, "Service en cours d'implémentation"))
                    ));
                }
            })
            .onErrorResume(e -> {
                logger.error("Erreur lors de la récupération des soldes", e);
                return Mono.just(new GlobalBalanceResponse(
                    "error",
                    "Service indisponible",
                    "0.00",
                    "0.00",
                    "0.00",
                    List.of(new ServiceStatus("i-pay", false, "Service indisponible"),
                           new ServiceStatus("i-banking", false, "Service en cours d'implémentation"))
                ));
            });
    }
}

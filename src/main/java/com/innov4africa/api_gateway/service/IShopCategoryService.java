package com.innov4africa.api_gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.innov4africa.api_gateway.model.DomaineResponse;
import com.innov4africa.api_gateway.model.CategorieResponse;

import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class IShopCategoryService {
    private static final Logger logger = LoggerFactory.getLogger(IShopCategoryService.class);

    private final WebClient webClient;

    @Autowired
    public IShopCategoryService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://ibusinesscompanies.com:8443")
            .build();
    }

    /**
     * Récupère la liste des domaines pour un utilisateur
     *
     * @param userId Identifiant de l'utilisateur
     * @return Liste des domaines
     */
    public Mono<List<DomaineResponse>> getDomaines(Integer userId) {
        logger.info("Récupération des domaines pour user_id={}", userId);
        
        return webClient.get()
            .uri("/mobile-ws/user/getDomaines?user_id={userId}", userId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<DomaineResponse>>() {})
            .doOnNext(domaines -> logger.debug("Domaines récupérés: {}", domaines.size()));
    }

    /**
     * Récupère les catégories d'un domaine pour un utilisateur
     *
     * @param userId Identifiant de l'utilisateur
     * @param domaineId Identifiant du domaine
     * @return Liste des catégories
     */
    public Mono<List<CategorieResponse>> getCategories(Integer userId, Integer domaineId) {
        logger.info("Récupération des catégories pour user_id={}, domaine_id={}", userId, domaineId);
        
        return webClient.get()
            .uri("/mobile-ws/user/getCategorieOfUser?user_id={userId}&id_domaine={domaineId}", 
                userId, domaineId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<CategorieResponse>>() {})
            .doOnNext(categories -> logger.debug("Catégories récupérées: {}", categories.size()));
    }

    /**
     * Récupère les sous-catégories d'une catégorie pour un utilisateur
     *
     * @param userId Identifiant de l'utilisateur
     * @param categorieId Identifiant de la catégorie
     * @return Liste des sous-catégories
     */
    public Mono<List<CategorieResponse>> getSousCategories(Integer userId, Integer categorieId) {
        logger.info("Récupération des sous-catégories pour user_id={}, categorie={}", userId, categorieId);
        
        return webClient.get()
            .uri("/mobile-ws/user/getSousCategorieOfUser?user_id={userId}&categorie={categorieId}", 
                userId, categorieId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<CategorieResponse>>() {})
            .doOnNext(sousCategories -> logger.debug("Sous-catégories récupérées: {}", sousCategories.size()));
    }
}

package com.innov4africa.api_gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.innov4africa.api_gateway.model.DomaineResponse;
import com.innov4africa.api_gateway.model.CategorieResponse;
import com.innov4africa.api_gateway.service.IShopCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/api/ishop/categories")
public class IShopCategoryController {
    private static final Logger logger = LoggerFactory.getLogger(IShopCategoryController.class);

    @Autowired
    private IShopCategoryService categoryService;

    @Operation(summary = "Liste les domaines disponibles",
              description = "Retourne la liste des domaines (marketplaces) accessibles pour l'utilisateur")
    @GetMapping("/domaines")
    public Mono<ResponseEntity<List<DomaineResponse>>> getDomaines(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @RequestParam Integer userId) {
        return categoryService.getDomaines(userId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Liste les catégories d'un domaine",
              description = "Retourne la liste des catégories disponibles dans un domaine spécifique")
    @GetMapping("/liste")
    public Mono<ResponseEntity<List<CategorieResponse>>> getCategories(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @RequestParam Integer userId,
            @Parameter(description = "ID du domaine", required = true)
            @RequestParam Integer domaineId) {
        return categoryService.getCategories(userId, domaineId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Liste les sous-catégories",
              description = "Retourne la liste des sous-catégories d'une catégorie spécifique")
    @GetMapping("/sous-categories")
    public Mono<ResponseEntity<List<CategorieResponse>>> getSousCategories(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @RequestParam Integer userId,
            @Parameter(description = "ID de la catégorie", required = true)
            @RequestParam Integer categorieId) {
        return categoryService.getSousCategories(userId, categorieId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

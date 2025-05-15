package com.innov4africa.api_gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov4africa.api_gateway.model.IShopProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class IShopProductCacheService {
    private static final Logger logger = LoggerFactory.getLogger(IShopProductCacheService.class);
    private static final String CACHE_KEY_PREFIX = "ishop:products:";
    private static final long CACHE_TTL_HOURS = 1; // Durée de vie du cache : 1 heure

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getCacheKey(Integer userId, String language) {
        return CACHE_KEY_PREFIX + userId + ":" + language;
    }

    public void cacheProducts(Integer userId, String language, List<IShopProduct> products) {
        try {
            String cacheKey = getCacheKey(userId, language);
            String productsJson = objectMapper.writeValueAsString(products);
            redisTemplate.opsForValue().set(cacheKey, productsJson, CACHE_TTL_HOURS, TimeUnit.HOURS);
            logger.debug("Produits mis en cache pour userId={}, language={}, count={}", 
                userId, language, products.size());
        } catch (JsonProcessingException e) {
            logger.error("Erreur lors de la mise en cache des produits", e);
        }
    }

    public List<IShopProduct> getCachedProducts(Integer userId, String language) {
        String cacheKey = getCacheKey(userId, language);
        String productsJson = redisTemplate.opsForValue().get(cacheKey);
        
        if (productsJson == null) {
            return null;
        }

        try {
            List<IShopProduct> products = objectMapper.readValue(productsJson, 
                new TypeReference<List<IShopProduct>>() {});
            logger.debug("Produits récupérés du cache pour userId={}, language={}, count={}", 
                userId, language, products.size());
            return products;
        } catch (JsonProcessingException e) {
            logger.error("Erreur lors de la lecture des produits du cache", e);
            return Collections.emptyList();
        }
    }

    public void invalidateCache(Integer userId, String language) {
        String cacheKey = getCacheKey(userId, language);
        redisTemplate.delete(cacheKey);
        logger.debug("Cache invalidé pour userId={}, language={}", userId, language);
    }
}

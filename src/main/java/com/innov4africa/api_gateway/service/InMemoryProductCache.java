package com.innov4africa.api_gateway.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import com.innov4africa.api_gateway.model.IShopProduct;

@Component
public class InMemoryProductCache {
    private Map<String, List<IShopProduct>> tempCache = new ConcurrentHashMap<>();
    private Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = TimeUnit.MINUTES.toMillis(5);

    private String getCacheKey(String userId, String language) {
        return userId + "_" + language;
    }

    public void store(String userId, String language, List<IShopProduct> products) {
        String key = getCacheKey(userId, language);
        tempCache.put(key, products);
        cacheTimestamps.put(key, System.currentTimeMillis());
    }

    public List<IShopProduct> get(String userId, String language) {
        String key = getCacheKey(userId, language);
        Long timestamp = cacheTimestamps.get(key);
        
        if (timestamp == null) {
            return null;
        }

        // Vérifier si le cache est expiré
        if (System.currentTimeMillis() - timestamp > CACHE_DURATION) {
            tempCache.remove(key);
            cacheTimestamps.remove(key);
            return null;
        }

        return tempCache.get(key);
    }

    public void clear() {
        tempCache.clear();
        cacheTimestamps.clear();
    }
}

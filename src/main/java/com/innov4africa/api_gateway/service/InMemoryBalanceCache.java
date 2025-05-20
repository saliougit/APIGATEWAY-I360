package com.innov4africa.api_gateway.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.innov4africa.api_gateway.model.GlobalBalanceResponse;

@Component
public class InMemoryBalanceCache {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryBalanceCache.class);
    private static final long CACHE_DURATION = TimeUnit.MINUTES.toMillis(5); // 5 minutes

    private final Map<String, GlobalBalanceResponse> balanceCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();

    private String getCacheKey(String telephone, String email) {
        return telephone + "_" + email;
    }

    public void store(String telephone, String email, GlobalBalanceResponse balance) {
        String key = getCacheKey(telephone, email);
        balanceCache.put(key, balance);
        cacheTimestamps.put(key, System.currentTimeMillis());
        logger.debug("Balance stockée en cache local pour telephone={}, email={}", telephone, email);
    }

    public GlobalBalanceResponse get(String telephone, String email) {
        String key = getCacheKey(telephone, email);
        Long timestamp = cacheTimestamps.get(key);
        
        if (timestamp == null) {
            return null;
        }

        // Vérifier si le cache est expiré
        if (System.currentTimeMillis() - timestamp > CACHE_DURATION) {
            balanceCache.remove(key);
            cacheTimestamps.remove(key);
            return null;
        }

        GlobalBalanceResponse balance = balanceCache.get(key);
        if (balance != null) {
            logger.debug("Balance trouvée dans le cache local pour telephone={}, email={}", telephone, email);
        }
        return balance;
    }

    public void clear() {
        balanceCache.clear();
        cacheTimestamps.clear();
    }
}

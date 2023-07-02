package com.celonis.weather.cache;

import com.celonis.weather.model.forecast.ForecastEntity;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
public class CaffeineCache implements ICaffeineCache{
    private final Cache<String, ForecastEntity> cache;

    public CaffeineCache(){
        cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofDays(3))
                .build();
    }

    public ForecastEntity getIfPresent(String key){
        return cache.getIfPresent(key);
    }

    /**
     * The get operation of caffeine cache add the key-value only if is not present
     * @param key
     * @param forecast
     * @return
     */
    public ForecastEntity addIfNotPresent(String key, ForecastEntity forecast){
        return cache.get(key, (k)->forecast);
    }

    @Override
    public Set<String> getAllKeys() {
        return cache.asMap().keySet();
    }
}

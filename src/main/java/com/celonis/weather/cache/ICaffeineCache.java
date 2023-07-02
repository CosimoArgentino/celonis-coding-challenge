package com.celonis.weather.cache;

import com.celonis.weather.model.forecast.ForecastEntity;

import java.util.Set;

public interface ICaffeineCache {
    ForecastEntity getIfPresent(String key);
    ForecastEntity addIfNotPresent(String key, ForecastEntity forecast);
    Set<String> getAllKeys();
}

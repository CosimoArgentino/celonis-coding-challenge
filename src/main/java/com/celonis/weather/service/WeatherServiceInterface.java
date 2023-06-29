package com.celonis.weather.service;

import com.celonis.weather.model.Weather;

public interface WeatherServiceInterface {
    void fetchCityWeather(String city);
}

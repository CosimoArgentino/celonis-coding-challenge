package com.celonis.weather.service;

import com.celonis.weather.model.Weather;

public interface WeatherServiceInterface {
    Weather fetchCityWeather(String city);
}

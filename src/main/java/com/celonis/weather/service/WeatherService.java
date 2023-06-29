package com.celonis.weather.service;

import com.celonis.weather.model.Weather;
import org.springframework.stereotype.Service;

@Service
public class WeatherService implements WeatherServiceInterface{

    @Override
    public Weather fetchCityWeather(String city) {
        return null;
    }
}

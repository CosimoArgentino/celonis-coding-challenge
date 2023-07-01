package com.celonis.weather.service;

import com.celonis.weather.dto.ForecastPresentationDTO;

import java.util.List;
import java.util.Set;

public interface WeatherServiceInterface {
    SaveStatus saveCityWeather(String city);
    List<ForecastPresentationDTO> fetchCityWeather(String city);
    Set<ForecastPresentationDTO> fetchAll();
}

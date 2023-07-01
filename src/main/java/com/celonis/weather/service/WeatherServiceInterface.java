package com.celonis.weather.service;

import com.celonis.weather.dto.ForecastPresentationDTO;

import java.util.List;

public interface WeatherServiceInterface {
    SaveStatus saveCityWeather(String city);
    List<ForecastPresentationDTO> fetchCityWeather(String city);
    List<ForecastPresentationDTO> fetchAll();
}

package com.celonis.weather.service;

import com.celonis.weather.dto.ForecastPresentationDTO;

import java.util.List;
import java.util.Set;

public interface IForecastService {
    SaveStatus saveCityForecast(String city);
    List<ForecastPresentationDTO> fetchCityForecast(String city);
    Set<ForecastPresentationDTO> fetchAllForecasts();
}

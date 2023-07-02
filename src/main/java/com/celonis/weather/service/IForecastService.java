package com.celonis.weather.service;

import com.celonis.weather.dto.ForecastPresentationDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IForecastService {
    SaveStatus saveCityForecast(String city, LocalDate date);
    List<ForecastPresentationDTO> fetchCityForecast(String city, LocalDate date);
    Set<ForecastPresentationDTO> fetchAllForecasts(LocalDate date);
}

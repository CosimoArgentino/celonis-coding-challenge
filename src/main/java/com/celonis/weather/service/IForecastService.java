package com.celonis.weather.service;

import com.celonis.weather.dto.ForecastPresentationDTO;

import java.time.LocalDate;
import java.util.List;

public interface IForecastService {
    SaveStatus saveCityForecast(String city, LocalDate date);
    List<ForecastPresentationDTO> fetchCityForecast(String city, LocalDate date);
    List<ForecastPresentationDTO> fetchAllForecasts(LocalDate date);
}

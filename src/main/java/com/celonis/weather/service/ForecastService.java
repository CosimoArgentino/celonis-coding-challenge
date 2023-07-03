package com.celonis.weather.service;

import com.celonis.weather.cache.ICaffeineCache;
import com.celonis.weather.dto.ForecastPresentationDTO;
import com.celonis.weather.dto.WeatherApiDTO;
import com.celonis.weather.model.forecast.ForecastEntity;
import com.celonis.weather.repository.IForecastDAO;
import com.celonis.weather.service.exception.ForecastLocationNotFoundException;
import com.celonis.weather.service.exception.WeatherApiException;
import jakarta.persistence.Tuple;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ForecastService implements IForecastService {

    private final ICaffeineCache cache;
    private final static Logger logger = LoggerFactory.getLogger(ForecastService.class);
    private final IForecastDAO forecastDAO;
    @Value("${apiKey}")
    private String apiKey;
    @Value("${weatherApi}")
    private String weatherApi;

    public ForecastService(IForecastDAO forecastDAO, ICaffeineCache caffeineCache){
        this.forecastDAO = forecastDAO;
        this.cache = caffeineCache;
    }

    @Override
    public SaveStatus saveCityForecast(String city, LocalDate date) {

        LocalDate now = date != null ? date : LocalDate.now();
        String todayKey = now.toString() + city;
        String tomorrowKey = now.plusDays(1).toString() + city;

        if(cache.getIfPresent(todayKey) != null && cache.getIfPresent(tomorrowKey) != null){
            //both days are presents in cache
            return SaveStatus.SKIPPED;
        }

        //if it's saved just one day, it's not important because the weather api does not take a range date,
        //the system will fetch the weather for two days in any case
        RestTemplate restTemplate = new RestTemplate();
        try {
            logger.trace(String.format("fetch %s forecast from weather api", city));
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(weatherApi)
                    .queryParam("q",city)
                    .queryParam("days", 2)
                    .queryParam("aqi","no")
                    .queryParam("alerts","no")
                    .queryParam("key", apiKey);

            ResponseEntity<WeatherApiDTO> response = restTemplate.getForEntity(builder.toUriString(), WeatherApiDTO.class);
            WeatherApiDTO weather = response.getBody();

            List<ForecastEntity> forecastEntities = weather.toEntity();
            SaveStatus status =  checkAndAddToCache(forecastEntities);

            forecastDAO.saveAll(forecastEntities);

            return status;
        }catch(RuntimeException exc){
            if(exc.getMessage().contains("No matching location found")){
                String errorMessage = String.format("city %s not found on weather api", city);
                logger.error(errorMessage, city);
                throw new ForecastLocationNotFoundException(errorMessage, exc);
            }
            String errorMessage = String.format("error on fetching weather for %s, error message: %s", city, exc.getMessage());
            logger.error(errorMessage);
            throw new WeatherApiException(errorMessage, exc);
        }
    }

    @Override
    public List<ForecastPresentationDTO> fetchCityForecast(String city, LocalDate date) {
        LocalDate now = date != null ? date : LocalDate.now();
        LocalDate tomorrow = now.plusDays(1);
        logger.trace(String.format("fetch forecast for %s and dates %s, %s", city, now.toString(), tomorrow.toString()));
        String todayKey = now.toString() + city;
        String tomorrowKey = tomorrow.toString() + city;

        ForecastEntity todayForecast = cache.getIfPresent(todayKey);
        ForecastEntity tomorrowForecast = cache.getIfPresent(tomorrowKey);

        List<ForecastPresentationDTO> dtos = new ArrayList<>();

        if (todayForecast != null){
            dtos.add(ForecastPresentationDTO.fromEntity(todayForecast));
        }else{
            checkFromDbAndAddToCacheAndDtos(city, now, todayKey, dtos);
        }

        logger.trace(String.format("forecast %s found in cache for %s and date %s", todayForecast != null ? "" : "not", city, now.toString()));

        if (tomorrowForecast != null){
            dtos.add(ForecastPresentationDTO.fromEntity(tomorrowForecast));
        }else{
            checkFromDbAndAddToCacheAndDtos(city, tomorrow, tomorrowKey, dtos);
        }

        logger.trace(String.format("forecast %s found in cache for %s and date %s", tomorrowForecast != null ? "" : "not", city, tomorrow.toString()));

        return dtos;
    }

    @Override
    public List<ForecastPresentationDTO> fetchAllForecasts(LocalDate date, Pageable pageable) {

        LocalDate nowAsLocalDate = date != null ? date : LocalDate.now();
        Date now = Date.valueOf(nowAsLocalDate);
        Date tomorrow = Date.valueOf(nowAsLocalDate.plusDays(1));

        logger.trace(String.format("fetch all saved cities for date %s and %s", now.toString(), tomorrow.toString()));

        List<ForecastEntity> forecasts = forecastDAO.findByDateInOrderByNameAscDateAsc(List.of(now, tomorrow), pageable);

        checkAndAddToCache(forecasts);

        return forecasts
                .stream()
                .map(f->ForecastPresentationDTO.fromEntity(f))
                .collect(Collectors.toList());

    }

    private SaveStatus checkAndAddToCache(List<ForecastEntity> forecasts) {
        SaveStatus status = SaveStatus.SKIPPED;
        for (ForecastEntity f : forecasts) {
            String key = f.getDate().toString() + f.getName();
            if (cache.getIfPresent(key) == null) {
                status = SaveStatus.SAVED;
            }
            cache.addIfNotPresent(key, f);
        }
        return status;
    }

    private void checkFromDbAndAddToCacheAndDtos(String city, LocalDate localDate, String key,Collection<ForecastPresentationDTO> dtos) {
        ForecastEntity forecast = forecastDAO.findByNameAndDate(city, Date.valueOf(localDate));
        if (forecast != null) {
            cache.addIfNotPresent(key, forecast);
            dtos.add(ForecastPresentationDTO.fromEntity(forecast));
        }
    }
}

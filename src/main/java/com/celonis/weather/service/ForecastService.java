package com.celonis.weather.service;

import com.celonis.weather.cache.ICaffeineCache;
import com.celonis.weather.dto.ForecastPresentationDTO;
import com.celonis.weather.dto.WeatherApiDTO;
import com.celonis.weather.model.forecast.ForecastEntity;
import com.celonis.weather.repository.IForecastDAO;
import com.celonis.weather.service.exception.ForecastLocationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


@Service
public class ForecastService implements IForecastService {

    private final ICaffeineCache cache;
    private final static Logger logger = LoggerFactory.getLogger(ForecastService.class);
    private IForecastDAO forecastDAO;
    @Value("${apiKey}")
    private String apiKey;
    @Value("${weatherApi}")
    private String weatherApi;

    public ForecastService(IForecastDAO forecastDAO, ICaffeineCache caffeineCache){
        this.forecastDAO = forecastDAO;
        this.cache = caffeineCache;
    }

    @Override
    public SaveStatus saveCityForecast(String city) {

        LocalDate now = LocalDate.now();
        String todayKey = now.toString() + city;
        String tomorrowKey = now.plusDays(1).toString() + city;

        if(cache.getIfPresent(todayKey) != null && cache.getIfPresent(tomorrowKey) != null){
            //both days are presents in cache
            return SaveStatus.SKIPPED;
        }

        RestTemplate restTemplate = new RestTemplate();
        WeatherApiDTO weather;
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(weatherApi)
                    .queryParam("q",city)
                    .queryParam("days", 2)
                    .queryParam("aqi","no")
                    .queryParam("alerts","no")
                    .queryParam("key", apiKey);

            ResponseEntity<WeatherApiDTO> response = restTemplate.getForEntity(builder.toUriString(), WeatherApiDTO.class);
            weather = response.getBody();
        }catch(RuntimeException exc){
            if(exc.getMessage().contains("No matching location found")){
                String errorMessage = String.format("city %s not found on wather api");
                logger.error(errorMessage, city);
                throw new ForecastLocationNotFoundException(errorMessage, exc);
            }
            logger.error(String.format("error on fetching weather for %s, error message: %s", city, exc.getMessage()));
            throw exc;
        }

        List<ForecastEntity> forecastEntities = weather.toEntity();
        SaveStatus status =  checkAndAddToCache(forecastEntities);
        forecastDAO.saveAll(forecastEntities);
        return status;
    }

    @Override
    public List<ForecastPresentationDTO> fetchCityForecast(String city) {
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plusDays(1);
        String todayKey = now.toString() + city;
        String tomorrowKey = tomorrow.toString() + city;

        ForecastEntity todayWeather = cache.getIfPresent(todayKey);
        ForecastEntity tomorrowWeather = cache.getIfPresent(tomorrowKey);

        List<ForecastPresentationDTO> dtos = new ArrayList<>();

        checkFromDbIfNullAndAddToCacheAndDtos(city, now, todayKey, todayWeather, dtos);

        checkFromDbIfNullAndAddToCacheAndDtos(city, tomorrow, todayKey, tomorrowWeather, dtos);

        return dtos;
    }

    @Override
    public Set<ForecastPresentationDTO> fetchAllForecasts() {
        Set<String> keys = cache.getAllKeys();

        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plusDays(1);
        String todayAsString = now.toString();
        String tomorrowAsString = tomorrow.toString();

        SortedSet<ForecastPresentationDTO> forecasts = new TreeSet<>();

        for(String key : keys){

            String cityName = key.substring(10);
            String todayKey = todayAsString+cityName;
            String tomorrowKey = tomorrowAsString+cityName;

            ForecastEntity todayForecast = cache.getIfPresent(todayKey);
            ForecastEntity tomorrowForecast = cache.getIfPresent(tomorrowKey);

            checkFromDbIfNullAndAddToCacheAndDtos(cityName, now, todayKey, todayForecast, forecasts);

            checkFromDbIfNullAndAddToCacheAndDtos(cityName, tomorrow, tomorrowKey, tomorrowForecast, forecasts);
        }

        return forecasts;
    }

    private SaveStatus checkAndAddToCache(List<ForecastEntity> forecasts) {
        SaveStatus status = SaveStatus.SAVED;
        for (ForecastEntity f : forecasts) {
            String key = f.getDate().toString() + f.getName();
            //the get operation insert only if the key is not present
            if (cache.getIfPresent(key) != null) {
                status = SaveStatus.SAVED;
            }
            cache.addIfNotPresent(key, f);
        }
        return status;
    }

    private void checkFromDbIfNullAndAddToCacheAndDtos(String city, LocalDate now, String todayKey, ForecastEntity todayWeather, Collection<ForecastPresentationDTO> dtos) {
        if(todayWeather != null){
            dtos.add(ForecastPresentationDTO.fromEntity(todayWeather));
        }else{
            ForecastEntity forecast = forecastDAO.findByNameAndDate(city, Date.valueOf(now));
            if (forecast != null) {
                cache.addIfNotPresent(todayKey, forecast);
                dtos.add(ForecastPresentationDTO.fromEntity(forecast));
            }
        }
    }
}

package com.celonis.weather.service;

import com.celonis.weather.dto.ForecastPresentationDTO;
import com.celonis.weather.dto.WeatherApiDTO;
import com.celonis.weather.model.forecast.ForecastEntity;
import com.celonis.weather.repository.IWeatherDAO;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


@Service
public class WeatherService implements WeatherServiceInterface{

    private Cache<String, ForecastEntity> cache;
    private final static Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private IWeatherDAO weatherDAO;
    @Value("${apiKey}")
    private String apiKey;
    @Value("${weatherApi}")
    private String weatherApi;

    public WeatherService(IWeatherDAO weatherDAO){
        this.weatherDAO = weatherDAO;

        cache = Caffeine.newBuilder()
                //should be for each key
                .expireAfterWrite(Duration.ofDays(2))
                .maximumSize(100)
                .build();
    }

    @Override
    public SaveStatus saveCityWeather(String city) {

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
        }catch(Exception exc){
            logger.error(String.format("error on fetching weather for %s, error message: %s", city, exc.getMessage()));
            throw exc;
        }

        List<ForecastEntity> weatherEntities = weather.toEntity();
        return checkAndAddToCache(weatherEntities);
    }

    @Override
    public List<ForecastPresentationDTO> fetchCityWeather(String city) {
        LocalDate now = LocalDate.now();
        String todayKey = now.toString() + city;
        String tomorrowKey = now.plusDays(1).toString() + city;

        ForecastEntity todayWeather = cache.getIfPresent(todayKey);
        ForecastEntity tomorrowWeather = cache.getIfPresent(tomorrowKey);

        List<ForecastPresentationDTO> dtos = new ArrayList<>();
        if(todayWeather != null){
            dtos.add(ForecastPresentationDTO.fromEntity(todayWeather));
        }
        if(tomorrowWeather != null){
            dtos.add(ForecastPresentationDTO.fromEntity(tomorrowWeather));
        }
        return dtos;
    }

    @Override
    public List<ForecastPresentationDTO> fetchAll() {
        Set<String> keys = cache.asMap().keySet();
        LocalDate now = LocalDate.now();
        String today = now.toString();
        String tomorrow = now.plusDays(1).toString();
        List<ForecastPresentationDTO> weathers = new ArrayList<>();
        for(String key : keys){

            String cityName = key.substring(10);
            String todayKey = today+cityName;
            String tomorrowKey = tomorrow+cityName;

            ForecastEntity todayWeather = cache.getIfPresent(todayKey);
            ForecastEntity tomorrowWeather = cache.getIfPresent(tomorrowKey);

            if(todayWeather != null){
                weathers.add(ForecastPresentationDTO.fromEntity(todayWeather));
            }
            if(tomorrowWeather != null){
                weathers.add(ForecastPresentationDTO.fromEntity(tomorrowWeather));
            }
        }

        weathers.sort(Comparator.comparing(k->k.getName()));
        return weathers;
    }

    private SaveStatus checkAndAddToCache(List<ForecastEntity> weathers) {
        SaveStatus status = SaveStatus.SAVED;
        for (ForecastEntity f : weathers) {
            String key = f.getDate().toString() + f.getName();
            //the get operation insert only if the key is not present
            if (cache.getIfPresent(key) != null) {
                status = SaveStatus.NEXT_DAY;
            }
            cache.get(key, (k) -> f);
        }
        return status;
    }

}

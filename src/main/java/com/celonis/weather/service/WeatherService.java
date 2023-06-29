package com.celonis.weather.service;

import com.celonis.weather.model.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class WeatherService implements WeatherServiceInterface{
    private final static Logger logger = LoggerFactory.getLogger(WeatherService.class);
    @Value("${apiKey}")
    private String apiKey;
    @Value("${weatherApi}")
    private String weatherApi;

    @Override
    public void fetchCityWeather(String city) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(weatherApi)
                    .queryParam("q",city)
                    .queryParam("days", 2)
                    .queryParam("aqi","no")
                    .queryParam("alerts","no")
                    .queryParam("key", apiKey);

            ResponseEntity<Weather> response = restTemplate.getForEntity(builder.toUriString(), Weather.class);
            //TODO use elasticsearch (...) or maybe just a static map to create an in-memory database/cache
        }catch(Exception exc){
            logger.error(String.format("error on fetching weather for %s, error message: %s", city, exc.getMessage()));
            throw exc;
        }
    }
}

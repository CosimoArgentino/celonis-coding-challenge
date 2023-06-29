package com.celonis.weather.service;

import com.celonis.weather.model.forecast.Day;
import com.celonis.weather.model.forecast.Forecastday;
import com.celonis.weather.model.forecast.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class WeatherService implements WeatherServiceInterface{

    private static Map<String, Map<Date, Day>> cache = new HashMap<>();
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
            Weather weather = response.getBody();

            checkAndAddToCache(weather);

            //TODO use elasticsearch (...) or maybe just a static map to create an in-memory database/cache
        }catch(Exception exc){
            logger.error(String.format("error on fetching weather for %s, error message: %s", city, exc.getMessage()));
            throw exc;
        }
    }


    private void checkAndAddToCache(Weather weather){
        String city = weather.getLocation().getCountry();
        List<Forecastday> forecast = weather.getForecast().getForecastday();
        if (cache.containsKey(city)){
            Map<Date, Day> days = cache.get(city);
            for(Forecastday f : forecast){
                if (days.containsKey(f.getDate())){
                    continue;
                }
                days.put(f.getDate(), f.getDay());
            }
            return;
        }

        Map<Date, Day> m = new HashMap<>();
        forecast.forEach(f -> {
            m.put(f.getDate(), f.getDay());
        });
        cache.put(city, m);
    }
}

package com.celonis.weather.service;

import com.celonis.weather.model.Weather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService implements WeatherServiceInterface{

    @Value("${apiKey}")
    private String apiKey;
    @Value("${weatherApi}")
    private String weatherApi;

    @Override
    public Weather fetchCityWeather(String city) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(weatherApi)
                    .queryParam("q",city)
                    .queryParam("days", 2)
                    .queryParam("aqi","no")
                    .queryParam("alerts","no")
                    .queryParam("key", apiKey);

            ResponseEntity<Weather> response = restTemplate.getForEntity(builder.toUriString(), Weather.class);
            return response.getBody();
        }catch(Exception exc){
            System.out.println(exc.getMessage());
            throw exc;
        }
    }
}

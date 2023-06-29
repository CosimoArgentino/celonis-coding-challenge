package com.celonis.weather.controller;

import com.celonis.weather.model.Weather;
import com.celonis.weather.service.WeatherServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather/forecast")
public class WeatherApi {

    private WeatherServiceInterface weatherService;

    public WeatherApi (WeatherServiceInterface weatherService){
        this.weatherService = weatherService;
    }

    @PostMapping("/save/{city}")
    public ResponseEntity<String> saveCityForecast(@PathVariable String city){
        return null;
    }

    @GetMapping("/retrieve/{city}")
    @ResponseBody public ResponseEntity<Weather> getForecastForCity(@PathVariable String city){
        return null;
    }

}

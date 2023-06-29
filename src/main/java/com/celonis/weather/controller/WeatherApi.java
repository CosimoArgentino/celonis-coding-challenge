package com.celonis.weather.controller;

import com.celonis.weather.model.forecast.Weather;
import com.celonis.weather.service.WeatherServiceInterface;
import org.springframework.http.HttpStatus;
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
        //TODO all responses from weather api documentation
        try{
            weatherService.fetchCityWeather(city);
            return new ResponseEntity<>(String.format("%s weather saved", city), HttpStatus.OK);
        }catch (Exception exc){
            if (exc.getMessage().contains("No matching location found")) {
                return new ResponseEntity<>(String.format("%s not found", city), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetch/{city}")
    @ResponseBody public ResponseEntity<Weather> getForecastForCity(@PathVariable String city){
        return null;
    }

    @GetMapping("/fetch")
    @ResponseBody public ResponseEntity<Weather> getAllForecast(@PathVariable String city){
        return null;
    }

}

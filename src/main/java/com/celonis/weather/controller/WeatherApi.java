package com.celonis.weather.controller;

import com.celonis.weather.dto.ForecastPresentationDTO;
import com.celonis.weather.dto.WeatherApiDTO;
import com.celonis.weather.service.SaveStatus;
import com.celonis.weather.service.WeatherServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            SaveStatus status = weatherService.saveCityWeather(city);
            return new ResponseEntity<>(String.format("%s weather saved%s", city, status == SaveStatus.NEXT_DAY ?
                    ", only tomorrow saved" : ""), HttpStatus.OK);
        }catch (Exception exc){
            if (exc.getMessage().contains("No matching location found")) {
                return new ResponseEntity<>(String.format("%s not found", city), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetch/{city}")
    @ResponseBody public ResponseEntity<List<ForecastPresentationDTO>> getForecastForCity(@PathVariable String city){
        List<ForecastPresentationDTO> forecasts = weatherService.fetchCityWeather(city);
        if (forecasts.size() == 0){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(forecasts, HttpStatus.OK);
    }

    @GetMapping("/fetch")
    @ResponseBody public ResponseEntity<List<ForecastPresentationDTO>> getAllForecast(){
        List<ForecastPresentationDTO> forecasts = weatherService.fetchAll();
        if (forecasts.size() == 0){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(forecasts, HttpStatus.OK);
    }

}

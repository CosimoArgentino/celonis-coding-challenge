package com.celonis.weather.controller;

import com.celonis.weather.dto.ForecastPresentationDTO;
import com.celonis.weather.service.SaveStatus;
import com.celonis.weather.service.IForecastService;
import com.celonis.weather.service.exception.ForecastLocationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/weather/forecast")
public class ForecastApiController {

    private IForecastService weatherService;

    public ForecastApiController(IForecastService weatherService){
        this.weatherService = weatherService;
    }

    @PostMapping("/save/{city}")
    public ResponseEntity<String> saveCityForecast(@PathVariable String city){
        //TODO all responses from weather api documentation
        try{
            SaveStatus status = weatherService.saveCityForecast(city);
            return new ResponseEntity<>(String.format("%s weather %s", status.name()), HttpStatus.OK);
        }catch (ForecastLocationNotFoundException exc){
            return new ResponseEntity<>(String.format("%s not found", city), HttpStatus.NOT_FOUND);
        }catch (RuntimeException exc){
            return new ResponseEntity<>(String.format("error: %s", exc.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetch/{city}")
    @ResponseBody public ResponseEntity<List<ForecastPresentationDTO>> getForecastForCity(@PathVariable String city){
        List<ForecastPresentationDTO> forecasts = weatherService.fetchCityForecast(city);
        if (forecasts.size() == 0){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(forecasts, HttpStatus.OK);
    }

    @GetMapping("/fetch")
    @ResponseBody public ResponseEntity<Set<ForecastPresentationDTO>> getAllForecast(){
        Set<ForecastPresentationDTO> forecasts = weatherService.fetchAllForecasts();
        if (forecasts.size() == 0){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(forecasts, HttpStatus.OK);
    }
}

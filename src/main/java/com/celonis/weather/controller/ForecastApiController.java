package com.celonis.weather.controller;

import com.celonis.weather.dto.ForecastPresentationDTO;
import com.celonis.weather.service.SaveStatus;
import com.celonis.weather.service.IForecastService;
import com.celonis.weather.service.exception.ForecastLocationNotFoundException;
import com.celonis.weather.service.exception.WeatherApiException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/weather/forecast")
public class ForecastApiController {

    private IForecastService forecastService;

    public ForecastApiController(IForecastService forecastService){
        this.forecastService = forecastService;
    }

    @PostMapping("/save/{city}")
    public ResponseEntity<String> saveCityForecast(@PathVariable String city,
                                                   @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        try{
            SaveStatus status = forecastService.saveCityForecast(city, date);
            return new ResponseEntity<>(String.format("%s weather %s", city, status.name()), HttpStatus.CREATED);
        }catch (ForecastLocationNotFoundException exc){
            return new ResponseEntity<>(String.format("%s not valid", city), HttpStatus.BAD_REQUEST);
        }catch (WeatherApiException exc){
            return new ResponseEntity<>("error on weather api %s", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetch/{city}")
    @ResponseBody public ResponseEntity<List<ForecastPresentationDTO>> getForecastForCity(@PathVariable String city,
                                                                                          @RequestParam(value = "date", required = false)
                                                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        List<ForecastPresentationDTO> forecasts = forecastService.fetchCityForecast(city, date);
        if (forecasts.size() == 0){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(forecasts, HttpStatus.OK);
    }

    @GetMapping("/fetch")
    @ResponseBody public ResponseEntity<List<ForecastPresentationDTO>> getAllForecast(@RequestParam(value = "date", required = false)
                                                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        List<ForecastPresentationDTO> forecasts = forecastService.fetchAllForecasts(date);
        if (forecasts.size() == 0){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(forecasts, HttpStatus.OK);
    }
}

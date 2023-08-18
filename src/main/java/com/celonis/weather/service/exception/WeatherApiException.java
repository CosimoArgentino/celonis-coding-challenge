package com.celonis.weather.service.exception;

public class WeatherApiException extends RuntimeException{

    public WeatherApiException(String message, Throwable err){
        super(message, err);
    }
}

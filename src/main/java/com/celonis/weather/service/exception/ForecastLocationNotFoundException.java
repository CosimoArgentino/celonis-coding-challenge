package com.celonis.weather.service.exception;

public class ForecastLocationNotFoundException extends RuntimeException{
    public ForecastLocationNotFoundException(String message, Throwable err){
        super(message, err);
    }
}

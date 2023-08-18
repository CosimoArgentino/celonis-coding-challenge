package com.celonis.weather.dto;

import com.celonis.weather.model.forecast.ForecastEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiDTO {
    private Location location;
    private Forecast forecast;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public List<ForecastEntity> toEntity(){

        List<ForecastEntity> forecasts = new ArrayList<>(2);

        forecast.getForecastday().forEach(f -> {
            Day day = f.getDay();

            ForecastEntity forecastEntity = new ForecastEntity(this.location.getName(), Date.valueOf(f.getDate()),
                    day.getMaxtemp_c(), day.getMintemp_c(), day.getTotalprecip_mm(), day.getTotalsnow_cm(),
                    day.getAvghumidity(), day.getCondition().getText());

            forecasts.add(forecastEntity);
        });

        return forecasts;
    }
}

class Location {
    private String name;
    private String region;
    private String country;
    private int localtime_epoch;
    private String localtime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getLocaltime_epoch() {
        return localtime_epoch;
    }

    public void setLocaltime_epoch(int localtime_epoch) {
        this.localtime_epoch = localtime_epoch;
    }

    public String getLocaltime() {
        return localtime;
    }

    public void setLocaltime(String localtime) {
        this.localtime = localtime;
    }
}

class Forecastday {

    private LocalDate date;
    private int date_epoch;
    private Day day;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getDate_epoch() {
        return date_epoch;
    }

    public void setDate_epoch(int date_epoch) {
        this.date_epoch = date_epoch;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }
}

class Forecast {
    private List<Forecastday> forecastday;

    public List<Forecastday> getForecastday() {
        return forecastday;
    }

    public void setForecastday(List<Forecastday> forecastday) {
        this.forecastday = forecastday;
    }
}

class Day {
    private double maxtemp_c;
    private double mintemp_c;
    private double totalprecip_mm;
    private double totalsnow_cm;
    private double avghumidity;
    private Condition condition;

    public double getMaxtemp_c() {
        return maxtemp_c;
    }

    public void setMaxtemp_c(double maxtemp_c) {
        this.maxtemp_c = maxtemp_c;
    }

    public double getMintemp_c() {
        return mintemp_c;
    }

    public void setMintemp_c(double mintemp_c) {
        this.mintemp_c = mintemp_c;
    }

    public double getTotalprecip_mm() {
        return totalprecip_mm;
    }

    public void setTotalprecip_mm(double totalprecip_mm) {
        this.totalprecip_mm = totalprecip_mm;
    }

    public double getTotalsnow_cm() {
        return totalsnow_cm;
    }

    public void setTotalsnow_cm(double totalsnow_cm) {
        this.totalsnow_cm = totalsnow_cm;
    }

    public double getAvghumidity() {
        return avghumidity;
    }

    public void setAvghumidity(double avghumidity) {
        this.avghumidity = avghumidity;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}

class Condition {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
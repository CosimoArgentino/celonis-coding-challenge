package com.celonis.weather.dto;

import com.celonis.weather.model.forecast.ForecastEntity;

import java.sql.Date;

public class ForecastPresentationDTO implements Comparable<ForecastPresentationDTO>{
    private String name;
    private Date date;
    private double maxTempC;
    private double minTempC;
    private double totalPrecipMM;
    private double totalSnowMM;
    private double avgHumidity;
    private String condition;

    public ForecastPresentationDTO(String name, Date date, double maxTempC, double minTempC, double totalPrecipMM,
                                   double totalSnowMM, double avgHumidity, String condition) {
        this.name = name;
        this.date = date;
        this.maxTempC = maxTempC;
        this.minTempC = minTempC;
        this.totalPrecipMM = totalPrecipMM;
        this.totalSnowMM = totalSnowMM;
        this.avgHumidity = avgHumidity;
        this.condition = condition;
    }

    public ForecastPresentationDTO(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getMaxTempC() {
        return maxTempC;
    }

    public void setMaxTempC(double maxTempC) {
        this.maxTempC = maxTempC;
    }

    public double getMinTempC() {
        return minTempC;
    }

    public void setMinTempC(double minTempC) {
        this.minTempC = minTempC;
    }

    public double getTotalPrecipMM() {
        return totalPrecipMM;
    }

    public void setTotalPrecipMM(double totalPrecipMM) {
        this.totalPrecipMM = totalPrecipMM;
    }

    public double getTotalSnowMM() {
        return totalSnowMM;
    }

    public void setTotalSnowMM(double totalSnowMM) {
        this.totalSnowMM = totalSnowMM;
    }

    public double getAvgHumidity() {
        return avgHumidity;
    }

    public void setAvgHumidity(double avgHumidity) {
        this.avgHumidity = avgHumidity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public static ForecastPresentationDTO fromEntity(ForecastEntity entity){
        return new ForecastPresentationDTO(entity.getName(), entity.getDate(), entity.getMaxTempC(), entity.getMinTempC(),
        entity.getTotalPrecipMM(), entity.getTotalSnowMM(), entity.getAvgHumidity(), entity.getCondition());
    }

    @Override
    public int compareTo(ForecastPresentationDTO o) {
        String thisKey = this.name + this.date.toString();
        String compareKey = o.getName() + o.getDate().toString();
        return thisKey.compareTo(compareKey);
    }
}

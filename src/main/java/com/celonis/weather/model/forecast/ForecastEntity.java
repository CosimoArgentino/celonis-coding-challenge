package com.celonis.weather.model.forecast;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "forecast", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "date"})})
public class ForecastEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;
    private Date date;
    private double maxTempC;
    private double minTempC;
    private double totalPrecipMM;
    private double totalSnowMM;
    private double avgHumidity;
    private String condition;

    public ForecastEntity(){

    }

    public ForecastEntity(String name, Date date, double maxTempC, double minTempC, double totalPrecipMM,
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}

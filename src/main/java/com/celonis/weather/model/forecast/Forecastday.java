package com.celonis.weather.model.forecast;

import java.util.Date;

public class Forecastday {

    private Date date;
    private int date_epoch;
    private Day day;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

package com.celonis.weather.repository;

import com.celonis.weather.model.forecast.ForecastEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWeatherDAO extends JpaRepository<ForecastEntity, Long> {

}

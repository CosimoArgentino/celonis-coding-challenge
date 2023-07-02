package com.celonis.weather.repository;

import com.celonis.weather.model.forecast.ForecastEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface IForecastDAO extends JpaRepository<ForecastEntity, Long> {
    ForecastEntity findByNameAndDate(String name, Date date);
}

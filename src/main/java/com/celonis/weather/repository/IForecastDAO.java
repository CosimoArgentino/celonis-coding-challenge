package com.celonis.weather.repository;

import com.celonis.weather.model.forecast.ForecastEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface IForecastDAO extends JpaRepository<ForecastEntity, Long> {
    ForecastEntity findByNameAndDate(String name, Date date);
    Long deleteByName(String name);
    List<ForecastEntity> findByDateInOrderByNameAscDateAsc(List<Date> dates);
}

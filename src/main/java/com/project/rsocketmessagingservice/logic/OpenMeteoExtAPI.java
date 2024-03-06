package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.Location;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.LocationBoundary;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface OpenMeteoExtAPI {

    public Flux<Map<String, Object>> getWeeklyForecast(int days, LocationBoundary location);

    public Flux<Map<String, Object>> getDailyRecommendation(LocationBoundary location);
}

package com.project.rsocketmessagingservice.logic.openMeteo;

import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.LocationBoundary;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface OpenMeteoExtAPI {

    public Flux<Map<String, Object>> getWeeklyForecast(int days, LocationBoundary location);

    public Flux<Map<String, Object>> getDailyRecommendation(LocationBoundary location, int hours);
}

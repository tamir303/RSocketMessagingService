package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.Location;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OpenMeteoExtAPI {

    public Flux<Map<String, Object>> getWeeklyForecast(int days, Location location);

    public Flux<Map<String, Object>> getDailyRecommendation(Location location);
}

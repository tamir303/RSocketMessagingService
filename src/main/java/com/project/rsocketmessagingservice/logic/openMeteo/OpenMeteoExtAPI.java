package com.project.rsocketmessagingservice.logic.openMeteo;

import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.LocationBoundary;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Interface for accessing the OpenMeteo external API.
 */
public interface OpenMeteoExtAPI {

    /**
     * Retrieves the weekly weather forecast for a specific location.
     * @param days The number of days for the forecast.
     * @param location The location for which the forecast is requested.
     * @return A Flux emitting maps containing weather forecast information.
     */
    Flux<Map<String, Object>> getWeeklyForecast(int days, LocationBoundary location);

    /**
     * Retrieves the daily weather recommendation for a specific location.
     * @param location The location for which the recommendation is requested.
     * @param hours The number of hours for which the recommendation is needed.
     * @return A Flux emitting maps containing weather recommendation information.
     */
    Flux<Map<String, Object>> getDailyRecommendation(LocationBoundary location, int hours);
}

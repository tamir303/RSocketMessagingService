package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.LocationBoundary;
import com.project.rsocketmessagingservice.logic.openMeteo.OpenMeteoExtAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Controller class to handle OpenMeteo API operations.
 */
@RestController
@RequestMapping(path = "/openmeteo")
public class ClientOpenMeteoController {

    private final OpenMeteoExtAPI openMeteoService;

    /**
     * Constructs a ClientOpenMeteoController with the provided OpenMeteoExtAPI instance.
     * @param openMeteoService The OpenMeteoExtAPI instance.
     */
    @Autowired
    public ClientOpenMeteoController(OpenMeteoExtAPI openMeteoService) {
        this.openMeteoService = openMeteoService;
    }

    /**
     * Retrieves weekly forecast from OpenMeteo API.
     * @param days The number of days for the forecast.
     * @param latitude The latitude coordinate for the location.
     * @param longitude The longitude coordinate for the location.
     * @return A Flux emitting the weekly forecast data.
     */
    @GetMapping(
            path = {"/weekly-forecast"},
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<Map<String, Object>> getWeeklyForecast(@RequestParam int days,
                                                       @RequestParam double latitude,
                                                       @RequestParam double longitude) {
        return openMeteoService.getWeeklyForecast(days, new LocationBoundary(latitude, longitude));
    }

    /**
     * Retrieves daily recommendation from OpenMeteo API.
     * @param latitude The latitude coordinate for the location.
     * @param longitude The longitude coordinate for the location.
     * @param hours The number of hours for the recommendation.
     * @return A Flux emitting the daily recommendation data.
     */
    @GetMapping(
            path = {"/daily-recommendation"},
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<Map<String, Object>> getDailyRecommendation(@RequestParam double latitude,
                                                            @RequestParam double longitude,
                                                            @RequestParam int hours) {
        return openMeteoService.getDailyRecommendation(new LocationBoundary(latitude, longitude), hours);
    }
}

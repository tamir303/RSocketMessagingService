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

@RestController
@RequestMapping(path = "/openmeteo")
public class ClientOpenMeteoController {

    private final OpenMeteoExtAPI openMeteoService;

    @Autowired
    public ClientOpenMeteoController(OpenMeteoExtAPI openMeteoService) {
        this.openMeteoService = openMeteoService;
    }

    @GetMapping(
            path = {"/weekly-forecast"},
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<Map<String, Object>> getWeeklyForecast(@RequestParam int days,
                                                       @RequestParam double latitude,
                                                       @RequestParam double longitude) {
        return openMeteoService.getWeeklyForecast(days, new LocationBoundary(latitude, longitude));
    }

    @GetMapping(
            path = {"/daily-recommendation"},
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<Map<String, Object>> getDailyRecommendation(@RequestParam double latitude,
                                                            @RequestParam double longitude,
                                                            @RequestParam int hours) {
        return openMeteoService.getDailyRecommendation(new LocationBoundary(latitude, longitude), hours);
    }
}

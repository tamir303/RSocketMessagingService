package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.logic.OpenMeteoExtAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping(path = "/openmeteo")
public class OpenMeteoController {

    private final OpenMeteoExtAPI openMeteoService;

    @Autowired
    public OpenMeteoController(OpenMeteoExtAPI openMeteoService) {
        this.openMeteoService = openMeteoService;
    }

    @GetMapping(path = "/weekly-forecast/{days}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Map<String, Object>> getWeeklyForecast(@PathVariable int days) {
        return openMeteoService.getWeeklyForecast(days);
    }

    @GetMapping(path = "/daily-recommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Map<String, Object>> getDailyRecommendation() {
        return openMeteoService.getDailyRecommendation();
    }
}

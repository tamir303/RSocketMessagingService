package com.project.rsocketmessagingservice.logic;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OpenMeteoExtAPI {

    public Flux<Map<String, Object>> getWeeklyForecast(int days);

    public Flux<Map<String, Object>> getDailyRecommendation();
}

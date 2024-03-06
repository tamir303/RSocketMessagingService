package com.project.rsocketmessagingservice.logic;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OpenMeteoExtAPI {

    public Flux<Object> getWeeklyForecast(int days);

    public Mono<Object> getDailyRecommendation();
}

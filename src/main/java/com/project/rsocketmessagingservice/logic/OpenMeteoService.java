package com.project.rsocketmessagingservice.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenMeteoService implements OpenMeteoExtAPI {
    private final WebClient webClient;

    @Value("${openmeteo.api.baseForecastUrl}")
    private String openMeteoApiUrl;

    @Override
    public Flux<Object> getWeeklyForecast(int days) {
        if (days < 1 || days > 16) {
            return Flux.error(new IllegalArgumentException("Number of days must be between 1 and 16"));
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(openMeteoApiUrl)
                        .queryParam("forecast_days", days)
                        .build())
                .retrieve()
                .bodyToFlux(Object.class);
    }

    @Override
    public Mono<Object> getDailyRecommendation() {
        // Implement this method if needed
        return null;
    }
}

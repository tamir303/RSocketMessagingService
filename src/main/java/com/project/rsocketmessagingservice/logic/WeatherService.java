package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.MessageBoundaries.MessageBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WeatherService {
    Mono<MessageBoundary> attachNewWeatherMachineEvent(MessageBoundary data);
    Mono<Void> removeWeatherMachineEvent(MessageBoundary data);
    Mono<Void> updateWeatherMachineEvent(MessageBoundary data);
    Flux<MessageBoundary> getAllWeatherMachines(String houseUUID);
    Flux<MessageBoundary> getWeatherForecast(MessageBoundary data);
    Mono<MessageBoundary> getWeatherRecommendations(MessageBoundary data);
    Mono<Void> changeMachineState(MessageBoundary data);
}

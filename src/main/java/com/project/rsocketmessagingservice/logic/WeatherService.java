package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WeatherService {
    Mono<MessageBoundary> attachNewWeatherMachineEvent(NewMessageBoundary message);
    Mono<Void> removeWeatherMachineEvent(String machineUUID);
    Mono<Void> updateWeatherMachineEvent(MessageBoundary message);
    Flux<MessageBoundary> getAllWeatherMachines();
    Flux<MessageBoundary> getWeatherForecast(MessageBoundary message);
    Mono<MessageBoundary> getWeatherRecommendations();
}

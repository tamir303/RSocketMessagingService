package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WeatherService {
    Mono<MessageBoundary> attachNewWeatherMachineEvent(NewMessageBoundary message);
    Mono<Void> removeWeatherMachineEvent(MessageBoundary message);
    Mono<Void> updateWeatherMachineEvent(MessageBoundary message);
    Flux<MessageBoundary> getAllWeatherMachines();
    Flux<MessageBoundary> getWeatherForecast(String houseUUID, Integer days);
    Mono<MessageBoundary> getWeatherRecommendations(MessageBoundary data);
    Mono<Void> changeMachineState(MessageBoundary data);
}

package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.MessageBoundaries.MessageBoundary;
import com.project.rsocketmessagingservice.logic.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    @MessageMapping("attach-new-weather-machine")
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(Mono<MessageBoundary> data) {
        return data.flatMap(weatherService::attachNewWeatherMachineEvent);
    }

    @MessageMapping("remove-weather-machine")
    public Mono<Void> removeWeatherMachineEvent(Mono<MessageBoundary> data) {
        return data.doOnNext(weatherService::removeWeatherMachineEvent).then();
    }

    @MessageMapping("update-weather-machine")
    public Mono<Void> updateWeatherMachineEvent(Mono<MessageBoundary> data) {
        return data.doOnNext(weatherService::updateWeatherMachineEvent).then();
    }

    @MessageMapping("get-all-weather-machines")
    public Flux<MessageBoundary> getAllWeatherMachines(Mono<String> houseUUID) {
        return houseUUID.flatMapMany(weatherService::getAllWeatherMachines);
    }

    @MessageMapping("get-weather-forecast")
    public Flux<MessageBoundary> getWeatherForecast(Mono<MessageBoundary> data) {
        return weatherService.getWeatherForecast(data.block());
    }
    @MessageMapping("get-weather-recommendations")
    public Mono<Void> getWeatherRecommendations(Mono<MessageBoundary> data) {
        return data.doOnNext(weatherService::getWeatherRecommendations).then();
    }

    @MessageMapping("change-machine-state")
    public Mono<Void> changeMachineState(Mono<MessageBoundary> data) {
        return data.doOnNext(weatherService::changeMachineState).then();
    }

}

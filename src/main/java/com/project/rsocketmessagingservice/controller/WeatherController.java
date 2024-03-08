package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.logic.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    @MessageMapping("attach-new-weather-machine")
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(@Payload NewMessageBoundary message) {
        return weatherService.attachNewWeatherMachineEvent(message);
    }

    @MessageMapping("remove-weather-machine")
    public Mono<Void> removeWeatherMachineEvent(@Payload MessageBoundary message) {
        return weatherService.removeWeatherMachineEvent(message);
    }

    @MessageMapping("update-weather-machine")
    public Mono<Void> updateWeatherMachineEvent(@Payload MessageBoundary message) {
        return weatherService.updateWeatherMachineEvent(message);
    }

    @MessageMapping("get-all-weather-machines")
    public Flux<MessageBoundary> getAllWeatherMachines() {
        return weatherService.getAllWeatherMachines();
    }

    @MessageMapping("remove-all-weather-machines")
    public Mono<Void> removeAllWeatherMachines() {
        return weatherService.removeAllWeatherMachines();
    }

    @MessageMapping("get-weather-forecast")
    public Flux<MessageBoundary> getWeatherForecast(@RequestBody MessageBoundary message) {
        return weatherService.getWeatherForecast(message);
    }

    @MessageMapping("get-weather-recommendations")
    public Mono<MessageBoundary> getWeatherRecommendations() {
        return weatherService.createWeatherRecommendations();
    }
}

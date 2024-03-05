package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.logic.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    @MessageMapping("attach-new-weather-machine")
    public Mono<MessageBoundary> attachNewWeatherMachineEvent( @Payload NewMessageBoundary data) {
        log.debug("Invoking: attach-new-weather-machine");
        return weatherService.attachNewWeatherMachineEvent(data);
    }

    @MessageMapping("remove-weather-machine")
    public Mono<Void> removeWeatherMachineEvent( @Payload String machineUUID) {
        return weatherService.removeWeatherMachineEvent(machineUUID);
    }

    @MessageMapping("update-weather-machine")
    public Mono<Void> updateWeatherMachineEvent(Mono<MessageBoundary> data) {
        return data.doOnNext(weatherService::updateWeatherMachineEvent).then();
    }

    @MessageMapping("get-all-weather-machines")
    public Flux<MessageBoundary> getAllWeatherMachines() {
        return weatherService.getAllWeatherMachines();
    }

    @MessageMapping("get-weather-forecast")
    public Flux<MessageBoundary> getWeatherForecast(@RequestParam String houseUUID, @RequestParam Integer days) {
        return weatherService.getWeatherForecast(houseUUID,days);
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

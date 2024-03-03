package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.MessageBoundaries.MessageBoundary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/weather")
public class ClientWeatherController {
    private final RSocketRequester requester;

    public ClientWeatherController(RSocketRequester.Builder requesterBuilder,
                                   @Value("${demoapp.client.rsocket.host:127.0.0.1}") String rsocketHost,
                                   @Value("${demoapp.client.rsocket.port:7000}") int rsocketPort) {
        this.requester = requesterBuilder.tcp(rsocketHost, rsocketPort);
    }

    @MessageMapping("attach-new-weather-machine")
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(Mono<MessageBoundary> data) {
        return this.requester.route("attach-new-weather-machine")
                .data(data)
                .retrieveMono(MessageBoundary.class)
                .log();
    }

    @MessageMapping("remove-weather-machine")
    public Mono<Void> removeWeatherMachineEvent(Mono<MessageBoundary> data) {
        return this.requester.route("remove-weather-machine")
                .data(data)
                .send()
                .log();
    }

    @MessageMapping("update-weather-machine")
    public Mono<Void> updateWeatherMachineEvent(Mono<MessageBoundary> data) {
        return this.requester.route("update-weather-machine")
                .data(data)
                .send()
                .log();
    }

    @MessageMapping("get-all-weather-machines")
    public Flux<MessageBoundary> getAllWeatherMachines(Mono<String> houseUUID) {
        return this.requester.route("get-all-weather-machines")
                .data(houseUUID)
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    @MessageMapping("get-weather-forecast")
    public Flux<MessageBoundary> getWeatherForecast(Mono<MessageBoundary> data) {
        return this.requester.route("get-weather-forecast")
                .data(data)
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    @MessageMapping("get-weather-recommendations")
    public Mono<Void> getWeatherRecommendations(Mono<MessageBoundary> data) {
        return this.requester.route("get-weather-recommendations")
                .data(data)
                .send()
                .log();
    }

    @MessageMapping("change-machine-state")
    public Mono<Void> changeMachineState(Mono<MessageBoundary> data) {
        return this.requester.route("change-machine-state")
                .data(data)
                .send()
                .log();
    }
}

package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/rsocket/weather")
public class ClientWeatherController {
    private RSocketRequester requester;
    private RSocketRequester.Builder requesterBuilder;
    private String rsocketHost;
    private int rsocketPort;

    @Autowired
    public void setRequesterBuilder(RSocketRequester.Builder requesterBuilder) {
        this.requesterBuilder = requesterBuilder;
    }

    @Value("${demoapp.client.rsocket.host:127.0.0.1}")
    public void setRsocketHost(String rsocketHost) {
        this.rsocketHost = rsocketHost;
    }

    @Value("${demoapp.client.rsocket.port:7000}")
    public void setRsocketPort(int rsocketPort) {
        this.rsocketPort = rsocketPort;
    }

    @PostConstruct
    public void init() {
        this.requester = this.requesterBuilder
                .tcp(rsocketHost, rsocketPort);
    }

    @PostMapping(
            path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MessageBoundary> createWeatherMachine(@RequestBody MessageBoundary data) {
        return this.requester.route("attach-new-weather-machine")
                .data(Mono.just(data))
                .retrieveMono(MessageBoundary.class)
                .log();
    }

//    @DeleteMapping("/remove/{id}")
//    public Mono<Void> removeWeatherMachine(@PathVariable String id) {
//        return this.requester.route("remove-weather-machine")
//                .data(Mono.just(new MessageBoundary(id)))
//                .send()
//                .log();
//    }

    @PutMapping("/update")
    public Mono<Void> updateWeatherMachine(@RequestBody MessageBoundary data) {
        return this.requester.route("update-weather-machine")
                .data(Mono.just(data))
                .send()
                .log();
    }

    @GetMapping("/all")
    public Flux<MessageBoundary> getAllWeatherMachines(@RequestParam String houseUUID) {
        return this.requester.route("get-all-weather-machines")
                .data(Mono.just(houseUUID))
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    @GetMapping("/forecast")
    public Flux<MessageBoundary> getWeatherForecast(@RequestBody MessageBoundary data) {
        return this.requester.route("get-weather-forecast")
                .data(Mono.just(data))
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    @GetMapping("/recommendations")
    public Mono<Void> getWeatherRecommendations(@RequestBody MessageBoundary data) {
        return this.requester.route("get-weather-recommendations")
                .data(Mono.just(data))
                .send()
                .log();
    }

    @PutMapping("/state")
    public Mono<Void> changeMachineState(@RequestBody MessageBoundary data) {
        return this.requester.route("change-machine-state")
                .data(Mono.just(data))
                .send()
                .log();
    }
}

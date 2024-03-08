package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceIdBoundary;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<MessageBoundary> createWeatherMachine(@RequestBody NewMessageBoundary message) {
        return this.requester.route("attach-new-weather-machine")
                    .data(message)
                    .retrieveMono(MessageBoundary.class)
                    .log();
    }

    @DeleteMapping("/remove")
    public Mono<Void> removeWeatherMachine(@RequestBody MessageBoundary message) {
        return this.requester.route("remove-weather-machine")
                .data(message)
                .send()
                .log();
    }

    @DeleteMapping("/remove")
    public Mono<Void> removeAllWeatherMachines() {
        return this.requester.route("remove-all-weather-machines")
                .send()
                .log();
    }

    @PutMapping("/update")
    public Mono<Void> updateWeatherMachine(@RequestBody MessageBoundary message) {
        return this.requester.route("update-weather-machine")
                .data(message)
                .send()
                .log();
    }

    @GetMapping(
            path = {"/all"},
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<MessageBoundary> getAllWeatherMachines() {
        return this.requester.route("get-all-weather-machines")
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    @GetMapping(
            path = {"/device"},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<MessageBoundary> getWeatherMachineById(@RequestBody DeviceIdBoundary id) {
        return this.requester.route("get-weather-machine-by-id")
                .data(id)
                .retrieveMono(MessageBoundary.class)
                .log();
    }

    @PostMapping(
            path = "/forecast",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<MessageBoundary> getWeatherForecast(@RequestBody MessageBoundary message) {
        return this.requester.route("get-weather-forecast")
                .data(message)
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    @GetMapping("/recommendations")
    public Mono<MessageBoundary> getWeatherRecommendations() {
        return this.requester.route("get-weather-recommendations")
                .retrieveMono(MessageBoundary.class)
                .log();
    }
}

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

/**
 * Controller class to handle weather-related operations.
 */
@RestController
@RequestMapping(path = "/rsocket/weather")
public class ClientWeatherController {

    private RSocketRequester requester;
    private RSocketRequester.Builder requesterBuilder;
    private String rsocketHost;
    private int rsocketPort;

    /**
     * Sets the RSocket requester builder.
     * @param requesterBuilder The RSocket requester builder.
     */
    @Autowired
    public void setRequesterBuilder(RSocketRequester.Builder requesterBuilder) {
        this.requesterBuilder = requesterBuilder;
    }

    /**
     * Sets the RSocket server host.
     * @param rsocketHost The RSocket server host.
     */
    @Value("${demoapp.client.rsocket.host:127.0.0.1}")
    public void setRsocketHost(String rsocketHost) {
        this.rsocketHost = rsocketHost;
    }

    /**
     * Sets the RSocket server port.
     * @param rsocketPort The RSocket server port.
     */
    @Value("${demoapp.client.rsocket.port:7000}")
    public void setRsocketPort(int rsocketPort) {
        this.rsocketPort = rsocketPort;
    }

    /**
     * Initializes the RSocket requester upon bean construction.
     */
    @PostConstruct
    public void init() {
        this.requester = this.requesterBuilder
                .tcp(rsocketHost, rsocketPort);
    }

    /**
     * Creates a new weather machine.
     * @param message The message containing information about the new weather machine.
     * @return A Mono emitting the created weather machine message boundary.
     */
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

    /**
     * Removes a weather machine.
     * @param message The message containing information about the weather machine to remove.
     * @return A Mono indicating the completion of the removal operation.
     */
    @DeleteMapping(path = {"/remove"}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Void> removeWeatherMachine(@RequestBody MessageBoundary message) {
        return this.requester.route("remove-weather-machine")
                .data(message)
                .send()
                .log();
    }

    /**
     * Removes all weather machines.
     * @return A Mono indicating the completion of the removal operation.
     */
    @DeleteMapping("/remove/all")
    public Mono<Void> removeAllWeatherMachines() {
        return this.requester.route("remove-all-weather-machines")
                .send()
                .log();
    }

    /**
     * Updates a weather machine.
     * @param message The message containing information about the weather machine to update.
     * @return A Mono indicating the completion of the update operation.
     */
    @PutMapping("/update")
    public Mono<Void> updateWeatherMachine(@RequestBody MessageBoundary message) {
        return this.requester.route("update-weather-machine")
                .data(message)
                .send()
                .log();
    }

    /**
     * Retrieves all weather machines.
     * @return A Flux emitting all weather machine message boundaries.
     */
    @GetMapping(
            path = {"/all"},
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<MessageBoundary> getAllWeatherMachines() {
        return this.requester.route("get-all-weather-machines")
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    /**
     * Retrieves a weather machine by its ID.
     * @param id The ID of the weather machine.
     * @return A Mono emitting the weather machine message boundary.
     */
    @GetMapping(
            path = {"/device"},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<MessageBoundary> getWeatherMachineById(@RequestBody DeviceIdBoundary id) {
        return this.requester.route("get-weather-machine-by-id")
                .data(id)
                .retrieveMono(MessageBoundary.class)
                .log();
    }

    /**
     * Retrieves weather forecast.
     * @param message The message containing information about the weather forecast request.
     * @return A Flux emitting weather forecast message boundaries.
     */
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

    /**
     * Retrieves weather recommendations.
     * @return A Mono emitting weather recommendation message boundary.
     */
    @GetMapping("/recommendations")
    public Mono<MessageBoundary> getWeatherRecommendations() {
        return this.requester.route("get-weather-recommendations")
                .retrieveMono(MessageBoundary.class)
                .log();
    }
}

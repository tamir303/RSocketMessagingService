package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceIdBoundary;
import com.project.rsocketmessagingservice.logic.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller class for handling weather-related operations using RSocket.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * Attaches a new weather machine event.
     * @param message The new message boundary containing weather machine details.
     * @return A Mono emitting the created message boundary.
     */
    @MessageMapping("attach-new-weather-machine")
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(@Payload NewMessageBoundary message) {
        return weatherService.attachNewWeatherMachineEvent(message);
    }

    /**
     * Removes a weather machine event.
     * @param message The message boundary containing weather machine details to remove.
     * @return A Mono indicating the completion of the removal operation.
     */
    @MessageMapping("remove-weather-machine")
    public Mono<Void> removeWeatherMachineEvent(@Payload MessageBoundary message) {
        return weatherService.removeWeatherMachineEvent(message);
    }

    /**
     * Updates a weather machine event.
     * @param message The message boundary containing updated weather machine details.
     * @return A Mono indicating the completion of the update operation.
     */
    @MessageMapping("update-weather-machine")
    public Mono<Void> updateWeatherMachineEvent(@Payload MessageBoundary message) {
        return weatherService.updateWeatherMachineEvent(message);
    }

    /**
     * Retrieves all weather machines.
     * @return A Flux emitting all weather machine boundaries.
     */
    @MessageMapping("get-all-weather-machines")
    public Flux<MessageBoundary> getAllWeatherMachines() {
        return weatherService.getAllWeatherMachines();
    }

    /**
     * Retrieves a weather machine by its ID.
     * @param id The ID of the weather machine to retrieve.
     * @return A Mono emitting the weather machine boundary.
     */
    @MessageMapping("get-weather-machine-by-id")
    public Mono<MessageBoundary> getWeatherMachineById(@Payload DeviceIdBoundary id) {
        return weatherService.getWeatherMachineById(id);
    }

    /**
     * Removes all weather machines.
     * @return A Mono indicating the completion of the removal operation.
     */
    @MessageMapping("remove-all-weather-machines")
    public Mono<Void> removeAllWeatherMachines() {
        return weatherService.removeAllWeatherMachines();
    }

    /**
     * Retrieves weather forecast.
     * @param message The message boundary containing details for weather forecast.
     * @return A Flux emitting weather forecast boundaries.
     */
    @MessageMapping("get-weather-forecast")
    public Flux<MessageBoundary> getWeatherForecast(@Payload MessageBoundary message) {
        return weatherService.getWeatherForecast(message);
    }

    /**
     * Retrieves weather recommendations.
     * @return A Mono emitting weather recommendations boundary.
     */
    @MessageMapping("get-weather-recommendations")
    public Mono<MessageBoundary> getWeatherRecommendations() {
        return weatherService.createWeatherRecommendations();
    }
}

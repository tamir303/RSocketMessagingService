package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceIdBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface defining operations related to weather machines and forecasts.
 */
public interface WeatherService {
    /**
     * Attaches a new weather machine event.
     *
     * @param message Message containing the new weather machine event details.
     * @return Mono emitting the created weather machine event.
     */
    Mono<MessageBoundary> attachNewWeatherMachineEvent(NewMessageBoundary message);

    /**
     * Removes a weather machine event.
     *
     * @param message Message containing the weather machine event details to be removed.
     * @return Mono emitting a completion signal after removing the weather machine event.
     */
    Mono<Void> removeWeatherMachineEvent(MessageBoundary message);

    /**
     * Updates a weather machine event.
     *
     * @param message Message containing the updated weather machine event details.
     * @return Mono emitting a completion signal after updating the weather machine event.
     */
    Mono<Void> updateWeatherMachineEvent(MessageBoundary message);

    /**
     * Retrieves all weather machines.
     *
     * @return Flux emitting all weather machine events.
     */
    Flux<MessageBoundary> getAllWeatherMachines();

    /**
     * Retrieves the weather forecast for a specific weather machine.
     *
     * @param message Message containing the details of the weather machine.
     * @return Flux emitting the weather forecast for the specified weather machine.
     */
    Flux<MessageBoundary> getWeatherForecast(MessageBoundary message);

    /**
     * Creates weather recommendations.
     *
     * @return Mono emitting the created weather recommendations.
     */
    Mono<MessageBoundary> createWeatherRecommendations();

    /**
     * Removes all weather machines.
     *
     * @return Mono emitting a completion signal after removing all weather machines.
     */
    Mono<Void> removeAllWeatherMachines();

    /**
     * Retrieves a weather machine by its ID.
     *
     * @param id ID of the weather machine.
     * @return Mono emitting the weather machine corresponding to the given ID.
     */
    Mono<MessageBoundary> getWeatherMachineById(DeviceIdBoundary id);
}

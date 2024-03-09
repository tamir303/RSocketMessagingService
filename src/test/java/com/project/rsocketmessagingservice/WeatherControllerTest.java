package com.project.rsocketmessagingservice;

import com.project.rsocketmessagingservice.boundary.*;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceIdBoundary;
import com.project.rsocketmessagingservice.controller.WeatherController;
import com.project.rsocketmessagingservice.logic.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WeatherControllerTest {

    @Mock
    WeatherService weatherService;

    @InjectMocks
    WeatherController weatherController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /*
    attachNewWeatherMachineEvent_ValidInput_ReturnsMonoMessageBoundary:
    Given: We create a valid input NewMessageBoundary and its corresponding expected response MessageBoundary.
        We mock the behavior of the weatherService to return the expected response when attachNewWeatherMachineEvent is called with the input message.
    When: We call the attachNewWeatherMachineEvent method of the WeatherController with the input message.
    Then: We verify that the output of the method call matches the expected response.
    */
    @Test
    void attachNewWeatherMachineEvent_ValidInput_ReturnsMonoMessageBoundary() {
        // Arrange
        NewMessageBoundary message = NewMessageBoundary.builder()
                .messageType("type")
                .summary("summary")
                .externalReferences(Collections.emptyList())
                .messageDetails(new HashMap<>())
                .build();
        MessageBoundary expectedResponse = new MessageBoundary(message);
        when(weatherService.attachNewWeatherMachineEvent(message)).thenReturn(Mono.just(expectedResponse));

        // Act
        Mono<MessageBoundary> result = weatherController.attachNewWeatherMachineEvent(message);

        // Assert
        assertEquals(expectedResponse, result.block());
    }


    /*
    removeWeatherMachineEvent_ValidInput_ReturnsMonoVoid:
    Given: We create a valid input MessageBoundary.
        We mock the behavior of the weatherService to return an empty Mono when removeWeatherMachineEvent is called with the input message.
    When: We call the removeWeatherMachineEvent method of the WeatherController with the input message.
    Then: We verify that the output of the method call is an empty Mono.
     */
    @Test
    void removeWeatherMachineEvent_ValidInput_ReturnsMonoVoid() {
        // Given
        MessageBoundary message = new MessageBoundary();
        when(weatherService.removeWeatherMachineEvent(message)).thenReturn(Mono.empty());
        // When
        Mono<Void> result = weatherController.removeWeatherMachineEvent(message);
        // Then
        assertEquals(Mono.empty(), result);
    }

    @Test
    void updateWeatherMachineEvent_ValidInput_ReturnsMonoVoid() {
        // Given
        MessageBoundary message = new MessageBoundary();
        when(weatherService.updateWeatherMachineEvent(message)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = weatherController.updateWeatherMachineEvent(message);

        // Then
        assertEquals(Mono.empty(), result);
    }

    @Test
    void getAllWeatherMachines_ValidInput_ReturnsFluxMessageBoundary() {
        // Given
        MessageBoundary message1 = new MessageBoundary();
        MessageBoundary message2 = new MessageBoundary();
        List<MessageBoundary> expectedResponse = List.of(message1, message2);
        when(weatherService.getAllWeatherMachines()).thenReturn(Flux.fromIterable(expectedResponse));

        // When
        Flux<MessageBoundary> result = weatherController.getAllWeatherMachines();

        // Then
        assertEquals(expectedResponse, result.collectList().block());
    }

    @Test
    void getWeatherMachineById_ValidInput_ReturnsMonoMessageBoundary() {
        // Given
        DeviceIdBoundary deviceId = new DeviceIdBoundary();
        MessageBoundary expectedResponse = new MessageBoundary();
        when(weatherService.getWeatherMachineById(deviceId)).thenReturn(Mono.just(expectedResponse));

        // When
        Mono<MessageBoundary> result = weatherController.getWeatherMachineById(deviceId);

        // Then
        assertEquals(expectedResponse, result.block());
    }

    @Test
    void removeAllWeatherMachines_ValidInput_ReturnsMonoVoid() {
        // Given
        when(weatherService.removeAllWeatherMachines()).thenReturn(Mono.empty());

        // When
        Mono<Void> result = weatherController.removeAllWeatherMachines();

        // Then
        assertEquals(Mono.empty(), result);
    }

    @Test
    void getWeatherForecast_ValidInput_ReturnsFluxMessageBoundary() {
        // Given
        MessageBoundary message = new MessageBoundary();
        MessageBoundary forecast1 = new MessageBoundary();
        MessageBoundary forecast2 = new MessageBoundary();
        List<MessageBoundary> expectedResponse = List.of(forecast1, forecast2);
        when(weatherService.getWeatherForecast(message)).thenReturn(Flux.fromIterable(expectedResponse));

        // When
        Flux<MessageBoundary> result = weatherController.getWeatherForecast(message);

        // Then
        assertEquals(expectedResponse, result.collectList().block());
    }

    @Test
    void getWeatherRecommendations_ValidInput_ReturnsMonoMessageBoundary() {
        // Given
        MessageBoundary expectedResponse = new MessageBoundary();
        when(weatherService.createWeatherRecommendations()).thenReturn(Mono.just(expectedResponse));

        // When
        Mono<MessageBoundary> result = weatherController.getWeatherRecommendations();

        // Then
        assertEquals(expectedResponse, result.block());
    }
}

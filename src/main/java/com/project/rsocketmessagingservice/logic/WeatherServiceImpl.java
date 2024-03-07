package com.project.rsocketmessagingservice.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceDetailsBoundary;
import com.project.rsocketmessagingservice.dal.DeviceCrud;
import com.project.rsocketmessagingservice.dal.MessageCrud;
import com.project.rsocketmessagingservice.data.DeviceEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final MessageService messageService;
    private final DeviceCrud deviceCrud;
    private final OpenMeteoExtAPI openMeteoExtAPI;
    @Value("${component-service.port}")
    private String componentPort;
    private WebClient client;
    private ObjectMapper jackson;

    @PostConstruct
    public void init() {
        client = WebClient.create("http://localhost:" + componentPort);
        jackson = new ObjectMapper();
    }

    //// WORK
    @Override
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(NewMessageBoundary message) {

        return validateAndGetDevice(message.getMessageDetails())
                .flatMap(device -> {
                    System.err.println(device);
                    if (device.isWeatherDevice()) {
                        log.info("Creating new weather machine event: {}", device);

                        // Convert DeviceBoundary to DeviceEntity
                        DeviceEntity deviceEntity = device.toEntity();

                        // Save DeviceEntity to database and then proceed to create message
                        return deviceCrud
                                .save(deviceEntity)
                                .flatMap(savedDeviceEntity -> messageService.createMessage(message));
                    } else {
                        return Mono.empty();
                    }
                })
                .switchIfEmpty(Mono.empty())
                .log();
    }

    //// WORK
    @Override
    public Mono<Void> removeWeatherMachineEvent(MessageBoundary message) {
        try {
            DeviceBoundary deviceBoundary = jackson.convertValue(message.getMessageDetails(), DeviceBoundary.class);
            if (deviceBoundary != null && deviceBoundary.getDevice() != null) {
                String id = deviceBoundary.getDevice().getId();
                return deviceCrud
                        .findById(id)
                        .flatMap(deviceEntity -> {
                            if (deviceEntity != null) {
                                log.info("Removing weather machine with UUID: {}", id);
                                return deviceCrud.deleteById(id);
                            } else {
                                log.warn("Weather machine with UUID {} not found.", id);
                                return Mono.empty();
                            }
                        })
                        .then();
            } else {
                log.warn("Invalid message details for removing weather machine.");
                return Mono.empty();
            }
        } catch (Exception e) {
            log.error("Error removing weather machine: {}", e.getMessage());
            return Mono.error(e);
        }
    }

    //// WORK
    public Mono<Void> updateWeatherMachineEvent(MessageBoundary data) {
        return validateAndGetDevice(data.getMessageDetails())
                .flatMap(deviceDetailsBoundary -> {
                    if (deviceDetailsBoundary.isWeatherDevice()) {
                        return this.deviceCrud.existsById(deviceDetailsBoundary.getId())
                                .flatMap(exists -> {
                                    if (!exists) {
                                        return Mono.error(new RuntimeException("Device not found"));
                                    } else {
                                        log.info("Updating weather machine event: {}", deviceDetailsBoundary);

                                        // Save to the database and then make the PUT request
                                        return this.deviceCrud.save(deviceDetailsBoundary.toEntity())
                                                .then(
                                                        client.put()
                                                                .uri("/devices/{id}/status", deviceDetailsBoundary.getId())
                                                                .body(BodyInserters.fromValue(data))
                                                                .retrieve()
                                                                .toBodilessEntity()
                                                                .then()
                                                );
                                    }
                                });
                    } else {
                        return Mono.empty();
                    }
                })
                .doOnError(error -> {
                    // Log the error
                    log.error("Error occurred during updateWeatherMachineEvent: {}", error.getMessage());
                })
                .onErrorResume(error -> Mono.empty()); // or handle the error as needed
    }

    //// WORK
    @Override
    public Flux<MessageBoundary> getAllWeatherMachines() {
        return deviceCrud.findAll()
                .flatMap(deviceEntity -> {
                    String messageId = UUID.randomUUID().toString();
                    String timestamp = LocalDateTime.now().toString();
                    String summary = "Show Device " + deviceEntity.getId() + " Details";
                    ExternalReferenceBoundary externalReference = new ExternalReferenceBoundary("WeatherService", deviceEntity.getId());
                    Map<String, Object> deviceDetailsMap = Collections.singletonMap("device", deviceEntity.toMap());

                    MessageBoundary messageBoundary = MessageBoundary.builder()
                            .messageId(messageId)
                            .publishedTimestamp(timestamp)
                            .messageType("Get All Weather Machines")
                            .summary(summary)
                            .externalReferences(Collections.singletonList(externalReference))
                            .messageDetails(deviceDetailsMap)
                            .build();

                    // Emit each message boundary individually
                    return Mono.just(messageBoundary);
                })
                .doOnNext(System.err::println);
    }

    //TODO: Need to decide on the forecast structure in response to the consumer
    @Override
    public Flux<MessageBoundary> getWeatherForecast(MessageBoundary message) {
        //use here OpenMeteoExtAPI to get the forecast, do not forget to pass the location and the number of days, the API returns Flux of data structures by days you can pass them through or more beautify them
        return null;
    }

    //TODO: Need to decide on the recommendations structure in response to the consumer
    @Override
    public Mono<MessageBoundary> getWeatherRecommendations() {
        //use here OpenMeteoExtAPI to build the recommendation structure, do not forget to pass the location and the number of hours for the daily recommendation, the API returns Flux of data structures by hours analyze them and return Mono
        return null;
    }

    private Mono<DeviceDetailsBoundary> validateAndGetDevice(Map<String, Object> messageDetails) {
        try {
            DeviceDetailsBoundary deviceDetailsBoundary = jackson.convertValue(messageDetails.get("device"), DeviceDetailsBoundary.class);
            if (deviceDetailsBoundary == null) {
                log.error("No 'device' object found in messageDetails.");
                return Mono.empty();
            }
            return Mono.just(deviceDetailsBoundary).log();
        } catch (Exception e) {
            log.error("Error converting 'device' object to DeviceDetailsBoundary: {}", e.getMessage());
            return Mono.error(e);
        }
    }



}

package com.project.rsocketmessagingservice.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceDetailsBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.LocationBoundary;
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

import java.io.Reader;
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

    //// WORK
    @Override
    public Flux<MessageBoundary> getWeatherForecast(MessageBoundary message) {
        Gson gson = new Gson();

        JsonElement messageDetailsElement = gson.toJsonTree(message.getMessageDetails());
        if (messageDetailsElement.isJsonObject()) {
            JsonObject messageDetailsObject = messageDetailsElement.getAsJsonObject();

            JsonElement deviceElement = messageDetailsObject.get("device");
            if (deviceElement != null && deviceElement.isJsonObject()) {
                JsonObject deviceObject = deviceElement.getAsJsonObject();

                JsonElement additionalAttributesElement = deviceObject.get("additionalAttributes");
                if (additionalAttributesElement != null && additionalAttributesElement.isJsonObject()) {
                    // Extract additionalAttributes using Gson
                    JsonObject additionalAttributes = additionalAttributesElement.getAsJsonObject();

                    // Print the additionalAttributes map
                    System.err.println(additionalAttributes);

                    // DEFAULT VALUES FOR WEEK + TEL AVIV LOCATION
                    int days = 7;
                    LocationBoundary locationBoundary = new LocationBoundary(32.0809, 34.7806);

                    // Check if additionalAttributes contains necessary keys
                    if (additionalAttributes.has("days")) {
                        // 7 days default
                        days = additionalAttributes.get("days").getAsInt();
                    }
                    if (additionalAttributes.has("location")) {
                        locationBoundary = gson.fromJson(additionalAttributes.get("location"), LocationBoundary.class);
                    }

                    // Subscribe to the flux and convert each emitted string to MessageBoundary
                    LocationBoundary finalLocationBoundary = locationBoundary;
                    return openMeteoExtAPI.getWeeklyForecast(days, locationBoundary)
                            .map(jsonString -> {
                                DeviceDetailsBoundary deviceDetailsBoundary =  gson.fromJson(deviceObject, DeviceDetailsBoundary.class);
                                DeviceBoundary deviceBoundary = new DeviceBoundary(deviceDetailsBoundary);
                                deviceBoundary.getDevice().getAdditionalAttributes().put("location", finalLocationBoundary);
                                deviceBoundary.getDevice().getAdditionalAttributes().put("data", jsonString);
                                message.getMessageDetails().put("device", deviceBoundary.getDevice());
                                return message;
                            });
                } else {
                    System.err.println("Additional attributes are null or not present");
                }
            } else {
                System.err.println("Device object is null or not present");
            }
        }
        return Flux.empty();
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

package com.project.rsocketmessagingservice.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.dal.MessageCrud;
import com.project.rsocketmessagingservice.utils.AverageRecommendation;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceDetailsBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.LocationBoundary;
import com.project.rsocketmessagingservice.dal.DeviceCrud;
import com.project.rsocketmessagingservice.data.DeviceEntity;
import com.project.rsocketmessagingservice.logic.clients.ComponentClient;
import com.project.rsocketmessagingservice.logic.kafka.KafkaMessageProducer;
import com.project.rsocketmessagingservice.logic.openMeteo.OpenMeteoExtAPI;
import com.project.rsocketmessagingservice.utils.exceptions.DeviceIsNotWeatherTypeException;
import com.project.rsocketmessagingservice.utils.exceptions.DeviceNotFoundException;
import com.project.rsocketmessagingservice.utils.exceptions.MessageWithoutDeviceException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

import static com.project.rsocketmessagingservice.utils.MessageCreator.createUpdateMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final KafkaMessageProducer kafka;
    private final MessageService messageService;
    private  final MessageCrud messageCrud;
    private final DeviceCrud deviceCrud;
    private final OpenMeteoExtAPI openMeteoExtAPI;
    private final ComponentClient componentClient;
    private ObjectMapper jackson;
    // DEFAULT VALUES FOR WEEK + TEL AVIV LOCATION
    @Value("${openmeteo.days.default}")
    private int days;
    @Value("${openmeteo.hours.default}")
    private  int hours;
    @Value("${openmeteo.lat}")
    private double latitude;
    @Value("${openmeteo.lng}")
    private double longitude;
    private LocationBoundary locationBoundary;

    @PostConstruct
    public void init() {
        jackson = new ObjectMapper();
        locationBoundary = new LocationBoundary(latitude, longitude);
    }

    //// WORK
    @Override
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(NewMessageBoundary message) {
        return validateAndGetDevice(message.getMessageDetails())
                .flatMap(weatherMachineDevice -> {
                    if (weatherMachineDevice.isWeatherDevice()) {
                        log.info("Processing new weather machine event for device: {}", weatherMachineDevice.getId());
                        DeviceEntity deviceEntity = weatherMachineDevice.toEntity();
                        // Save the device to the database and then create the message
                        return deviceCrud.save(deviceEntity)
                                .flatMap(savedDeviceEntity -> messageService.createMessage(message))
                                .onErrorResume(error -> {
                                    // Handle error during processing
                                    log.error("Error processing weather machine message:", error);
                                    return Mono.empty();
                                });
                    } else {
                        return Mono.error(new DeviceIsNotWeatherTypeException("Device is not a weather machine"));
                    }
                })
                // Handle error during processing
                .switchIfEmpty(Mono.empty())
                .log();
    }

    //// WORK
    @Override
    public Mono<Void> removeWeatherMachineEvent(MessageBoundary message) {
        return Mono.just(message)
                .map(MessageBoundary::getMessageDetails)
                .flatMap(messageDetails -> {
                    try {
                        DeviceBoundary deviceBoundary = jackson.convertValue(messageDetails, DeviceBoundary.class);
                        // Extract the device ID from the message details
                        return Optional.ofNullable(deviceBoundary)
                                .map(device -> Mono.just(device.getDevice().getId()))
                                .orElse(Mono.empty());
                    } catch (Exception e) {
                        log.error("Error converting message details: {}", e.getMessage());
                        return Mono.empty();
                    }
                })
                // Validate not null and remove the device
                .filter(Objects::nonNull) // Filter out null IDs
                .doOnNext(id -> log.info("Removing weather machine with UUID: {}", id))
                .flatMap(deviceCrud::deleteById)
                .then()
                // Handle error during removal
                .onErrorResume(error -> {
                    log.error("Error removing weather machine event: {}", error.getMessage());
                    return Mono.empty();
                });
    }

    //// WORK
    public Mono<Void> updateWeatherMachineEvent(MessageBoundary message) {
        return validateAndGetDevice(message.getMessageDetails())
                .flatMap(weatherDeviceDetails -> {
                    if (weatherDeviceDetails.isWeatherDevice()) {
                        // Check if the device exists in the database
                        return this.deviceCrud.existsById(weatherDeviceDetails.getId())
                                .flatMap(exists -> {
                                    if (!exists) {
                                        return Mono.error(new DeviceNotFoundException("Device not found with ID: " + weatherDeviceDetails.getId()));
                                    } else {
                                        log.info("Updating weather machine event with ID: {}", weatherDeviceDetails.getId());
                                        // Save to database and conditionally send PUT request
                                        return this.deviceCrud.save(weatherDeviceDetails.toEntity())
                                                // Send Update request to Component Microservice
                                                .then(componentClient.updateDevice(weatherDeviceDetails.getId(), message))
                                                // Send message to Kafka that the device was updated
                                                .then(kafka.sendMessageToKafka(createUpdateMessage(message, weatherDeviceDetails.getId())));
                                    }
                                });
                    } else {
                        return Mono.empty();
                    }
                })
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()))
                .then();
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
                });
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

    @Override
    public Mono<MessageBoundary> createWeatherRecommendations() {
        AverageRecommendation averageRecommendation = new AverageRecommendation();
        Flux<Map<String, Object>> data = openMeteoExtAPI.getDailyRecommendation(locationBoundary, hours);
        return averageRecommendation
                .updateAllAverages(data)
                .flatMap(messageBoundary -> messageCrud.save(messageBoundary.toEntity())
                        .thenReturn(messageBoundary))
                .doOnNext(kafka::sendMessageToKafka);
    }


    private Mono<DeviceDetailsBoundary> validateAndGetDevice(Map<String, Object> messageDetails) {
        try {
            DeviceDetailsBoundary deviceDetailsBoundary = jackson.convertValue(messageDetails.get("device"), DeviceDetailsBoundary.class);
            if (deviceDetailsBoundary == null) {
                log.error("No 'device' object found in messageDetails.");
                return Mono.error(new MessageWithoutDeviceException("No 'device' object found in messageDetails."));
            }
            return Mono.just(deviceDetailsBoundary).log();
        } catch (Exception e) {
            log.error("Error converting 'device' object to DeviceDetailsBoundary: {}", e.getMessage());
            return Mono.error(e);
        }
    }
}

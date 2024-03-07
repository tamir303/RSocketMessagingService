package com.project.rsocketmessagingservice.logic;

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
import com.project.rsocketmessagingservice.data.DeviceEntity;
import com.project.rsocketmessagingservice.logic.clients.ComponentClient;
import com.project.rsocketmessagingservice.logic.kafka.KafkaMessageProducer;
import com.project.rsocketmessagingservice.logic.openMeteo.OpenMeteoExtAPI;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

import static com.project.rsocketmessagingservice.utils.Utils.createUpdateMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final KafkaMessageProducer kafka;
    private final MessageService messageService;
    private final DeviceCrud deviceCrud;
    private final OpenMeteoExtAPI openMeteoExtAPI;
    private final ComponentClient componentClient;
    private ObjectMapper jackson;

    @PostConstruct
    public void init() {
        jackson = new ObjectMapper();
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
                        return Mono.empty();
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
                                .map(DeviceBoundary::getDevice)
                                .map(DeviceDetailsBoundary::getId)
                                .map(Mono::just)
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
                                        return Mono.error(new RuntimeException("Device not found"));
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

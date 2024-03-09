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
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceIdBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.LocationBoundary;
import com.project.rsocketmessagingservice.dal.DeviceCrud;
import com.project.rsocketmessagingservice.dal.MessageCrud;
import com.project.rsocketmessagingservice.data.DeviceEntity;
import com.project.rsocketmessagingservice.logic.clients.ComponentClient;
import com.project.rsocketmessagingservice.logic.kafka.KafkaMessageProducer;
import com.project.rsocketmessagingservice.logic.openMeteo.OpenMeteoExtAPI;
import com.project.rsocketmessagingservice.utils.AverageRecommendation;
import com.project.rsocketmessagingservice.utils.exceptions.DeviceIsNotWeatherTypeException;
import com.project.rsocketmessagingservice.utils.exceptions.DeviceNotFoundException;
import com.project.rsocketmessagingservice.utils.exceptions.MessageWithoutDeviceException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

import static com.project.rsocketmessagingservice.utils.MessageCreator.createUpdateMessage;
import static com.project.rsocketmessagingservice.utils.MessageCreator.getMachineByIdMessage;
import static com.project.rsocketmessagingservice.utils.exceptions.ConstantErrorMessages.NO_DEVICE_FOUND;

/**
 * Service implementation for managing weather-related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final KafkaMessageProducer kafka;
    private final MessageService messageService;
    private final MessageCrud messageCrud;
    private final DeviceCrud deviceCrud;
    private final OpenMeteoExtAPI openMeteoExtAPI;
    private final ComponentClient componentClient;

    @Autowired
    private Environment env;
    private ObjectMapper jackson;
    // DEFAULT VALUES FOR WEEK + TEL AVIV LOCATION
    @Value("${openmeteo.days.default}")
    private int days;
    @Value("${openmeteo.hours.default}")
    private int hours;
    @Value("${openmeteo.lat}")
    private double latitude;
    @Value("${openmeteo.lng}")
    private double longitude;
    private LocationBoundary locationBoundary;

    /**
     * Initializes the WeatherServiceImpl after bean creation.
     */
    @PostConstruct
    public void init() {
        jackson = new ObjectMapper();
        this.days = Integer.parseInt(Objects.requireNonNull(env.getProperty("openmeteo.days.default")));
        this.locationBoundary = new LocationBoundary(latitude, longitude);
    }

    /**
     * Attaches a new weather machine event.
     *
     * @param message Message containing the new weather machine event details.
     * @return Mono emitting the created weather machine event.
     */
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

    /**
     * Removes a weather machine event.
     *
     * @param message Message containing the weather machine event details to be removed.
     * @return Mono emitting a completion signal after removing the weather machine event.
     */
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

    /**
     * Updates a weather machine event.
     *
     * @param message Message containing the updated weather machine event details.
     * @return Mono emitting a completion signal after updating the weather machine event.
     */
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


    /**
     * Retrieves all weather machines.
     *
     * @return Flux emitting all weather machine events.
     */
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

    /**
     * Retrieves the weather forecast for a specific weather machine.
     *
     * @param message Message containing the details of the weather machine.
     * @return Flux emitting the weather forecast for the specified weather machine.
     */
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

                    System.err.println(days + ",   " + locationBoundary);
                    // Check if additionalAttributes contains necessary keys
                    if (additionalAttributes.has("days")) {
                        // 7 days default
                        System.err.println("7");
                        days = additionalAttributes.get("days").getAsInt();
                    }
                    if (additionalAttributes.has("location")) {
                        System.err.println("location");
                        locationBoundary = gson.fromJson(additionalAttributes.get("location"), LocationBoundary.class);
                    }

                    // Subscribe to the flux and convert each emitted string to MessageBoundary
                    LocationBoundary finalLocationBoundary = locationBoundary;

                    return openMeteoExtAPI.getWeeklyForecast(days, locationBoundary)
                            .flatMap(jsonString -> {
                                // Create a new MessageBoundary object as a clone
                                MessageBoundary clonedMessage = new MessageBoundary();
                                // Copy necessary fields from the original message
                                clonedMessage.setMessageId(UUID.randomUUID().toString());
                                clonedMessage.setPublishedTimestamp(LocalDateTime.now().toString());
                                clonedMessage.setMessageType(message.getMessageType());
                                clonedMessage.setSummary(message.getSummary());
                                clonedMessage.setExternalReferences(message.getExternalReferences());
                                // Create a deep copy of the message details to avoid mutation of the original message
                                Map<String, Object> messageDetailsCopy = new HashMap<>(message.getMessageDetails());
                                clonedMessage.setMessageDetails(messageDetailsCopy);

                                // Modify the cloned message as needed
                                DeviceDetailsBoundary deviceDetailsBoundary = gson.fromJson(deviceObject, DeviceDetailsBoundary.class);
                                DeviceBoundary deviceBoundary = new DeviceBoundary(deviceDetailsBoundary);
                                deviceBoundary.getDevice().getAdditionalAttributes().put("location", finalLocationBoundary);
                                deviceBoundary.getDevice().getAdditionalAttributes().put(jsonString.get("date").toString(), jsonString);
                                clonedMessage.getMessageDetails().put("device", deviceBoundary.getDevice());

                                // Reset values to default after processing each request
                                init();

                                // Save the cloned message to the database
                                return messageCrud.save(clonedMessage.toEntity()).thenReturn(clonedMessage);
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

    /**
     * Creates weather recommendations.
     *
     * @return Mono emitting the created weather recommendations.
     */
    @Override
    public Mono<MessageBoundary> createWeatherRecommendations() {
        AverageRecommendation averageRecommendation = new AverageRecommendation();
        Flux<Map<String, Object>> data = openMeteoExtAPI.getDailyRecommendation(locationBoundary, hours);
        return averageRecommendation
                .updateAllAverages(data)
                .flatMap(messageBoundary -> messageCrud.save(messageBoundary.toEntity()).thenReturn(messageBoundary))
                .doOnNext(kafka::sendMessageToKafka);
    }

    /**
     * Removes all weather machines.
     *
     * @return Mono emitting a completion signal after removing all weather machines.
     */
    @Override
    public Mono<Void> removeAllWeatherMachines() {
        log.info("Removing all weather machines");
        return deviceCrud
                .deleteAll()
                .log();
    }

    /**
     * Retrieves a weather machine by its ID.
     *
     * @param id ID of the weather machine.
     * @return Mono emitting the weather machine corresponding to the given ID.
     */
    @Override
    public Mono<MessageBoundary> getWeatherMachineById(DeviceIdBoundary id) {
        log.info("Getting weather machine by ID: {}", id);

        return deviceCrud.findById(id.getDeviceId())
                .map(device -> getMachineByIdMessage(device, id.getDeviceId()))
                .doOnNext(message -> log.info("Found weather machine: {}", message))
                .doOnError(error -> log.error("Error getting weather machine by ID: {}", error.getMessage()))
                .switchIfEmpty(Mono.error(new DeviceNotFoundException(NO_DEVICE_FOUND)));
    }

    /**
     * Validates and retrieves device details from the message.
     *
     * @param messageDetails Details of the message containing the device.
     * @return Mono emitting the device details.
     */
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

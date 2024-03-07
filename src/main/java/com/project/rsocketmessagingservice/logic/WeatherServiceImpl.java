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
import org.springframework.stereotype.Service;
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
    private final MessageCrud messageCrud;
    private final MessageService messageService;
    private final DeviceCrud deviceCrud;
    private final OpenMeteoExtAPI openMeteoExtAPI;
    private ObjectMapper jakson;

    @PostConstruct
    public void init() {
        jakson = new ObjectMapper();
    }

    //// NEED TO TEST
    @Override
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(NewMessageBoundary message) {

        return validateAndGetDevice(message.getMessageDetails())
                .flatMap(device -> {
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

    //// NEED TO TEST
    @Override
    public Mono<Void> removeWeatherMachineEvent(MessageBoundary message) {
        String machineUUID = message.getMessageDetails().get("id").toString();
        return deviceCrud
                .findById(machineUUID)  // Find the device by UUID
                .flatMap(deviceEntity -> {
                    if (deviceEntity != null) {
                        log.info("Removing weather machine with UUID: {}", machineUUID);
                        return deviceCrud.deleteById(machineUUID);  // Delete the device
                    } else {
                        log.warn("Weather machine with UUID {} not found.", machineUUID);
                        return Mono.empty();
                    }
                })
                .then();
    }

    // TODO: NEED TO CHECK
    @Override
    public Mono<Void> updateWeatherMachineEvent(MessageBoundary data) {
        return validateAndGetDevice(data.getMessageDetails())
                .flatMap(deviceDetailsBoundary -> {
                    if (deviceDetailsBoundary.isWeatherDevice()) {
                        return this.deviceCrud.existsById(deviceDetailsBoundary.getId())
                                .map(exists -> {
                                    if (!exists) {
                                        return Mono.error(new RuntimeException("Device not found"));
                                    } else {
                                        log.info("Updating weather machine event: {}", deviceDetailsBoundary);
                                        return this.deviceCrud.save(deviceDetailsBoundary.toEntity());
                                    }
                                });
                    } else {
                        return Mono.empty();
                    }
                }).then();
    }


    //TODO: Need to decide if need this or get all from message boundary
    @Override
    public Flux<MessageBoundary> getAllWeatherMachines() {
        return this.deviceCrud.findAll()
                .flatMap(deviceEntity -> {
                    String messageId = UUID.randomUUID().toString();
                    String timestamp = LocalDateTime.now().toString();
                    String summary = "Show Device " + deviceEntity.getId() + " Details";
                    ExternalReferenceBoundary externalReference = new ExternalReferenceBoundary("WeatherService", deviceEntity.getId());
                    Map<String, Object> deviceDetailsMap = Collections.singletonMap("device", deviceEntity.toMap());

                    return Mono.just(MessageBoundary.builder()
                            .messageId(messageId)
                            .publishedTimestamp(timestamp)
                            .messageType("Get All Weather Machines")
                            .summary(summary)
                            .externalReferences(Collections.singletonList(externalReference))
                            .messageDetails(deviceDetailsMap)
                            .build());
                });
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
            // Extract the inner "device" map from the messageDetails
            Map<String, Object> deviceMap = (Map<String, Object>) messageDetails.get("device");
            if (deviceMap == null) {
                log.error("No 'device' object found in messageDetails.");
                return Mono.empty();
            }

            // Convert the "device" map to a DeviceBoundary object
            DeviceDetailsBoundary deviceBoundary = jakson.convertValue(deviceMap, DeviceDetailsBoundary.class);
            // Return the DeviceBoundary object wrapped in a Mono
            return Mono.just(deviceBoundary);
        } catch (Exception e) {
            // If an exception occurs during conversion, log the error and return an empty Mono
            log.error("Error converting 'device' object to DeviceBoundary: {}", e.getMessage());
            return Mono.empty();
        }
    }


}

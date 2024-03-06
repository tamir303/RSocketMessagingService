package com.project.rsocketmessagingservice.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.StatusBoundary;
import com.project.rsocketmessagingservice.dal.DeviceCrud;
import com.project.rsocketmessagingservice.dal.MessageCrud;
import com.project.rsocketmessagingservice.data.DeviceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
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

    //// WORK
    @Override
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(NewMessageBoundary message) {
        return validateAndGetDevice(message.getMessageDetails())
                .flatMap(device -> {
                    if (device.isWeatherDevice()) {
                        log.info("Creating new weather machine event: {}", device); // Use String formatting for readability
                            deviceCrud.save(device.toEntity());
                            return messageService.createMessage(message);
                        }
                    else {
                        return Mono.empty();
                    }
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> removeWeatherMachineEvent(MessageBoundary message) {
        return validateAndGetDevice(message.getMessageDetails())
                .flatMap(deviceDetailsBoundary -> {
                    if (deviceDetailsBoundary.isWeatherDevice()) {
                        log.info("Removing weather machine event: {}", deviceDetailsBoundary);
                        this.deviceCrud.deleteById(deviceDetailsBoundary.toEntity().getId());
                    }
                    return Mono.empty();
                });
    }

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


    @Override
    public Flux<MessageBoundary> getAllWeatherMachines() {
        Flux<DeviceEntity> deviceEntities = this.deviceCrud.findAll();
     return deviceEntities.flatMap(deviceEntity -> Mono.just(MessageBoundary.builder()
             .messageId(UUID.randomUUID().toString())
             .publishedTimestamp(LocalDateTime.now().toString())
             .messageType("Get All Weather Machines")
             .summary("Show Device " + deviceEntity.getId() + " Details")
             .externalReferences(List.of((new ExternalReferenceBoundary("WeatherService", deviceEntity.getId()))))
             .messageDetails(deviceEntity.toMap()).build()));
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

    private Mono<DeviceBoundary> validateAndGetDevice(Map<String, Object> messageDetails) {
        try {
            // Extract the inner "device" map from the messageDetails
            Map<String, Object> deviceMap = (Map<String, Object>) messageDetails.get("device");
            if (deviceMap == null) {
                log.error("No 'device' object found in messageDetails.");
                return Mono.empty();
            }

            // Convert the "device" map to a DeviceBoundary object
            ObjectMapper objectMapper = new ObjectMapper();
            DeviceBoundary deviceBoundary = objectMapper.convertValue(deviceMap, DeviceBoundary.class);
            // Return the DeviceBoundary object wrapped in a Mono
            return Mono.just(deviceBoundary);
        } catch (Exception e) {
            // If an exception occurs during conversion, log the error and return an empty Mono
            log.error("Error converting 'device' object to DeviceBoundary: {}", e.getMessage());
            return Mono.empty();
        }
    }


}

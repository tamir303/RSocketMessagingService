package com.project.rsocketmessagingservice.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceBoundary;
import com.project.rsocketmessagingservice.dal.MessageCrud;
import com.project.rsocketmessagingservice.data.DeviceEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final MessageCrud messageCrud;
    private final MessageService messageService;
    private ObjectMapper jackson;

    @PostConstruct
    public void init() {
        this.jackson = new ObjectMapper();
    }

    @Override
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(NewMessageBoundary data) {
        return
            // Validate and convert message details to DeviceBoundary
            DeviceBoundary device = validateAndGetDevice(data.getMessageDetails());
            if (device == null) {
                return Mono.error(new IllegalArgumentException("Invalid device details"));
            }

            // Check device type and process accordingly
            if (device.isWeatherDevice()) {
                DeviceEntity entity = device.toEntity();
                data.setMessageDetails(entity.toMap()); // Use entity directly
                return messageService.createMessage(data);
            }

            // Return empty Mono if device is not a weather machine
            else {
                return Mono.empty();
            }
    }

    @Override
    public Mono<Void> removeWeatherMachineEvent(String machineUUID) {
        return messageCrud.findAll() // Assuming this retrieves all entities
                .filter(entity -> entity.getMessageDetails().get("machineUUID").equals(machineUUID)) // Filter entities by machineUUID
                .flatMap(messageCrud::delete) // Delete matching entities
                .then(); // Return a Mono<Void>
    }

    @Override
    public Mono<Void> updateWeatherMachineEvent(MessageBoundary data) {
        return null;
    }

    @Override
    public Flux<MessageBoundary> getAllWeatherMachines() {
        return messageCrud.
                findAll().
                map(MessageBoundary::new)
                .log();
    }

    @Override
    public Flux<MessageBoundary> getWeatherForecast(String houseUUID, Integer days) {
        return null;
    }

    @Override
    public Mono<MessageBoundary> getWeatherRecommendations(MessageBoundary data) {
        return null;
    }

    @Override
    public Mono<Void> changeMachineState(MessageBoundary data) {
        return null;
    }

    private DeviceBoundary validateAndGetDevice(Object messageDetails) {
        if (messageDetails instanceof DeviceBoundary) {
            return (DeviceBoundary) messageDetails;
        } else {
            return null; // Or throw an exception if expected to be DeviceBoundary
        }
    }
}

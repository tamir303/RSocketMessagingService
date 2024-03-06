package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceBoundary;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final MessageCrud messageCrud;
    private final MessageService messageService;
    private final DeviceCrud deviceCrud;

    @Override
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(NewMessageBoundary message) {
        return validateAndGetDevice(message.getMessageDetails())
                .flatMap(deviceBoundary -> {
                    if (deviceBoundary.isWeatherDevice()) {
                        log.info("Creating new weather machine event: {}", deviceBoundary); // Use String formatting for readability
                            deviceCrud.save(deviceBoundary.toEntity());
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
                .flatMap(deviceBoundary -> {
                    if (deviceBoundary.isWeatherDevice()) {
                        log.info("Removing weather machine event: {}", deviceBoundary);
                        this.deviceCrud.deleteById(deviceBoundary.toEntity().getId());
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> updateWeatherMachineEvent(MessageBoundary data) {
        return validateAndGetDevice(data.getMessageDetails())
                .flatMap(deviceBoundary -> {
                    if (deviceBoundary.isWeatherDevice()) {
                        return this.deviceCrud.existsById(deviceBoundary.getId())
                                .map(exists -> {
                                    if (!exists) {
                                        return Mono.error(new RuntimeException("Device not found"));
                                    } else {
                                        log.info("Updating weather machine event: {}", deviceBoundary);
                                        return this.deviceCrud.save(deviceBoundary.toEntity());
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

    @Override
    public Flux<MessageBoundary> getWeatherForecast(MessageBoundary message) {
        return null;
    }

    @Override
    public Mono<MessageBoundary> getWeatherRecommendations() {
        return null;
    }

    private Mono<DeviceBoundary> validateAndGetDevice(Object messageDetails) {
        if (messageDetails instanceof DeviceBoundary) {
            return Mono.just((DeviceBoundary) messageDetails);
        } else {
            return Mono.empty();
        }
    }
}

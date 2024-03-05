package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherObjectBoundary;
import com.project.rsocketmessagingservice.dal.MessageCrud;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final MessageCrud messageCrud;

    // INPUT
    /*
    * {
          "messageId": "5f05c7b5-5769-4a09-a2f0-c4ba0cbe3fbd",
          "publishedTimestamp": "2024-03-05T23:32:49.562005800",
          "messageType": "adding new machine for weather control system",
          "summary": "New house has been registered to the weather control",
          "externalReferences": [
            {
              "service": "string",
              "externalServiceId": "string"
            }
          ],
          "messageDetails": {
            "Location": {
              "Lat": 35.12478,
              "Lng": 36.25741
            },
            "houseUUID": "123",
            "machineUUID": "1234"
          }
        }
    * */
    @Override
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(NewMessageBoundary data) {
        MessageBoundary messageBoundary = new MessageBoundary(data);
        messageBoundary.setMessageId(UUID.randomUUID().toString());
        messageBoundary.setPublishedTimestamp(LocalDateTime.now().toString());

        return Mono.just(messageBoundary.toEntity())
                .flatMap(messageCrud::save)
                .map(MessageBoundary::new)
                .log();
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
}

package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherObjectBoundary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final MessageService messageService;

    @Override
    public Mono<MessageBoundary> attachNewWeatherMachineEvent(WeatherObjectBoundary data) {

        NewMessageBoundary newMessageBoundary = new NewMessageBoundary();
        newMessageBoundary.setMessageDetails(data.toMap());
        messageService.createMessage(newMessageBoundary);
    }

    @Override
    public Mono<Void> removeWeatherMachineEvent(MessageBoundary data) {
        return null;
    }

    @Override
    public Mono<Void> updateWeatherMachineEvent(MessageBoundary data) {
        return null;
    }

    @Override
    public Flux<MessageBoundary> getAllWeatherMachines(String houseUUID) {
        return null;
    }

    @Override
    public Flux<MessageBoundary> getWeatherForecast(MessageBoundary data) {
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

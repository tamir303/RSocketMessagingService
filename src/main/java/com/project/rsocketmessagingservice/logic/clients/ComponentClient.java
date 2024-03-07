package com.project.rsocketmessagingservice.logic.clients;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ComponentClient {
    @Value("${component-service.port}")
    private String componentPort;
    private String putUrl;
    private WebClient client;

    @PostConstruct
    public void init() {
        String baseUrl = "http://localhost:" + componentPort;
        this.putUrl = "/devices/{id}/status";
        this.client = WebClient.create(baseUrl);
    }

    public Mono<Void> updateDevice(String deviceId, MessageBoundary updatedDevice) {
        return this.client.put()
                .uri(putUrl, deviceId)
                .body(BodyInserters.fromValue(updatedDevice))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}

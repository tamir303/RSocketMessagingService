package com.project.rsocketmessagingservice.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {
    @Value("${target.topic.name:topic1}")
    private String targetTopic;
    private final StreamBridge kafka;
    private ObjectMapper jackson;
    private final WeatherService weatherService;

    @PostConstruct
    public void init() {
        jackson = new ObjectMapper();
    }

    public Mono<Void> sendMessageToKafka(MessageService message) {
        try {
            String messageToKafka = this.jackson.writeValueAsString(message);
            kafka.send(targetTopic, message);
            log.info("Sent message to Kafka: {}", messageToKafka);
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
        }

        return Mono.empty();
    }
}
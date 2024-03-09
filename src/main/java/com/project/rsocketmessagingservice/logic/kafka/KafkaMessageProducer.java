package com.project.rsocketmessagingservice.logic.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service class for producing messages to Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {
    @Value("${target.topic.name:topic1}")
    private String targetTopic;
    private final StreamBridge kafka;
    private ObjectMapper jackson;

    /**
     * Initializes the ObjectMapper after construction.
     */
    @PostConstruct
    public void init() {
        jackson = new ObjectMapper();
    }

    /**
     * Sends a message to Kafka.
     * @param message The message to be sent.
     * @return A Mono representing the completion of the operation.
     */
    public Mono<Void> sendMessageToKafka(MessageBoundary message) {
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

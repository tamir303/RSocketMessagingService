package com.project.rsocketmessagingservice.logic.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceDetailsBoundary;
import com.project.rsocketmessagingservice.logic.WeatherService;
import com.project.rsocketmessagingservice.utils.exceptions.DeviceIsNotWeatherTypeException;
import com.project.rsocketmessagingservice.utils.exceptions.MessageWithoutDeviceException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageListener {

    @Value("${target.topic.name:topic1}")
    private String targetTopic;
    private final StreamBridge kafka;
    private ObjectMapper objectMapper;
    private final WeatherService weatherService;

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper();
    }

    // Listen to the Kafka topic and process the received message
    @Bean
    public Consumer<String> demoMessageSink() {
        return message -> {
            try {
                MessageBoundary receivedMessage = objectMapper.readValue(message, MessageBoundary.class);
                validateMessage(receivedMessage);
                processWeatherMessage(receivedMessage);
            } catch (Exception e) {
                log.error("Error processing message: {}", e.getMessage());
            }
        };
    }

    // Validate the message received from Kafka
    private void validateMessage(MessageBoundary message) {
        if (message.getMessageDetails() == null
                || !message.getMessageDetails().containsKey("device")
                || message.getMessageDetails().get("device") == null
                || !message.getExternalReferences().get(0).getService().equals("WeatherService")) {
            throw new MessageWithoutDeviceException("Message is missing device details.");
        }
    }

    // Process the weather message and create a new device event
    private void processWeatherMessage(MessageBoundary message) {
        DeviceDetailsBoundary deviceDetails = objectMapper.convertValue(message.getMessageDetails().get("device"), DeviceDetailsBoundary.class);
        if (deviceDetails.isWeatherDevice()) {
            switch (message.getMessageType().toLowerCase()) {
                // Create a new weather machine event
                case "create" -> {
                    log.info("Creating new weather machine event from kafka.");
                    NewMessageBoundary newMessage = buildWeatherMessage(message);
                    weatherService.attachNewWeatherMachineEvent(newMessage);
                }
                // Remove a weather machine event
                case "remove" -> {
                    log.info("Removing weather machine event from kafka.");
                    weatherService.removeWeatherMachineEvent(message);
                }
            }
        } else {
            throw new DeviceIsNotWeatherTypeException("Device is not a weather device.");
        }
    }

    // Build a new message from the received message
    private NewMessageBoundary buildWeatherMessage(MessageBoundary message) {
        return NewMessageBoundary.builder()
                .messageType(message.getMessageType())
                .summary(message.getSummary())
                .messageDetails(new HashMap<>(message.getMessageDetails()))
                .externalReferences(message.getExternalReferences())
                .build();
    }
}

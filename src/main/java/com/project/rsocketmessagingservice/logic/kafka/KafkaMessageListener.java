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

/**
 * Configuration class for listening to Kafka messages and processing them.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageListener {

    @Value("${target.topic.name:topic1}")
    private String targetTopic;
    private final StreamBridge kafka;
    private ObjectMapper objectMapper;
    private final WeatherService weatherService;

    /**
     * Initializes the ObjectMapper after construction.
     */
    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Configures a Kafka consumer to listen to messages and process them.
     *
     * @return A consumer function for processing Kafka messages.
     */
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

    /**
     * Validates the message received from Kafka.
     *
     * @param message The message to validate.
     */
    private void validateMessage(MessageBoundary message) {
        if (message.getMessageDetails() == null
                || !message.getMessageDetails().containsKey("device")
                || message.getMessageDetails().get("device") == null
                || !message.getExternalReferences().get(0).getService().equals("WeatherService")) {
            throw new MessageWithoutDeviceException("Message is missing device details.");
        }
    }

    /**
     * Processes the weather message received from Kafka.
     *
     * @param message The weather message to process.
     */
    private void processWeatherMessage(MessageBoundary message) {
        DeviceDetailsBoundary deviceDetails = objectMapper.convertValue(message.getMessageDetails().get("device"), DeviceDetailsBoundary.class);
        if (deviceDetails.isWeatherDevice()) {
            // Process the message based on the message type (create or remove) device event from kafka
            log.info("Creating new weather machine event from kafka.");
            NewMessageBoundary newMessage = buildWeatherMessage(message);
            weatherService.attachNewWeatherMachineEvent(newMessage);
        } else {
            throw new DeviceIsNotWeatherTypeException("Device is not a weather device.");
        }
    }

    /**
     * Builds a new message from the received weather message.
     *
     * @param message The weather message received from Kafka.
     * @return A new message boundary built from the received weather message.
     */
    private NewMessageBoundary buildWeatherMessage(MessageBoundary message) {
        return NewMessageBoundary.builder()
                .messageType(message.getMessageType())
                .summary(message.getSummary())
                .messageDetails(new HashMap<>(message.getMessageDetails()))
                .externalReferences(message.getExternalReferences())
                .build();
    }
}

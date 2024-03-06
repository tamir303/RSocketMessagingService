package com.project.rsocketmessagingservice.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
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
public class KafkaProducer {
    @Value("${target.topic.name:topic1}")
    private String targetTopic;
    private final StreamBridge kafka;
    private ObjectMapper jackson;
    private WeatherService weatherService;

    @PostConstruct
    public void init() {
        jackson = new ObjectMapper();
    }

    @Bean
    public void sendMessageToKafka(MessageBoundary message) {
        try {
            String messageToKafka = this.jackson.writeValueAsString(message);
            this.kafka.send(targetTopic, messageToKafka);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Bean
    public Consumer<String> demoMessageSink(){
        return stringInput->{
            try {
                MessageBoundary message = this.jackson.readValue(stringInput, MessageBoundary.class);
                if (message.getMessageDetails() == null) {
                    message.setMessageDetails(new HashMap<>());
                }
                message.getMessageDetails()
                        .put("status", "received-from-kafka");

                MessageBoundary storedMessage = this.weatherService
                        .store(message)
                        .block();

                log.info("*** stored: " + storedMessage);
            }catch (Exception e) {
                e.printStackTrace();
                log.error(e);
            }
        };
    }
}

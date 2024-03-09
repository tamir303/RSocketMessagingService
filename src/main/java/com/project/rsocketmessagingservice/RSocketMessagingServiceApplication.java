package com.project.rsocketmessagingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * The RSocketMessagingServiceApplication class is the entry point for the RSocket Messaging Service application.
 */
@SpringBootApplication
public class RSocketMessagingServiceApplication {

    /**
     * The main method starts the RSocket Messaging Service application.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RSocketMessagingServiceApplication.class, args);
    }
}

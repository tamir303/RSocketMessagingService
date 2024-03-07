package com.project.rsocketmessagingservice.utils;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class Utils {
    public static MessageBoundary createUpdateMessage(MessageBoundary message, String deviceId) {
        return MessageBoundary.builder()
                .messageType("DEVICE_STATUS_UPDATED")
                .summary("Device {%s} had status updated \n %s".formatted(deviceId, message.getSummary()))
                .externalReferences(Collections.singletonList(new ExternalReferenceBoundary("WeatherService", deviceId)))
                .messageDetails(message.getMessageDetails())
                .build();
    }
}

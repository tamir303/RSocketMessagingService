package com.project.rsocketmessagingservice.utils;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceDetailsBoundary;
import com.project.rsocketmessagingservice.data.DeviceEntity;

import java.util.Collections;

public class MessageCreator {
    public static MessageBoundary createUpdateMessage(MessageBoundary message, String deviceId) {
        return MessageBoundary.builder()
                .messageType("DEVICE_STATUS_UPDATED")
                .summary("Device {%s} had status updated \n %s".formatted(deviceId, message.getSummary()))
                .externalReferences(Collections.singletonList(new ExternalReferenceBoundary("WeatherService", deviceId)))
                .messageDetails(message.getMessageDetails())
                .build();
    }

    public static MessageBoundary getMachineByIdMessage(DeviceEntity device, String deviceId) {
        return MessageBoundary.builder()
                .messageType("GET_WEATHER_MACHINE_BY_ID")
                .summary("Requesting weather machine with id {%s}".formatted(deviceId))
                .externalReferences(Collections.singletonList(new ExternalReferenceBoundary("WeatherService", deviceId)))
                .messageDetails(Collections.singletonMap("device", device.toMap()))
                .build();
    }
}

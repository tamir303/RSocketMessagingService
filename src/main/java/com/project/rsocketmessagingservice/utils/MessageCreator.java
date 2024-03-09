package com.project.rsocketmessagingservice.utils;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.data.DeviceEntity;

import java.util.Collections;

/**
 * The MessageCreator class provides static methods to create MessageBoundary objects
 * for different purposes such as updating device status and retrieving device information.
 */
public class MessageCreator {

    /**
     * Creates a MessageBoundary object for updating device status.
     *
     * @param message  The original message boundary.
     * @param deviceId The ID of the device whose status is updated.
     * @return The MessageBoundary object for updating device status.
     */
    public static MessageBoundary createUpdateMessage(MessageBoundary message, String deviceId) {
        return MessageBoundary.builder()
                .messageType("DEVICE_STATUS_UPDATED")
                .summary("Device {%s} had status updated \n %s".formatted(deviceId, message.getSummary()))
                .externalReferences(Collections.singletonList(new ExternalReferenceBoundary("WeatherService", deviceId)))
                .messageDetails(message.getMessageDetails())
                .build();
    }

    /**
     * Creates a MessageBoundary object for retrieving device information by ID.
     *
     * @param device   The device entity containing the device information.
     * @param deviceId The ID of the device to retrieve information for.
     * @return The MessageBoundary object for retrieving device information by ID.
     */
    public static MessageBoundary getMachineByIdMessage(DeviceEntity device, String deviceId) {
        return MessageBoundary.builder()
                .messageType("GET_WEATHER_MACHINE_BY_ID")
                .summary("Requesting weather machine with id {%s}".formatted(deviceId))
                .externalReferences(Collections.singletonList(new ExternalReferenceBoundary("WeatherService", deviceId)))
                .messageDetails(Collections.singletonMap("device", device.toMap()))
                .build();
    }
}

package com.project.rsocketmessagingservice.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception indicating that the device is not of weather type.
 * This exception is thrown when attempting to perform weather-related operations on a device that is not intended for weather monitoring.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DeviceIsNotWeatherTypeException extends RuntimeException {
    /**
     * Constructs a new DeviceIsNotWeatherTypeException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public DeviceIsNotWeatherTypeException(String message) {
        super(message);
    }
}
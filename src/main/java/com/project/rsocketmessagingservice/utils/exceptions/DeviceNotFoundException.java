package com.project.rsocketmessagingservice.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception indicating that a device was not found.
 * This exception is typically thrown when attempting to access or manipulate a device that does not exist.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DeviceNotFoundException extends RuntimeException {
    /**
     * Constructs a new DeviceNotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public DeviceNotFoundException(String message) {
        super(message);
    }
}

package com.project.rsocketmessagingservice.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception indicating that a message does not contain a device.
 * This exception is typically thrown when attempting to process a message without a device reference.
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class MessageWithoutDeviceException extends RuntimeException {
    /**
     * Constructs a new MessageWithoutDeviceException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public MessageWithoutDeviceException(String message) {
        super(message);
    }
}

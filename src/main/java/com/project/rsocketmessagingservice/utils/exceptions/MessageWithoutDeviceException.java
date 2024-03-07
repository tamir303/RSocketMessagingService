package com.project.rsocketmessagingservice.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class MessageWithoutDeviceException extends RuntimeException{
    public MessageWithoutDeviceException(String s) {
        super(s);
    }
}

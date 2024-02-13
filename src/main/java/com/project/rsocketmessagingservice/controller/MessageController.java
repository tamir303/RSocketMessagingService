package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.logic.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    /**
        java -jar rsc-0.9.1.jar --request --route=publish-message-req-resp --data='{"messageType":"SIMPLE","summary":"Hello, World!","externalReferences":[{"type":"SIMPLE","value":"Hello, World!"}], "messageDetails":{"simple":"Hello, World!"}}' --debug tcp://localhost:7001
     */
    @MessageMapping("publish-message-req-resp")
    public Mono<MessageBoundary> createMessage(NewMessageBoundary message) {
        log.debug("Invoking: publish-message-req-resp");
        return messageService.createMessage(message);
    }
}

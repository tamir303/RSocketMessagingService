package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.logic.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @MessageMapping("publish-message-req-resp")
    public Mono<MessageBoundary> createMessage(
            @Payload NewMessageBoundary message) {
        log.debug("Invoking: publish-message-req-resp");
        return messageService.createMessage(message);
    }

    @MessageMapping("getAll-req-stream")
    public Flux<MessageBoundary> getAllMessages() {
        log.debug("Invoking: getAll-req-stream");
        return messageService.getAllMessages();
    }
}

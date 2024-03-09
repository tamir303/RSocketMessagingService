package com.project.rsocketmessagingservice.controller;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.IdBoundary;
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

/**
 * Controller class for handling message-related operations using RSocket.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    /**
     * Creates a new message.
     * @param message The new message to create.
     * @return A Mono emitting the created message boundary.
     */
    @MessageMapping("publish-message-req-resp")
    public Mono<MessageBoundary> createMessage(@Payload NewMessageBoundary message) {
        log.debug("Invoking: publish-message-req-resp");
        return messageService.createMessage(message);
    }

    /**
     * Retrieves all messages.
     * @return A Flux emitting all message boundaries.
     */
    @MessageMapping("getAll-req-stream")
    public Flux<MessageBoundary> getAll() {
        log.debug("Invoking: getAll-req-stream");
        return messageService.getAll();
    }

    /**
     * Retrieves messages by their IDs.
     * @param ids Flux containing IDs of messages to retrieve.
     * @return A Flux emitting message boundaries.
     */
    @MessageMapping("getMessagesByIds-channel")
    public Flux<MessageBoundary> getMessagesByIds(
            Flux<IdBoundary> ids) {
        log.debug("Invoking: getMessagesByIds-channel");
        return messageService.getMessagesByIds(ids);
    }

    /**
     * Deletes all messages.
     * @return A Mono indicating the completion of the deletion operation.
     */
    @MessageMapping("deleteAll-fire-and-forget")
    public Mono<Void> deleteAll() {
        log.debug("Invoking: deleteAll-fire-and-forget");
        return messageService.deleteAll();
    }

    /**
     * Retrieves messages by their external references.
     * @param externalReferences Flux containing external references of messages to retrieve.
     * @return A Flux emitting message boundaries.
     */
    @MessageMapping("getMessagesByExternalReferences-channel")
    public Flux<MessageBoundary> getMessagesByExternalReferences(
            Flux<ExternalReferenceBoundary> externalReferences) {
        log.debug("Invoking: getMessagesByExternalReferences-channel");
        return messageService.getMessagesByExternalReferences(externalReferences);
    }
}

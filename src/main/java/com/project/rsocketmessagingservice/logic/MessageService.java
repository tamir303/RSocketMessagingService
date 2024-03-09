package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.IdBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for managing messages.
 */
public interface MessageService {

    /**
     * Creates a new message.
     * @param message The new message to create.
     * @return A Mono containing the created message.
     */
    Mono<MessageBoundary> createMessage(NewMessageBoundary message);

    /**
     * Retrieves all messages.
     * @return A Flux emitting all messages.
     */
    Flux<MessageBoundary> getAll();

    /**
     * Retrieves messages by their IDs.
     * @param ids A Flux of IDs.
     * @return A Flux emitting messages matching the provided IDs.
     */
    Flux<MessageBoundary> getMessagesByIds(Flux<IdBoundary> ids);

    /**
     * Deletes all messages.
     * @return A Mono representing the completion of the deletion operation.
     */
    Mono<Void> deleteAll();

    /**
     * Retrieves messages by their external references.
     * @param externalReferences A Flux of external references.
     * @return A Flux emitting messages with external references matching the provided ones.
     */
    Flux<MessageBoundary> getMessagesByExternalReferences(Flux<ExternalReferenceBoundary> externalReferences);
}

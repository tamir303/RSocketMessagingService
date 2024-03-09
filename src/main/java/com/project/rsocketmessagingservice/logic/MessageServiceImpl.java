package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.IdBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.dal.MessageCrud;
import com.project.rsocketmessagingservice.utils.ExternalRefConvertor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of the MessageService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageCrud messageCrud;

    /**
     * Creates a new message.
     * @param message The new message to create.
     * @return A Mono containing the created message.
     */
    @Override
    public Mono<MessageBoundary> createMessage(NewMessageBoundary message) {
        MessageBoundary messageBoundary = new MessageBoundary(message);
        messageBoundary.setMessageId(UUID.randomUUID().toString());
        messageBoundary.setPublishedTimestamp(LocalDateTime.now().toString());

        return Mono.just(messageBoundary.toEntity())
                .flatMap(messageCrud::save)
                .map(MessageBoundary::new)
                .log();
    }

    /**
     * Retrieves all messages.
     * @return A Flux emitting all messages.
     */
    @Override
    public Flux<MessageBoundary> getAll() {
        return messageCrud
                .findAll()
                .map(MessageBoundary::new)
                .log();
    }

    /**
     * Retrieves messages by their IDs.
     * @param ids A Flux of IDs.
     * @return A Flux emitting messages matching the provided IDs.
     */
    @Override
    public Flux<MessageBoundary> getMessagesByIds(Flux<IdBoundary> ids) {
        return ids.flatMap(id -> messageCrud.findById(id.getMessageId()))
                .map(MessageBoundary::new)
                .log();

    }

    /**
     * Deletes all messages.
     * @return A Mono representing the completion of the deletion operation.
     */
    @Override
    public Mono<Void> deleteAll() {
        return messageCrud
                .deleteAll()
                .log();
    }

    /**
     * Retrieves messages by their external references.
     * @param externalReferences A Flux of external references.
     * @return A Flux emitting messages with external references matching the provided ones.
     */
    @Override
    public Flux<MessageBoundary> getMessagesByExternalReferences(Flux<ExternalReferenceBoundary> externalReferences) {
        return externalReferences
                .map(ExternalRefConvertor::convertToEntity)
                .flatMap(messageCrud::findByExternalReferencesContaining)
                .map(MessageBoundary::new)
                .log();
    }
}

package com.project.rsocketmessagingservice.dal;

import com.project.rsocketmessagingservice.data.MessageEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Interface for accessing message data in the MongoDB repository in a reactive way.
 */
public interface MessageCrud extends ReactiveMongoRepository<MessageEntity, String> {

    /**
     * Retrieves messages by external references containing the specified reference.
     * @param ref The reference to search for within external references.
     * @return A Flux emitting message entities matching the specified reference.
     */
    Flux<MessageEntity> findByExternalReferencesContaining(String ref);
}

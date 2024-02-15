package com.project.rsocketmessagingservice.dal;

import com.project.rsocketmessagingservice.data.MessageEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MessageCrud extends ReactiveMongoRepository<MessageEntity, String> {
    Flux<MessageEntity> findByExternalReferencesContaining(String ref);
}

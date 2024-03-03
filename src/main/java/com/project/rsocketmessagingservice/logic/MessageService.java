package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.MessageBoundaries.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundaries.IdBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundaries.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundaries.NewMessageBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageService {
    Mono<MessageBoundary> createMessage(NewMessageBoundary message);
    Flux<MessageBoundary> getAll();
    Flux<MessageBoundary> getMessagesByIds(Flux<IdBoundary> ids);
    Mono<Void> deleteAll();
    Flux<MessageBoundary> getMessagesByExternalReferences(Flux<ExternalReferenceBoundary> externalReferences);
}

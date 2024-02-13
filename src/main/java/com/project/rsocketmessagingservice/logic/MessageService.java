package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.IdBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageService {
    Mono<MessageBoundary> createMessage(NewMessageBoundary message);
    Flux<MessageBoundary> getAll();
    Flux<MessageBoundary> getMessagesByIds(Flux<IdBoundary> ids);
    Mono<Void> deleteAll();
    Flux<MessageBoundary> getMessagesByExternalReferences(Flux<ExternalReferenceBoundary> externalReferences);
}

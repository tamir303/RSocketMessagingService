package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.IdBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageService {
    Mono<MessageBoundary> createMessage(NewMessageBoundary message);
    Flux<MessageBoundary> getAllMessages();
    Flux<MessageBoundary> getAllMessagesByIds(Flux<IdBoundary> ids);
    Mono<Void> clearAllMessages();
}

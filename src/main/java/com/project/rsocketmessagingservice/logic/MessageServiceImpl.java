package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.IdBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import com.project.rsocketmessagingservice.dal.MessageCrud;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageCrud messageCrud;
    @Override
    public Mono<MessageBoundary> createMessage(NewMessageBoundary message) {
        MessageBoundary messageBoundary = new MessageBoundary(message);
        messageBoundary.setMessageId(UUID.randomUUID().toString());
        messageBoundary.setPublishedTimestamp(LocalDateTime.now().toString());

        return Mono.just(messageBoundary)
                .map(MessageBoundary::toEntity)
                .flatMap(messageCrud::save)
                .map(MessageBoundary::new)
                .log();
    }

    @Override
    public Flux<MessageBoundary> getAllMessages() {
        return null;
    }

    @Override
    public Flux<MessageBoundary> getAllMessagesByIds(Flux<IdBoundary> ids) {
        return null;
    }

    @Override
    public Mono<Void> clearAllMessages() {
        return null;
    }
}

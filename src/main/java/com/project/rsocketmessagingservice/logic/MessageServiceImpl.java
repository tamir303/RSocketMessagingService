package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.MessageBoundaries.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundaries.IdBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundaries.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundaries.NewMessageBoundary;
import com.project.rsocketmessagingservice.dal.MessageCrud;
import com.project.rsocketmessagingservice.utils.ExternalRefConvertor;
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

        return Mono.just(messageBoundary.toEntity())
                .flatMap(messageCrud::save)
                .map(MessageBoundary::new)
                .log();
    }

    @Override
    public Flux<MessageBoundary> getAll() {
        return messageCrud
                .findAll()
                .map(MessageBoundary::new)
                .log();
    }

    @Override
    public Flux<MessageBoundary> getMessagesByIds(Flux<IdBoundary> ids) {
        return ids.flatMap(id -> messageCrud.findById(id.getMessageId()))
                .map(MessageBoundary::new)
                .log();

    }

    @Override
    public Mono<Void> deleteAll() {
        return messageCrud
                .deleteAll()
                .log();
    }

    @Override
    public Flux<MessageBoundary> getMessagesByExternalReferences(Flux<ExternalReferenceBoundary> externalReferences) {
        return externalReferences
                .map(ExternalRefConvertor::convertToEntity)
                .flatMap(messageCrud::findByExternalReferencesContaining)
                .map(MessageBoundary::new)
                .log();
    }
}

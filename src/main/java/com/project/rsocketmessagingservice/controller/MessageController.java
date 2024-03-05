//package com.project.rsocketmessagingservice.controller;
//
//import com.project.rsocketmessagingservice.boundary.MessageBoundaries.ExternalReferenceBoundary;
//import com.project.rsocketmessagingservice.boundary.MessageBoundaries.IdBoundary;
//import com.project.rsocketmessagingservice.boundary.MessageBoundaries.MessageBoundary;
//import com.project.rsocketmessagingservice.boundary.MessageBoundaries.NewMessageBoundary;
//import com.project.rsocketmessagingservice.logic.MessageService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Controller;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//public class MessageController {
//    private final MessageService messageService;
//
//    @MessageMapping("publish-message-req-resp")
//    public Mono<MessageBoundary> createMessage(
//            @Payload NewMessageBoundary message) {
//        log.debug("Invoking: publish-message-req-resp");
//        return messageService.createMessage(message);
//    }
//
//    @MessageMapping("getAll-req-stream")
//    public Flux<MessageBoundary> getAll() {
//        log.debug("Invoking: getAll-req-stream");
//        return messageService.getAll();
//    }
//
//    @MessageMapping("getMessagesByIds-channel")
//    public Flux<MessageBoundary> getMessagesByIds(
//            Flux<IdBoundary> ids) {
//        log.debug("Invoking: getMessagesByIds-channel");
//        return messageService.getMessagesByIds(ids);
//    }
//
//    @MessageMapping("deleteAll-fire-and-forget")
//    public Mono<Void> deleteAll() {
//        log.debug("Invoking: deleteAll-fire-and-forget");
//        return messageService.deleteAll();
//    }
//
//    @MessageMapping("getMessagesByExternalReferences-channel")
//    public Flux<MessageBoundary> getMessagesByExternalReferences(
//            Flux<ExternalReferenceBoundary> externalReferences) {
//        log.debug("Invoking: getMessagesByExternalReferences-channel");
//        return messageService.getMessagesByExternalReferences(externalReferences);
//    }
//}

package com.project.rsocketmessagingservice.controller;


import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.IdBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.NewMessageBoundary;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/rsocket/messages")
public class ClientMessageController {
    private RSocketRequester requester;
    private RSocketRequester.Builder requesterBuilder;
    private String rsocketHost;
    private int rsocketPort;

    @Autowired
    public void setRequesterBuilder(RSocketRequester.Builder requesterBuilder) {
        this.requesterBuilder = requesterBuilder;
    }

    @Value("${demoapp.client.rsocket.host:127.0.0.1}")
    public void setRsocketHost(String rsocketHost) {
        this.rsocketHost = rsocketHost;
    }

    @Value("${demoapp.client.rsocket.port:7000}")
    public void setRsocketPort(int rsocketPort) {
        this.rsocketPort = rsocketPort;
    }

    @PostConstruct
    public void init() {
        this.requester = this.requesterBuilder
                .tcp(rsocketHost, rsocketPort);
    }


    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<MessageBoundary> create(
            @RequestBody NewMessageBoundary message) {
        return this.requester
                .route("publish-message-req-resp")
                .data(message)
                .retrieveMono(MessageBoundary.class)
                .log();
    }

    @GetMapping(
            path = {"/byMessage/{ids}"},
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<MessageBoundary> getByMessageIds(
            @PathVariable("ids") String ids) {

        Flux<IdBoundary> idsFlux = Flux.fromArray(ids
                        .split(","))
                .map(IdBoundary::new);

        return this.requester
                .route("getMessagesByIds-channel")
                .data(idsFlux)
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    @PostMapping(
            path = {"/externalRef"},
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<MessageBoundary> getByMessageExternalRef(
            @RequestBody ExternalReferenceBoundary[] externalReferences) {

        Flux<ExternalReferenceBoundary> refFlux = Flux.fromArray(externalReferences);

        return this.requester
                .route("getMessagesByExternalReferences-channel")
                .data(refFlux)
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    @GetMapping(
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<MessageBoundary> getAll() {
        return this.requester
                .route("getAll-req-stream")
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    @DeleteMapping
    public Mono<Void> cleanup() {
        return this.requester
                .route("deleteAll-fire-and-forget")
                .send()
                .log();
    }
}
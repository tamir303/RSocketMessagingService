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

/**
 * Controller class to handle client-side messaging operations.
 */
@RestController
@RequestMapping(path = "/rsocket/messages")
public class ClientMessageController {

    private RSocketRequester requester;
    private RSocketRequester.Builder requesterBuilder;
    private String rsocketHost;
    private int rsocketPort;

    /**
     * Sets the RSocket requester builder.
     * @param requesterBuilder The RSocket requester builder.
     */
    @Autowired
    public void setRequesterBuilder(RSocketRequester.Builder requesterBuilder) {
        this.requesterBuilder = requesterBuilder;
    }

    /**
     * Sets the RSocket server host.
     * @param rsocketHost The RSocket server host.
     */
    @Value("${demoapp.client.rsocket.host:127.0.0.1}")
    public void setRsocketHost(String rsocketHost) {
        this.rsocketHost = rsocketHost;
    }

    /**
     * Sets the RSocket server port.
     * @param rsocketPort The RSocket server port.
     */
    @Value("${demoapp.client.rsocket.port:7000}")
    public void setRsocketPort(int rsocketPort) {
        this.rsocketPort = rsocketPort;
    }

    /**
     * Initializes the RSocket requester upon bean construction.
     */
    @PostConstruct
    public void init() {
        this.requester = this.requesterBuilder
                .tcp(rsocketHost, rsocketPort);
    }

    /**
     * Handles the creation of a new message.
     * @param message The new message to create.
     * @return A Mono emitting the created message boundary.
     */
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

    /**
     * Retrieves messages by their IDs.
     * @param ids The IDs of the messages to retrieve.
     * @return A Flux emitting the retrieved message boundaries.
     */
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

    /**
     * Retrieves messages by their external references.
     * @param externalReferences The external references to use for retrieval.
     * @return A Flux emitting the retrieved message boundaries.
     */
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

    /**
     * Retrieves all messages.
     * @return A Flux emitting all message boundaries.
     */
    @GetMapping(
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<MessageBoundary> getAll() {
        return this.requester
                .route("getAll-req-stream")
                .retrieveFlux(MessageBoundary.class)
                .log();
    }

    /**
     * Cleans up messages.
     * @return A Mono indicating the completion of the cleanup operation.
     */
    @DeleteMapping
    public Mono<Void> cleanup() {
        return this.requester
                .route("deleteAll-fire-and-forget")
                .send()
                .log();
    }
}
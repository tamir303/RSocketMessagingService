package com.project.rsocketmessagingservice.boundary;

import com.project.rsocketmessagingservice.data.MessageEntity;
import com.project.rsocketmessagingservice.utils.ExternalRefConvertor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a message boundary.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageBoundary {

    /**
     * The unique identifier of the message.
     */
    private String messageId;

    /**
     * The timestamp when the message was published.
     */
    private String publishedTimestamp;

    /**
     * The type of the message.
     */
    private String messageType;

    /**
     * The summary of the message.
     */
    private String summary;

    /**
     * The external references associated with the message.
     */
    private List<ExternalReferenceBoundary> externalReferences;

    /**
     * Additional details of the message.
     */
    private Map<String, Object> messageDetails;

    /**
     * Constructs a MessageBoundary object from a NewMessageBoundary object.
     *
     * @param newMessage The NewMessageBoundary object.
     */
    public MessageBoundary(NewMessageBoundary newMessage) {
        this.messageType = newMessage.getMessageType();
        this.summary = newMessage.getSummary();
        this.externalReferences = newMessage.getExternalReferences();
        this.messageDetails = newMessage.getMessageDetails();
    }

    /**
     * Constructs a MessageBoundary object from a MessageEntity object.
     *
     * @param entity The MessageEntity object.
     */
    public MessageBoundary(MessageEntity entity) {
        this.messageId = entity.getMessageId();
        this.publishedTimestamp = entity.getPublishedTimestamp();
        this.messageType = entity.getMessageType();
        this.summary = entity.getSummary();
        this.externalReferences = entity.getExternalReferences().stream().map(ExternalRefConvertor::convertToBoundary).toList();
        this.messageDetails = entity.getMessageDetails();
    }

    /**
     * Converts the MessageBoundary object to its corresponding MessageEntity object.
     *
     * @return The MessageEntity object.
     */
    public MessageEntity toEntity() {
        return new MessageEntity(
                this.messageId,
                this.publishedTimestamp,
                this.messageType,
                this.summary,
                this.externalReferences.stream().map(ExternalRefConvertor::convertToEntity).collect(Collectors.toSet()),
                this.messageDetails
        );
    }
}

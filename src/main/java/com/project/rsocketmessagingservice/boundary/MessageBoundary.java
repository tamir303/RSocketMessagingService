package com.project.rsocketmessagingservice.boundary;

import com.project.rsocketmessagingservice.data.MessageEntity;
import com.project.rsocketmessagingservice.utils.ExternalRefConvertor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageBoundary {
    private String messageId;
    private String publishedTimestamp;
    private String messageType;
    private String summary;
    private List<ExternalReferenceBoundary> externalReferences;
    private Map<String, Object> messageDetails;

    public MessageBoundary(NewMessageBoundary newMessage) {
        this.messageType = newMessage.getMessageType();
        this.summary = newMessage.getSummary();
        this.externalReferences = newMessage.getExternalReferences();
        this.messageDetails = newMessage.getMessageDetails();
    }

    public MessageBoundary(MessageEntity entity) {
        this.messageId = entity.getMessageId();
        this.publishedTimestamp = entity.getPublishedTimestamp();
        this.messageType = entity.getMessageType();
        this.summary = entity.getSummary();
        this.externalReferences = entity.getExternalReferences().stream().map(ExternalRefConvertor::convertToBoundary).toList();
        this.messageDetails = entity.getMessageDetails();
    }

    public MessageEntity toEntity() {
        return new MessageEntity(
                this.messageId,
                this.publishedTimestamp,
                this.messageType,
                this.summary,
                this.externalReferences.stream().map(ExternalRefConvertor::convertToEntity).toList(),
                this.messageDetails
        );
    }
}

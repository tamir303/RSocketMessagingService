package com.project.rsocketmessagingservice.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {
    @Id
    private String messageId;
    private String publishedTimestamp;
    private String messageType;
    private String summary;
    private List<String> externalReferences;
    private Map<String, Object> messageDetails;
}

package com.project.rsocketmessagingservice.boundary.MessageBoundaries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewMessageBoundary {
    private String messageType;
    private String summary;
    private List<ExternalReferenceBoundary> externalReferences;
    private Map<String, Object> messageDetails;
}

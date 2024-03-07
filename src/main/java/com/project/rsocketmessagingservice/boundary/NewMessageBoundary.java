package com.project.rsocketmessagingservice.boundary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewMessageBoundary {
    private String messageType;
    private String summary;
    private List<ExternalReferenceBoundary> externalReferences;
    private Map<String, Object> messageDetails;
}

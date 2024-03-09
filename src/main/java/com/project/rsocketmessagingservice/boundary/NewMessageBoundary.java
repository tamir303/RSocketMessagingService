package com.project.rsocketmessagingservice.boundary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Represents a new message boundary.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewMessageBoundary {

    /**
     * The type of the new message.
     */
    private String messageType;

    /**
     * The summary of the new message.
     */
    private String summary;

    /**
     * The external references associated with the new message.
     */
    private List<ExternalReferenceBoundary> externalReferences;

    /**
     * Additional details of the new message.
     */
    private Map<String, Object> messageDetails;
}

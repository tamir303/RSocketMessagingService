package com.project.rsocketmessagingservice.boundary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an external reference boundary.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalReferenceBoundary {

    /**
     * The service associated with the external reference.
     */
    private String service;

    /**
     * The identifier of the external service.
     */
    private String externalServiceId;
}

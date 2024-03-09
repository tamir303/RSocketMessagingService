package com.project.rsocketmessagingservice.boundary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an ID boundary.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdBoundary {

    /**
     * The identifier value.
     */
    private String messageId;
}

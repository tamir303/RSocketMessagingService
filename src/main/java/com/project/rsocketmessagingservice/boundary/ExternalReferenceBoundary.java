package com.project.rsocketmessagingservice.boundary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalReferenceBoundary {
    private String service;
    private String externalServiceId;
}

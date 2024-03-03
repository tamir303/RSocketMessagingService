package com.project.rsocketmessagingservice.utils;

import com.project.rsocketmessagingservice.boundary.MessageBoundaries.ExternalReferenceBoundary;

public class ExternalRefConvertor {
    public static String convertToEntity(ExternalReferenceBoundary externalReference) {
        return externalReference.getService() + ":" + externalReference.getExternalServiceId();
    }

    public static ExternalReferenceBoundary convertToBoundary(String externalReference) {
        String[] parts = externalReference.split(":");
        return new ExternalReferenceBoundary(parts[0], parts[1]);
    }
}

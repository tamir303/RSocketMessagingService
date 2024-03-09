/**
 * The ExternalRefConvertor class provides static methods to convert
 * ExternalReferenceBoundary objects to and from their string representation.
 */
package com.project.rsocketmessagingservice.utils;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;

public class ExternalRefConvertor {

    /**
     * Converts an ExternalReferenceBoundary object to its string representation.
     *
     * @param externalReference The ExternalReferenceBoundary object to convert.
     * @return The string representation of the ExternalReferenceBoundary.
     */
    public static String convertToEntity(ExternalReferenceBoundary externalReference) {
        return externalReference.getService() + ":" + externalReference.getExternalServiceId();
    }

    /**
     * Converts a string representation of an external reference to an ExternalReferenceBoundary object.
     *
     * @param externalReference The string representation of the external reference.
     * @return The ExternalReferenceBoundary object converted from the string.
     */
    public static ExternalReferenceBoundary convertToBoundary(String externalReference) {
        String[] parts = externalReference.split(":");
        return new ExternalReferenceBoundary(parts[0], parts[1]);
    }
}

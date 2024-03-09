package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a boundary object for location coordinates.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationBoundary {

    /**
     * The latitude coordinate of the location.
     */
    private double latitude;

    /**
     * The longitude coordinate of the location.
     */
    private double longitude;
}

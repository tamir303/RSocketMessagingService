package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a boundary object for device information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceBoundary {

    /**
     * The device details boundary object.
     */
    private DeviceDetailsBoundary device;
}

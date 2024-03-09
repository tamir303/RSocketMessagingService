package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a boundary object for a device ID.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceIdBoundary {

    /**
     * The unique identifier of the device.
     */
    private String deviceId;
}

package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the status of a device.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusBoundary {

    /**
     * Indicates whether the device is currently on or off.
     */
    Boolean isOn;

    /**
     * The brightness level of the device.
     */
    Integer brightness;

    /**
     * The RGB color values of the device.
     */
    List<Integer> colorRGB;

    /**
     * The current power consumption of the device in watts.
     */
    Integer currentPowerInWatts;
}

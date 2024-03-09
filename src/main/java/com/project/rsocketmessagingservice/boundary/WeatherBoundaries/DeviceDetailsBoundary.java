package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import com.project.rsocketmessagingservice.data.DeviceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents details of a device.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDetailsBoundary {

    /**
     * The unique identifier of the device.
     */
    String id;

    /**
     * The type of the device.
     */
    String type;

    /**
     * The sub-type of the device.
     */
    String subType;

    /**
     * The timestamp when the device was registered.
     */
    String registrationTimestamp;

    /**
     * The timestamp when the device was last updated.
     */
    String lastUpdateTimestamp;

    /**
     * The location of the device.
     */
    String location;

    /**
     * The power consumption of the device in watts.
     */
    Integer manufacturerPowerInWatts;

    /**
     * The status of the device.
     */
    StatusBoundary status;

    /**
     * Additional attributes of the device.
     */
    Map<String, Object> additionalAttributes;

    /**
     * Converts the boundary object to its corresponding entity object.
     *
     * @return The device entity object.
     */
    public DeviceEntity toEntity() {
        return new DeviceEntity(
                this.id,
                this.type,
                this.subType,
                this.registrationTimestamp,
                this.lastUpdateTimestamp,
                this.status.isOn,
                this.additionalAttributes
        );
    }

    /**
     * Checks if the device is a weather device.
     *
     * @return True if the device is of type "Weather", false otherwise.
     */
    public boolean isWeatherDevice() {
        return "Weather".equals(this.type); // Check if the type is "Weather"
    }
}

package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import com.project.rsocketmessagingservice.data.DeviceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceBoundary {
    DeviceDetailsBoundary device;

    public DeviceEntity toEntity() {
        return new DeviceEntity(
                this.device.id,
                this.device.type,
                this.device.subType,
                this.device.registrationTimestamp,
                this.device.lastUpdateTimestamp,
                this.device.status.isOn,
                this.device.additionalAttributes
        );
    }

    public boolean isWeatherDevice() {
        return "Weather".equals(this.device.type); // Check if the type is "Weather"
    }
}
}

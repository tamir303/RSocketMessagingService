package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import com.project.rsocketmessagingservice.data.DeviceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceBoundary {
    String id;
    String type;
    String subType;
    String registrationTimestamp;
    String lastUpdateTimestamp;
    String location;
    Integer manufacturerPowerInWatts;
    StatusBoundary status;
    Object additionalAttributes;

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

    public boolean isWeatherDevice() {
        return this.type.compareTo("Weather") == 0;
    }
}

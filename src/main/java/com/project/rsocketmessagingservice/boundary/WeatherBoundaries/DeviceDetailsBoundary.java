package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import com.project.rsocketmessagingservice.data.DeviceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.TreeMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDetailsBoundary {
    String id;
    String type;
    String subType;
    String registrationTimestamp;
    String lastUpdateTimestamp;
    String location;
    Integer manufacturerPowerInWatts;
    StatusBoundary status;
    Map<String, Object> additionalAttributes;

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
        return "Weather".equals(this.type); // Check if the type is "Weather"
    }
}

package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import com.project.rsocketmessagingservice.data.DeviceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
    Map<String, Object> additionalAttributes;

    public DeviceBoundary(DeviceEntity deviceEntity) {
        this.id = deviceEntity.getId();
        this.type = deviceEntity.getType();
        this.subType = deviceEntity.getSubType();
        this.registrationTimestamp = deviceEntity.getRegistrationTimestamp();
        this.lastUpdateTimestamp = deviceEntity.getLastUpdateTimestamp();
        this.additionalAttributes = deviceEntity.getAdditionalAttributes();
    }



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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new TreeMap<>();
        map.put("id", this.id);
        map.put("type", this.type);
        map.put("subType", this.subType);
        map.put("registrationTimestamp", this.registrationTimestamp);
        map.put("lastUpdateTimestamp", this.lastUpdateTimestamp);
        map.put("location", this.location);
        map.put("manufacturerPowerInWatts", this.manufacturerPowerInWatts);
        map.put("status", this.status);
        map.put("additionalAttributes", this.additionalAttributes);
        return map;
    }

}

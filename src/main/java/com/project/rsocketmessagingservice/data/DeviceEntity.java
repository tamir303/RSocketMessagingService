package com.project.rsocketmessagingservice.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceEntity {
    String id;
    String type;
    String subType;
    String registrationTimestamp;
    String lastUpdateTimestamp;
    Boolean isOn;
    Object additionalAttributes;

    public Map<String, Object> toMap() {
        return Map.of(
                "id", this.id,
                "type", this.type,
                "subType", this.subType,
                "registrationTimestamp", this.registrationTimestamp,
                "lastUpdateTimestamp", this.lastUpdateTimestamp,
                "isOn", this.isOn,
                "additionalAttributes", this.additionalAttributes
        );
    }
}

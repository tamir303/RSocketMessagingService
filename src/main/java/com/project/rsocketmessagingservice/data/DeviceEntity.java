package com.project.rsocketmessagingservice.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "devices")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceEntity {
    @Id
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

package com.project.rsocketmessagingservice.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Entity class representing a device in the MongoDB database.
 */
@Document(collection = "devices")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceEntity {

    @Id
    private String id;
    private String type;
    private String subType;
    private String registrationTimestamp;
    private String lastUpdateTimestamp;
    private Boolean isOn;
    private Map<String, Object> additionalAttributes;

    /**
     * Converts the device entity to a map representation.
     * @return A map containing device attributes.
     */
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

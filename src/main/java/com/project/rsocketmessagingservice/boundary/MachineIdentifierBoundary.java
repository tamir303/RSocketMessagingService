package com.project.rsocketmessagingservice.boundary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MachineIdentifierBoundary {

    private String houseUUID;
    private String machineUUID;

    public MachineIdentifierBoundary(String houseUUID, String machineUUID) {
        this.houseUUID = houseUUID;
        this.machineUUID = machineUUID;
    }
}

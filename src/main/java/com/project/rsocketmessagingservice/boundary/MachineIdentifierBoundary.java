package com.project.rsocketmessagingservice.boundary;

public class MachineIdentifierBoundary {

    private String houseUUID;
    private String machineUUID;

    public MachineIdentifierBoundary(String houseUUID, String machineUUID) {
        this.houseUUID = houseUUID;
        this.machineUUID = machineUUID;
    }

    public String getHouseUUID() {
        return houseUUID;
    }

    public void setHouseUUID(String houseUUID) {
        this.houseUUID = houseUUID;
    }

    public String getMachineUUID() {
        return machineUUID;
    }

    public void setMachineUUID(String machineUUID) {
        this.machineUUID = machineUUID;
    }

    @Override
    public String toString() {
        return "MachineIdentifierBoundary{" +
                "houseUUID='" + houseUUID + '\'' +
                ", machineUUID='" + machineUUID + '\'' +
                '}';
    }
}

package com.project.rsocketmessagingservice.data;

import com.project.rsocketmessagingservice.boundary.WeatherObjectBoundary;
import com.project.rsocketmessagingservice.utils.Location;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "weather")
public class WeatherEntity {

    @Id
    private String id;
    private String houseUUID;
    private String machineUUID;
    private double temperature;
    private Location location;
    private boolean machineIsOn;

    public WeatherEntity() {}

    public WeatherEntity(WeatherObjectBoundary weatherObjectBoundary) {
        this.houseUUID = weatherObjectBoundary.getMachine().getHouseUUID();
        this.machineUUID = weatherObjectBoundary.getMachine().getMachineUUID();
        this.temperature = weatherObjectBoundary.getTemperature();
        this.location = weatherObjectBoundary.getLocation();
        this.machineIsOn = weatherObjectBoundary.getMachineIsOn();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean getMachineIsOn() {
        return machineIsOn;
    }

    public void setMachineIsOn(boolean machineIsOn) {
        this.machineIsOn = machineIsOn;
    }

    @Override
    public String toString() {
        return "WeatherEntity{" +
                "id='" + id + '\'' +
                ", houseUUID='" + houseUUID + '\'' +
                ", machineUUID='" + machineUUID + '\'' +
                ", temperature=" + temperature +
                ", location=" + location +
                ", machineIsOn=" + machineIsOn +
                '}';
    }
}

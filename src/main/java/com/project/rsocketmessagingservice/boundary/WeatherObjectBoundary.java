package com.project.rsocketmessagingservice.boundary;

import com.project.rsocketmessagingservice.utils.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class WeatherObjectBoundary {

    private Location location;
    private MachineIdentifierBoundary machine;
    private double temperature;

    // Adding machine state (on/off)
    private boolean machineIsOn;

    // Adding recommendations
    private Map<String, Object> recommendations;

    public WeatherObjectBoundary(Location location, MachineIdentifierBoundary machine, double temperature) {
        this.location = location;
        this.machine = machine;
        this.temperature = temperature;
        this.machineIsOn = false;
        this.recommendations = new TreeMap<>();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public MachineIdentifierBoundary getMachine() {
        return machine;
    }

    public void setMachine(MachineIdentifierBoundary machine) {
        this.machine = machine;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setMachineIsOn(boolean machineIsOn) {
        this.machineIsOn = machineIsOn;
    }

    public boolean getMachineIsOn() {
        return this.machineIsOn;
    }

    public Map<String, Object> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(Map<String, Object> recommendations) {
        this.recommendations = recommendations;
    }

    @Override
    public String toString() {
        return "WeatherObjectBoundary{" +
                "location=" + location +
                ", machine=" + machine +
                ", temperature=" + temperature +
                ", machineIsOn=" + machineIsOn +
                ", recommendations=" + recommendations +
                '}';
    }
}

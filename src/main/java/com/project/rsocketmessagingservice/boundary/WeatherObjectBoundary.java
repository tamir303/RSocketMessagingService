package com.project.rsocketmessagingservice.boundary;

import com.project.rsocketmessagingservice.utils.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.TreeMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

}

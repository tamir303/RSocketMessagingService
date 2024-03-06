package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherObjectBoundary {

    private Location location;
    private MachineIdentifierBoundary machine;

    // Adding machine state (on/off)
    private boolean machineIsOn;

    // Adding recommendations
    private Map<String, Object> recommendations;
}

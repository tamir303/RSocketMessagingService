package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusBoundary {
    Boolean isOn;
    Integer brightness;
    List<Integer> colorRGB;
    Integer currentPowerInWatts;
}

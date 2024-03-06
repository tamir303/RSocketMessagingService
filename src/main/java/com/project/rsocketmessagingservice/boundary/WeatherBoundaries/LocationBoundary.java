package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationBoundary {
    private double lat;
    private double lng;
}

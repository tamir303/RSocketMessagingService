package com.project.rsocketmessagingservice.boundary.WeatherBoundaries;

import com.project.rsocketmessagingservice.data.DeviceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceBoundary {
    DeviceDetailsBoundary device;
}

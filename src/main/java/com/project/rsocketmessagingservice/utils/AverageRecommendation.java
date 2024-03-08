package com.project.rsocketmessagingservice.utils;

import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.project.rsocketmessagingservice.boundary.Enums.TemperatureInterval;
import com.project.rsocketmessagingservice.boundary.Enums.HumidityThreshold;
import com.project.rsocketmessagingservice.boundary.Enums.RainThreshold;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.project.rsocketmessagingservice.utils.OpenMeteoAPI.OptionsConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageRecommendation {
    private double averageTemperature_2m;
    private double averageRain;
    private double averageRelative_humidity_2m;
    private String recommendation; // Use enum type directly

    public Mono<MessageBoundary> updateAllAverages(Flux<Map<String, Object>> stringObjectMap) {
        if (stringObjectMap != null) {
            try {
//                averageTemperature_2m = Double.parseDouble(stringObjectMap.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT).toString());
//                averageRain = Double.parseDouble(stringObjectMap.get(RAIN_OPT).toString());
//                averageRelative_humidity_2m = Double.parseDouble(stringObjectMap.get(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT).toString());
            } catch (NumberFormatException | NullPointerException e) {
                System.err.println("Error while updating averages: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Map<String, Object> getRecommendation() {
        Map<String, Object> averageMap = new HashMap<>();
        averageMap.put("averageTemperature_2m", averageTemperature_2m);
        averageMap.put("averageRain", averageRain);
        averageMap.put("averageRelative_humidity_2m", averageRelative_humidity_2m);
        averageMap.put("temperatureRecommendation", TemperatureInterval.getInterval(averageTemperature_2m));
        averageMap.put("humidityRecommendation", HumidityThreshold.getThreshold(averageRelative_humidity_2m));
        averageMap.put("rainRecommendation", RainThreshold.getThreshold(averageRain));
        System.err.println(averageMap);
        return averageMap;
    }


}

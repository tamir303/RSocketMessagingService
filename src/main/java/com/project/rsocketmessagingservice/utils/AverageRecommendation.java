package com.project.rsocketmessagingservice.utils;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.utils.Enums.HumidityThreshold;
import com.project.rsocketmessagingservice.utils.Enums.RainThreshold;
import com.project.rsocketmessagingservice.utils.Enums.TemperatureInterval;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.project.rsocketmessagingservice.utils.OpenMeteoAPI.OptionsConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageRecommendation {
    private Map<String, Double> averagesParameters;
    private String recommendation; // Use enum type directly

    public Mono<MessageBoundary> updateAllAverages(Flux<Map<String, Object>> stringObjectMap) {
        AtomicReference<Integer> numHours = new AtomicReference<>(0);
        AtomicReference<Double> sumTemp = new AtomicReference<>(0.0);
        AtomicReference<Double> sumRain = new AtomicReference<>(0.0);
        AtomicReference<Double> sumHumidity = new AtomicReference<>(0.0);

        return stringObjectMap
                .flatMap(mapObj -> {
                    if (!mapObj.containsKey(TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT) ||
                            !mapObj.containsKey(RAIN_OPT) ||
                            !mapObj.containsKey(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT)) {
                        return Mono.error(new IllegalArgumentException("Some of the parameters for calculating the average recommendation are missing"));
                    }
                    numHours.updateAndGet(v -> v + 1);
                    sumTemp.updateAndGet(v -> v + (double) mapObj.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT));
                    sumRain.updateAndGet(v -> v + (double) mapObj.get(RAIN_OPT));
                    sumHumidity.updateAndGet(v -> v + (double) mapObj.get(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT));
                    return Mono.empty();
                })
                .then(Mono.defer(() -> {
                    averagesParameters.put(TEMPERATURE_2_METERS_ABOVE_SURFACE_MAX_OPT, sumTemp.get() / numHours.get());
                    averagesParameters.put(RAIN_OPT, sumRain.get() / numHours.get());
                    averagesParameters.put(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT, sumHumidity.get() / numHours.get());

                    String messageId = UUID.randomUUID().toString();
                    String timestamp = LocalDateTime.now().toString();
                    ExternalReferenceBoundary externalReference = new ExternalReferenceBoundary("WeatherService", "OpenMeteo_External_Service");
                    Map<String, Object> deviceDetailsMap = Collections.singletonMap("device", new );
                    MessageBoundary messageBoundary = MessageBoundary.builder()
                            .messageId(messageId)
                            .publishedTimestamp(timestamp)
                            .messageType("Weather recommendation")
                            .summary(HumidityThreshold.getThreshold(averagesParameters.get(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT))+
                                    "\n"+
                                    RainThreshold.getThreshold(averagesParameters.get(RAIN_OPT))+
                                    "\n"+
                                    TemperatureInterval.getInterval(averagesParameters.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT)))
                            .externalReferences(Collections.singletonList(externalReference))
                            .messageDetails.put("device",new TreeMap<String,Object>())
                            .build();

                    messageBoundary.set
                    return Mono.just(messageBoundary);
                }));
    }
}

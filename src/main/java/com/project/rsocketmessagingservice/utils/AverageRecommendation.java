package com.project.rsocketmessagingservice.utils;

import com.project.rsocketmessagingservice.boundary.ExternalReferenceBoundary;
import com.project.rsocketmessagingservice.boundary.MessageBoundary;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.DeviceDetailsBoundary;
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

/**
 * The AverageRecommendation class calculates the average parameters for weather data
 * and generates a recommendation based on those averages. It also creates a message
 * boundary object with the recommendation and other details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageRecommendation {
    /**
     * Map containing the average parameters for weather data.
     */
    private Map<String, Object> averagesParameters;

    /**
     * The generated weather recommendation based on the averages.
     */
    private String recommendation;

    /**
     * Updates all averages based on the provided Flux of weather data.
     *
     * @param stringObjectMap The Flux containing the weather data.
     * @return A Mono emitting a MessageBoundary object with the weather recommendation.
     */
    public Mono<MessageBoundary> updateAllAverages(Flux<Map<String, Object>> stringObjectMap) {
        this.averagesParameters = new TreeMap<>();
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
                    sumTemp.updateAndGet(v -> v + Double.parseDouble(mapObj.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT).toString()));
                    sumRain.updateAndGet(v -> v + Double.parseDouble(mapObj.get(RAIN_OPT).toString()));
                    sumHumidity.updateAndGet(v -> v + Double.parseDouble(mapObj.get(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT).toString()));
                    return Mono.empty();
                })
                .then(Mono.defer(() -> {
                    averagesParameters.put(TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT, sumTemp.get() / numHours.get());
                    averagesParameters.put(RAIN_OPT, sumRain.get() / numHours.get());
                    averagesParameters.put(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT, sumHumidity.get() / numHours.get());

                    String messageId = UUID.randomUUID().toString();
                    String timestamp = LocalDateTime.now().toString();
                    ExternalReferenceBoundary externalReference = new ExternalReferenceBoundary("WeatherService", "OpenMeteo_External_Service");

                    DeviceDetailsBoundary deviceDetailsBoundary = new DeviceDetailsBoundary();
                    deviceDetailsBoundary.setAdditionalAttributes(averagesParameters);
                    Map<String, Object> messageDetails = new TreeMap<>();
                    messageDetails.put("device", deviceDetailsBoundary);

                    recommendation = "";
                    recommendation += HumidityThreshold.getThreshold(
                            Double.parseDouble(averagesParameters.get(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT).toString())) + ", "
                            + RainThreshold.getThreshold(Double.parseDouble(averagesParameters.get(RAIN_OPT).toString())) + ", "
                            + TemperatureInterval.getInterval(Double.parseDouble(averagesParameters.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT).toString()));
                    MessageBoundary messageBoundary = MessageBoundary.builder()
                            .messageId(messageId)
                            .publishedTimestamp(timestamp)
                            .messageType("Weather recommendation")
                            .summary(recommendation)
                            .externalReferences(Collections.singletonList(externalReference))
                            .messageDetails(messageDetails).build();

                    return Mono.just(messageBoundary);
                }));
    }
}

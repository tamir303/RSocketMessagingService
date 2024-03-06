package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.LocationBoundary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.project.rsocketmessagingservice.utils.OpenMeteoAPI.ParamsConstants.*;
import static com.project.rsocketmessagingservice.utils.OpenMeteoAPI.OptionsConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenMeteoService implements OpenMeteoExtAPI {
    private WebClient webClient;

    @Override
    public Flux<Map<String, Object>> getWeeklyForecast(int days, LocationBoundary location) {
        if (days < MIN_DAYS_OPT || days > MAX_DAYS_OPT) {
            return Flux.error(new IllegalArgumentException("Number of days must be between %d and %d".formatted(MIN_DAYS_OPT,MAX_DAYS_OPT)));
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam(FORECAST_DAILY_PARAM, days)
                        .queryParam(LATITUDE_PARAM, location.getLatitude())
                        .queryParam(LONGITUDE_PARAM, location.getLongitude())
                        .queryParam(DAILY_PARAM,
                                TEMPERATURE_2_METERS_ABOVE_SURFACE_MAX_OPT,
                                TEMPERATURE_2_METERS_ABOVE_SURFACE_MIN_OPT,
                                SUNRISE_OPT,
                                SUNSET_OPT,
                                DAYLIGHT_DURATION_OPT,
                                SUNSHINE_DURATION_OPT,
                                RAIN_SUM_OPT,
                                SHOWERS_SUM_OPT,
                                SNOWFALL_SUM_OPT,
                                WIND_SPEED_10_METERS_ABOVE_SURFACE_MAX_OPT)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapMany(response -> {
                    Map<String, Object> dailyData = (Map<String, Object>) response.get(DAILY_PARAM);

                    List<String> dates = (List<String>) dailyData.get(TIME_OPT);
                    List<Double> temperatureMaxList = (List<Double>) dailyData.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_MAX_OPT);
                    List<Double> temperatureMinList = (List<Double>) dailyData.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_MIN_OPT);
                    List<String> sunriseList = (List<String>) dailyData.get(SUNRISE_OPT);
                    List<String> sunsetList = (List<String>) dailyData.get(SUNSET_OPT);
                    List<Double> daylightDurationList = (List<Double>) dailyData.get(DAYLIGHT_DURATION_OPT);
                    List<Double> sunshineDurationList = (List<Double>) dailyData.get(SUNSHINE_DURATION_OPT);
                    List<Double> rainSumList = (List<Double>) dailyData.get(RAIN_SUM_OPT);
                    List<Double> showersSumList = (List<Double>) dailyData.get(SHOWERS_SUM_OPT);
                    List<Double> snowfallSumList = (List<Double>) dailyData.get(SNOWFALL_SUM_OPT);
                    List<Double> windSpeedMaxList = (List<Double>) dailyData.get(WIND_SPEED_10_METERS_ABOVE_SURFACE_MAX_OPT);

                    return Flux.range(0, dates.size())
                            .map(index -> {
                                Map<String, Object> data = new HashMap<>();
                                data.put(DATE_OPT, dates.get(index));
                                data.put(TEMPERATURE_2_METERS_ABOVE_SURFACE_MAX_OPT, temperatureMaxList.get(index));
                                data.put(TEMPERATURE_2_METERS_ABOVE_SURFACE_MIN_OPT, temperatureMinList.get(index));
                                data.put(SUNRISE_OPT, sunriseList.get(index));
                                data.put(SUNSET_OPT, sunsetList.get(index));
                                data.put(DAYLIGHT_DURATION_OPT, daylightDurationList.get(index));
                                data.put(SUNSHINE_DURATION_OPT, sunshineDurationList.get(index));
                                data.put(RAIN_SUM_OPT, rainSumList.get(index));
                                data.put(SHOWERS_SUM_OPT, showersSumList.get(index));
                                data.put(SNOWFALL_SUM_OPT, snowfallSumList.get(index));
                                data.put(WIND_SPEED_10_METERS_ABOVE_SURFACE_MAX_OPT, windSpeedMaxList.get(index));
                                return data;
                            });
                });
    }

    @Override
    public Flux<Map<String, Object>> getDailyRecommendation(LocationBoundary location, int hours) {
        if (hours < MIN_HOURS_OPT || hours > MAX_HOURS_OPT) {
            return Flux.error(new IllegalArgumentException("Number of hours must be between %d and %d".formatted(MIN_HOURS_OPT,MAX_HOURS_OPT)));
        }
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam(FORECAST_HOURS_PARAM, hours)
                        .queryParam(LATITUDE_PARAM, location.getLatitude())
                        .queryParam(LONGITUDE_PARAM, location.getLongitude())
                        .queryParam(HOURLY_PARAM,
                                TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT,
                                RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT,
                                RAIN_OPT,
                                CLOUD_COVER_OPT,
                                WIND_SPEED_10_METERS_ABOVE_SURFACE_OPT,
                                SOIL_TEMPERATURE_0_CENTIMETERS_ABOVE_SURFACE_OPT,
                                IS_DAY_OPT)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapMany(response -> {
                    Map<String, Object> dailyData = (Map<String, Object>) response.get(HOURLY_PARAM);

                    List<String> dates = (List<String>) dailyData.get(TIME_OPT);
                    List<Double> temperatureList = (List<Double>) dailyData.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT);
                    List<Double> humidityList = (List<Double>) dailyData.get(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT);
                    List<String> rainList = (List<String>) dailyData.get(RAIN_OPT);
                    List<String> cloudList = (List<String>) dailyData.get(CLOUD_COVER_OPT);
                    List<Double> windSpeedList = (List<Double>) dailyData.get(WIND_SPEED_10_METERS_ABOVE_SURFACE_OPT);
                    List<Double> soilTempList = (List<Double>) dailyData.get(SOIL_TEMPERATURE_0_CENTIMETERS_ABOVE_SURFACE_OPT);
                    List<Double> isDayList = (List<Double>) dailyData.get(IS_DAY_OPT);

                    return Flux.range(0, dates.size())
                            .map(index -> {
                                Map<String, Object> data = new HashMap<>();
                                data.put(DATE_OPT, dates.get(index));
                                data.put(TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT, temperatureList.get(index));
                                data.put(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT, humidityList.get(index));
                                data.put(RAIN_OPT, rainList.get(index));
                                data.put(CLOUD_COVER_OPT, cloudList.get(index));
                                data.put(WIND_SPEED_10_METERS_ABOVE_SURFACE_OPT, windSpeedList.get(index));
                                data.put(SOIL_TEMPERATURE_0_CENTIMETERS_ABOVE_SURFACE_OPT, soilTempList.get(index));
                                data.put(IS_DAY_OPT, isDayList.get(index));
                                return data;
                            });
                });
    }

    @Value("${openmeteo.api.baseForecastUrl}")
    public void setRemoteUrl(String remoteUrl) {
        this.webClient = WebClient
                .create(remoteUrl);
    }
}

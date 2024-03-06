package com.project.rsocketmessagingservice.logic;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.LocationBoundary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.rsocketmessagingservice.utils.OpenMeteoAPI.OptionsConstants.*;
import static com.project.rsocketmessagingservice.utils.OpenMeteoAPI.ParamsConstants.*;

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
                        .queryParam(FORECAST_DAYS_PARAM, days)
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
                .bodyToMono(String.class)
                .flatMapMany(response -> {
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                    JsonObject dailyData = jsonResponse.getAsJsonObject(DAILY_PARAM);
                    List<String> dates = gson.fromJson(gson.toJson(dailyData.get(TIME_OPT)), new TypeToken<List<String>>() {}.getType());
                    List<Double> temperatureMaxList = gson.fromJson(gson.toJson(dailyData.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_MAX_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<Double> temperatureMinList = gson.fromJson(gson.toJson(dailyData.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_MIN_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<String> sunriseList = gson.fromJson(gson.toJson(dailyData.get(SUNRISE_OPT)), new TypeToken<List<String>>() {}.getType());
                    List<String> sunsetList = gson.fromJson(gson.toJson(dailyData.get(SUNSET_OPT)), new TypeToken<List<String>>() {}.getType());
                    List<Double> daylightDurationList = gson.fromJson(gson.toJson(dailyData.get(DAYLIGHT_DURATION_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<Double> sunshineDurationList = gson.fromJson(gson.toJson(dailyData.get(SUNSHINE_DURATION_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<Double> rainSumList = gson.fromJson(gson.toJson(dailyData.get(RAIN_SUM_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<Double> showersSumList = gson.fromJson(gson.toJson(dailyData.get(SHOWERS_SUM_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<Double> snowfallSumList = gson.fromJson(gson.toJson(dailyData.get(SNOWFALL_SUM_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<Double> windSpeedMaxList = gson.fromJson(gson.toJson(dailyData.get(WIND_SPEED_10_METERS_ABOVE_SURFACE_MAX_OPT)), new TypeToken<List<Double>>() {}.getType());

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
            return Flux.error(new IllegalArgumentException("Number of hours must be between %d and %d".formatted(MIN_HOURS_OPT, MAX_HOURS_OPT)));
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
                .bodyToMono(String.class)
                .flatMapMany(response -> {
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

                    JsonObject hourlyData = jsonResponse.getAsJsonObject(HOURLY_PARAM);
                    List<String> dates = gson.fromJson(gson.toJson(hourlyData.get(TIME_OPT)), new TypeToken<List<String>>() {}.getType());
                    List<Double> temperatureList = gson.fromJson(gson.toJson(hourlyData.get(TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<Double> humidityList = gson.fromJson(gson.toJson(hourlyData.get(RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<String> rainList = gson.fromJson(gson.toJson(hourlyData.get(RAIN_OPT)), new TypeToken<List<String>>() {}.getType());
                    List<String> cloudList = gson.fromJson(gson.toJson(hourlyData.get(CLOUD_COVER_OPT)), new TypeToken<List<String>>() {}.getType());
                    List<Double> windSpeedList = gson.fromJson(gson.toJson(hourlyData.get(WIND_SPEED_10_METERS_ABOVE_SURFACE_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<Double> soilTempList = gson.fromJson(gson.toJson(hourlyData.get(SOIL_TEMPERATURE_0_CENTIMETERS_ABOVE_SURFACE_OPT)), new TypeToken<List<Double>>() {}.getType());
                    List<Double> isDayList = gson.fromJson(gson.toJson(hourlyData.get(IS_DAY_OPT)), new TypeToken<List<Double>>() {}.getType());

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

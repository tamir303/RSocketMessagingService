package com.project.rsocketmessagingservice.logic;

import com.project.rsocketmessagingservice.boundary.WeatherBoundaries.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenMeteoService implements OpenMeteoExtAPI {
    private WebClient webClient;

    @Override
    public Flux<Map<String, Object>> getWeeklyForecast(int days , Location location) {
        if (days < 1 || days > 16) {
            return Flux.error(new IllegalArgumentException("Number of days must be between 1 and 16"));
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("forecast_days", days)
                        .queryParam("latitude",location.getLatitude())
                        .queryParam("longitude",location.getLongitude())
                        .queryParam("daily","temperature_2m_max","temperature_2m_min","sunrise","sunset","daylight_duration","sunshine_duration","rain_sum","showers_sum","snowfall_sum","wind_speed_10m_max")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapMany(response -> {
                    Map<String, Object> dailyData = (Map<String, Object>) response.get("daily");

                    List<String> dates = (List<String>) dailyData.get("time");
                    List<Double> temperatureMaxList = (List<Double>) dailyData.get("temperature_2m_max");
                    List<Double> temperatureMinList = (List<Double>) dailyData.get("temperature_2m_min");
                    List<String> sunriseList = (List<String>) dailyData.get("sunrise");
                    List<String> sunsetList = (List<String>) dailyData.get("sunset");
                    List<Double> daylightDurationList = (List<Double>) dailyData.get("daylight_duration");
                    List<Double> sunshineDurationList = (List<Double>) dailyData.get("sunshine_duration");
                    List<Double> rainSumList = (List<Double>) dailyData.get("rain_sum");
                    List<Double> showersSumList = (List<Double>) dailyData.get("showers_sum");
                    List<Double> snowfallSumList = (List<Double>) dailyData.get("snowfall_sum");
                    List<Double> windSpeedMaxList = (List<Double>) dailyData.get("wind_speed_10m_max");

                    return Flux.range(0, dates.size())
                            .map(index -> {
                                Map<String, Object> data = new HashMap<>();
                                data.put("date", dates.get(index));
                                data.put("temperature_2m_max", temperatureMaxList.get(index));
                                data.put("temperature_2m_min", temperatureMinList.get(index));
                                data.put("sunrise", sunriseList.get(index));
                                data.put("sunset", sunsetList.get(index));
                                data.put("daylight_duration", daylightDurationList.get(index));
                                data.put("sunshine_duration", sunshineDurationList.get(index));
                                data.put("rain_sum", rainSumList.get(index));
                                data.put("showers_sum", showersSumList.get(index));
                                data.put("snowfall_sum", snowfallSumList.get(index));
                                data.put("wind_speed_10m_max", windSpeedMaxList.get(index));
                                return data;
                            });
                });
    }

    @Override
    public Flux<Map<String, Object>> getDailyRecommendation(Location location) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("latitude",location.getLatitude())
                        .queryParam("longitude",location.getLongitude())
                        .queryParam("hourly","temperature_2m","relative_humidity_2m","rain","cloud_cover","wind_speed_10m","soil_temperature_0cm","is_day")
                        .queryParam("forecast_hours",24)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapMany(response -> {
                    Map<String, Object> dailyData = (Map<String, Object>) response.get("hourly");

                    List<String> dates = (List<String>) dailyData.get("time");
                    List<Double> temperatureList = (List<Double>) dailyData.get("temperature_2m");
                    List<Double> humidityList = (List<Double>) dailyData.get("relative_humidity_2m");
                    List<String> rainList = (List<String>) dailyData.get("rain");
                    List<String> cloudList = (List<String>) dailyData.get("cloud_cover");
                    List<Double> windSpeedList = (List<Double>) dailyData.get("wind_speed_10m");
                    List<Double> soilTempList = (List<Double>) dailyData.get("soil_temperature_0cm");
                    List<Double> isDayList = (List<Double>) dailyData.get("is_day");

                    return Flux.range(0, dates.size())
                            .map(index -> {
                                Map<String, Object> data = new HashMap<>();
                                data.put("date", dates.get(index));
                                data.put("temperature_2m", temperatureList.get(index));
                                data.put("relative_humidity_2m", humidityList.get(index));
                                data.put("rain", rainList.get(index));
                                data.put("cloud_cover", cloudList.get(index));
                                data.put("soil_temperature_0cm", windSpeedList.get(index));
                                data.put("wind_speed_10m", soilTempList.get(index));
                                data.put("is_day", isDayList.get(index));
                                return data;
                            });
                });
    }
    @Value("${openmeteo.api.baseForecastUrl}")
    public void setRemoteUrl (String remoteUrl) {
        this.webClient = WebClient
                .create(remoteUrl);
    }
}

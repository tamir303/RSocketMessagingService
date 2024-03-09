package com.project.rsocketmessagingservice.utils.OpenMeteoAPI;

/**
 * The OptionsConstants class provides constants for options used in OpenMeteoAPI requests.
 */
public class OptionsConstants {

    // Temperature options
    public static final String TEMPERATURE_2_METERS_ABOVE_SURFACE_OPT = "temperature_2m";
    public static final String TEMPERATURE_2_METERS_ABOVE_SURFACE_MAX_OPT = "temperature_2m_max";
    public static final String TEMPERATURE_2_METERS_ABOVE_SURFACE_MIN_OPT = "temperature_2m_min";

    // Humidity options
    public static final String RELATIVE_HUMIDITY_2_METERS_ABOVE_SURFACE_OPT = "relative_humidity_2m";

    // Rain options
    public static final String RAIN_OPT = "rain";

    // Cloud cover options
    public static final String CLOUD_COVER_OPT = "cloud_cover";

    // Wind speed options
    public static final String WIND_SPEED_10_METERS_ABOVE_SURFACE_OPT = "wind_speed_10m";
    public static final String WIND_SPEED_10_METERS_ABOVE_SURFACE_MAX_OPT = "wind_speed_10m_max";

    // Soil temperature options
    public static final String SOIL_TEMPERATURE_0_CENTIMETERS_ABOVE_SURFACE_OPT = "soil_temperature_0cm";

    // Other options
    public static final String IS_DAY_OPT = "is_day";
    public static final String TIME_OPT = "time";
    public static final String DATE_OPT = "date";
    public static final String SUNRISE_OPT = "sunrise";
    public static final String SUNSET_OPT = "sunset";
    public static final String DAYLIGHT_DURATION_OPT = "daylight_duration";
    public static final String SUNSHINE_DURATION_OPT = "sunshine_duration";
    public static final String RAIN_SUM_OPT = "rain_sum";
    public static final String SHOWERS_SUM_OPT = "showers_sum";
    public static final String SNOWFALL_SUM_OPT = "snowfall_sum";

    // Maximum and minimum values for days and hours
    public static int MAX_DAYS_OPT = 16;
    public static int MIN_DAYS_OPT = 1;
    public static int MAX_HOURS_OPT = 24;
    public static int MIN_HOURS_OPT = 1;
}

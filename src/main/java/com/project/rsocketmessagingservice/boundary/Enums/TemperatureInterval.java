package com.project.rsocketmessagingservice.boundary.Enums;

public enum TemperatureInterval {
    BELOW_FREEZING("Below Freezing", Double.NEGATIVE_INFINITY, 0.0),
    FREEZING_TO_COLD("Freezing to Cold", 0.0, 10.0),
    COLD_TO_MODERATE("Cold to Moderate", 10.0, 20.0),
    MODERATE_TO_WARM("Moderate to Warm", 20.0, 30.0),
    HOT("Hot", 30.0, 40.0),
    VERY_HOT("Very Hot", 40.0, Double.POSITIVE_INFINITY);

    private final String label;
    private final double lowerBound;
    private final double upperBound;

    private TemperatureInterval(String label, double lowerBound, double upperBound) {
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getLabel() {
        return label;
    }

    public static TemperatureInterval getInterval(double temperature) {
        for (TemperatureInterval interval : values()) {
            if (temperature >= interval.lowerBound && temperature < interval.upperBound) {
                return interval;
            }
        }
        // If temperature is not within any interval, return null or throw an exception
        return null;
    }
}

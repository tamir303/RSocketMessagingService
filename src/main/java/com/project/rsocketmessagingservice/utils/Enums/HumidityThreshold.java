package com.project.rsocketmessagingservice.utils.Enums;

public enum HumidityThreshold {
    LOW("Comfortable humidity", 0.0, 20.0),
    MODERATE("Moderate moisture", 20.0, 50.0),
    HIGH("High humidity and moisture", 50.0, 80.0),
    VERY_HIGH("Intolerant humidity and moisture", 80.0, 100.0);

    private final String label;
    private final double lowerBound;
    private final double upperBound;

    HumidityThreshold(String label, double lowerBound, double upperBound) {
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    public static String getThreshold(double humidity) {
        for (HumidityThreshold threshold : values()) {
            if (humidity >= threshold.lowerBound && humidity < threshold.upperBound) {
                return threshold.label;
            }
        }
        // If humidity is not within any threshold, return null or throw an exception
        return null;
    }
}

package com.project.rsocketmessagingservice.boundary.Enums;

public enum HumidityThreshold {
    LOW("Low", 0.0, 20.0),
    MODERATE("Moderate", 20.0, 50.0),
    HIGH("High", 50.0, 80.0),
    VERY_HIGH("Very High", 80.0, 100.0);

    private final String label;
    private final double lowerBound;
    private final double upperBound;

    private HumidityThreshold(String label, double lowerBound, double upperBound) {
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getLabel() {
        return label;
    }

    public static HumidityThreshold getThreshold(double humidity) {
        for (HumidityThreshold threshold : values()) {
            if (humidity >= threshold.lowerBound && humidity < threshold.upperBound) {
                return threshold;
            }
        }
        // If humidity is not within any threshold, return null or throw an exception
        return null;
    }
}

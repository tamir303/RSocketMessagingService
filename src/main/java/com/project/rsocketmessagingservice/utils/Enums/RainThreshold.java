package com.project.rsocketmessagingservice.utils.Enums;

public enum RainThreshold {
    NO_RAIN("No Rain", 0.0, 0.0),
    LIGHT_RAIN("Light Rain", 0.0, 0.2),
    MODERATE_RAIN("Consistent Rain", 0.2, 0.5),
    HEAVY_RAIN("Heavy Rain", 0.5, 1.0);

    private final String label;
    private final double lowerBound;
    private final double upperBound;

    private RainThreshold(String label, double lowerBound, double upperBound) {
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public static String getThreshold(double rain) {
        for (RainThreshold threshold : values()) {
            if (rain >= threshold.lowerBound && rain <= threshold.upperBound) {
                return threshold.label;
            }
        }
        // If rain is not within any threshold, return null or throw an exception
        return null;
    }
}

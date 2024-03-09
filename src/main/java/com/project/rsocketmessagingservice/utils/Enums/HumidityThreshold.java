package com.project.rsocketmessagingservice.utils.Enums;

/**
 * Enum representing different levels of humidity thresholds.
 */
public enum HumidityThreshold {
    LOW("Comfortable humidity", 0.0, 20.0),
    MODERATE("Moderate moisture", 20.0, 50.0),
    HIGH("High humidity and moisture", 50.0, 80.0),
    VERY_HIGH("Intolerant humidity and moisture", 80.0, 100.0);

    private final String label; // Label describing the humidity threshold level
    private final double lowerBound; // Lower bound of the humidity threshold
    private final double upperBound; // Upper bound of the humidity threshold

    /**
     * Constructor to initialize the enum constants with label, lower bound, and upper bound.
     *
     * @param label      Label describing the humidity threshold level.
     * @param lowerBound Lower bound of the humidity threshold.
     * @param upperBound Upper bound of the humidity threshold.
     */
    HumidityThreshold(String label, double lowerBound, double upperBound) {
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Gets the label of the humidity threshold based on the given humidity value.
     *
     * @param humidity Humidity value.
     * @return Label describing the humidity threshold level.
     */
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

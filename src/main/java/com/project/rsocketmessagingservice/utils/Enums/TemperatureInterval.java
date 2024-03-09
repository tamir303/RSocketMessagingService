package com.project.rsocketmessagingservice.utils.Enums;

/**
 * Enum representing different temperature intervals.
 */
public enum TemperatureInterval {
    BELOW_FREEZING("Below Freezing", Double.NEGATIVE_INFINITY, 0.0),
    FREEZING_TO_COLD("Freezing to Cold", 0.0, 10.0),
    COLD_TO_MODERATE("Cold to Moderate", 10.0, 20.0),
    MODERATE_TO_WARM("Moderate to Warm", 20.0, 30.0),
    HOT("Hot", 30.0, 40.0),
    VERY_HOT("Very Hot", 40.0, Double.POSITIVE_INFINITY);

    private final String label; // Label describing the temperature interval
    private final double lowerBound; // Lower bound of the temperature interval
    private final double upperBound; // Upper bound of the temperature interval

    /**
     * Constructor to initialize the enum constants with label, lower bound, and upper bound.
     *
     * @param label      Label describing the temperature interval.
     * @param lowerBound Lower bound of the temperature interval.
     * @param upperBound Upper bound of the temperature interval.
     */
    private TemperatureInterval(String label, double lowerBound, double upperBound) {
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Gets the label of the temperature interval based on the given temperature value.
     *
     * @param temperature Temperature value.
     * @return Label describing the temperature interval.
     */
    public static String getInterval(double temperature) {
        // Iterate through each enum constant to find the appropriate interval
        for (TemperatureInterval interval : values()) {
            // Check if the given temperature falls within the bounds of the current interval
            if (temperature >= interval.lowerBound && temperature < interval.upperBound) {
                return interval.label; // Return the label of the current interval
            }
        }
        // If temperature is not within any interval, return null or throw an exception
        return null;
    }
}
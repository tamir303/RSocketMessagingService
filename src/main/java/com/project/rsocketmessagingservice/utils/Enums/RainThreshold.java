package com.project.rsocketmessagingservice.utils.Enums;

/**
 * Enum representing different levels of rain thresholds.
 */
public enum RainThreshold {
    NO_RAIN("No Rain", 0.0, 0.0),
    LIGHT_RAIN("Light Rain", 0.0, 0.2),
    MODERATE_RAIN("Consistent Rain", 0.2, 0.5),
    HEAVY_RAIN("Heavy Rain", 0.5, 1.0);

    private final String label; // Label describing the rain threshold level
    private final double lowerBound; // Lower bound of the rain threshold
    private final double upperBound; // Upper bound of the rain threshold

    /**
     * Constructor to initialize the enum constants with label, lower bound, and upper bound.
     *
     * @param label      Label describing the rain threshold level.
     * @param lowerBound Lower bound of the rain threshold.
     * @param upperBound Upper bound of the rain threshold.
     */
    private RainThreshold(String label, double lowerBound, double upperBound) {
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Gets the label of the rain threshold based on the given rain value.
     *
     * @param rain Rain value.
     * @return Label describing the rain threshold level.
     */
    public static String getThreshold(double rain) {
        // Iterate through each enum constant to find the appropriate threshold
        for (RainThreshold threshold : values()) {
            // Check if the given rain falls within the bounds of the current threshold
            if (rain >= threshold.lowerBound && rain <= threshold.upperBound) {
                return threshold.label; // Return the label of the current threshold
            }
        }
        // If rain is not within any threshold, return null or throw an exception
        return null;
    }
}

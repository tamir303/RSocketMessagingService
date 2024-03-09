package com.project.rsocketmessagingservice.utils.exceptions;

/**
 * Utility class containing constant error messages used in the application.
 */
public class ConstantErrorMessages {
    /**
     * Error message indicating that no 'device' object was found in messageDetails.
     */
    public static final String NO_DEVICE_FOUND = "No 'device' object found in messageDetails.";

    /**
     * Error message indicating that the device is not a weather machine.
     */
    public static final String DEVICE_NOT_WEATHER_MACHINE = "Device is not a weather machine.";

    /**
     * Generates an error message for device not found with the provided ID.
     *
     * @param id ID of the device.
     * @return Error message indicating that the device was not found.
     */
    public static String DEVICE_NOT_FOUND(String id) {
        return "Device not found with ID: %s".formatted(id);
    }

    /**
     * Generates an error message for failing to remove the device with the specified reason.
     *
     * @param reason Reason for the failure to remove the device.
     * @return Error message indicating the failure to remove the device.
     */
    public static String FAILED_TO_REMOVE_DEVICE(String reason) {
        return "Failed to remove device with ID: %s".formatted(reason);
    }

    /**
     * Generates an error message for failing to process the device with the specified reason.
     *
     * @param reason Reason for the failure to process the device.
     * @return Error message indicating the failure to process the device.
     */
    public static String FAILED_TO_PROCESS_DEVICE(String reason) {
        return "Failed to process device with ID: %s".formatted(reason);
    }

    /**
     * Generates an error message for failing to convert an object with the specified reason.
     *
     * @param reason Reason for the failure to convert the object.
     * @return Error message indicating the failure to convert the object.
     */
    public static String FAILED_TO_CONVERT_OBJECT(String reason) {
        return "Failed to convert object: %s".formatted(reason);
    }
}

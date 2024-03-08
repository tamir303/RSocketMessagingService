package com.project.rsocketmessagingservice.utils.exceptions;

public class ConstantErrorMessages {
    public static final String NO_DEVICE_FOUND = "No 'device' object found in messageDetails.";
    public static final String DEVICE_NOT_WEATHER_MACHINE = "Device is not a weather machine.";
    public static String DEVICE_NOT_FOUND(String id) { return "Device not found with ID: %s".formatted(id); };
    public static String FAILED_TO_REMOVE_DEVICE(String reason) { return "Failed to remove device with ID: %s".formatted(reason); };
    public static String FAILED_TO_PROCESS_DEVICE(String reason) { return "Failed to process device with ID: %s".formatted(reason); };
    public static String FAILED_TO_CONVERT_OBJECT(String reason) { return "Failed to convert object: %s".formatted(reason); };
}

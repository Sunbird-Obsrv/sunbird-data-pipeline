package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class TelemetryLocationUpdaterConfig {

    private final String JOB_NAME = "TelemetryLocationUpdater";
    private static final String deviceProfileJobFlag = "device_profile_retrieved";
    private static final String deviceLocationJobFlag = "device_location_retrieved";
    private static final String derivedLocationJobFlag = "derived_location_retrieved";

    private String successTopic;
    private String failedTopic;
    private String malformedTopic;

    public TelemetryLocationUpdaterConfig(Config config) {
    	
        successTopic = config.get("output.success.topic.name", "telemetry.with_loation");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String malformedTopic() {
        return malformedTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }

    public static String getDeviceProfileJobFlag() { return deviceProfileJobFlag; }

    public static String getDeviceLocationJobFlag() {
        return deviceLocationJobFlag;
    }

    public static String getDerivedLocationJobFlag() {
        return derivedLocationJobFlag;
    }
}
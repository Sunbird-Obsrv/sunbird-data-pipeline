package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class TelemetryLocationUpdaterConfig {

    private final String JOB_NAME = "TelemetryLocationUpdater";
    private static final String deviceLocationJobFlag = "device_location_retrieved";
    private static final String userLocationJobFlag = "user_location_retrieved";

    private String successTopic;
    private String failedTopic;
    private String metricsTopic;
    private String malformedTopic;

    public TelemetryLocationUpdaterConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.with_loation");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        metricsTopic = config.get("output.metrics.topic.name", "telemetry.pipeline_metrics");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String metricsTopic() {
        return metricsTopic;
    }
    
    public String malformedTopic() {
    	return malformedTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }

    public static String getDeviceLocationJobFlag() {
        return deviceLocationJobFlag;
    }

    public static String getUserLocationJobFlag() {
        return userLocationJobFlag;
    }
}
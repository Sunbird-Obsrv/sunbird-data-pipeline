package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class ContentDeNormalizationConfig {

    private final String JOB_NAME = "DeDuplication";
    private static final String deviceDataJobFlag = "device_data_retrieved";
    private static final String userDataJobFlag = "user_data_retrieved";
    private static final String contentDataJobFlag = "content_data_retrieved";
    private static final String dialCodeDataJobFlag = "dialcode_data_retrieved";

    private String successTopic;
    private String failedTopic;
    private String malformedTopic;
    private String defaultChannel;
    private final String metricsTopic;


    public ContentDeNormalizationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.with_denorm");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
        defaultChannel = config.get("default.channel", "org.sunbird");
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

    public String defaultChannel() {
        return defaultChannel;
    }

    public String metricsTopic() {
        return metricsTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }

    public static String getDeviceLocationJobFlag() {
        return deviceDataJobFlag;
    }

    public static String getUserLocationJobFlag() {
        return userDataJobFlag;
    }

    public static String getContentLocationJobFlag() { return contentDataJobFlag; }

    public static String getDialCodeLocationJobFlag() { return dialCodeDataJobFlag; }
}
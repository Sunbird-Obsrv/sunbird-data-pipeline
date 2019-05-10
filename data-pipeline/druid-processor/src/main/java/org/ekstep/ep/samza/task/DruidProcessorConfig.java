package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class DruidProcessorConfig {

    private final String JOB_NAME = "DruidProcessor";
    private static final String deviceDataJobFlag = "device_data_retrieved";
    private static final String userDataJobFlag = "user_data_retrieved";
    private static final String contentDataJobFlag = "content_data_retrieved";
    private static final String dialCodeDataJobFlag = "dialcode_data_retrieved";

    private String successTopic;
    private String failedTopic;
    private String malformedTopic;
    private Integer ignorePeriodInMonths;
    private final String metricsTopic;
    private List<String> summaryFilterEvents;
    private String telemetrySchemaPath;
    private String summarySchemaPath;
    private String defaultSchemafile;
    private String telemetryEventsRouteTopic;
    private String summaryEventsRouteTopic;
    private String logEventsRouteTopic;


    public DruidProcessorConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.denorm");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
        ignorePeriodInMonths = config.getInt("telemetry.ignore.period.months", 6);
        List<String> defaultSummaryEvents = new ArrayList<>();
        defaultSummaryEvents.add("ME_WORKFLOW_SUMMARY");
        summaryFilterEvents = config.getList("summary.filter.events", defaultSummaryEvents);
        telemetrySchemaPath = config.get("telemetry.schema.path", "schemas/telemetry");
        summarySchemaPath = config.get("summary.schema.path", "schemas/summary");
        defaultSchemafile = config.get("event.schema.file","envelope.json");
        telemetryEventsRouteTopic = config.get("router.events.telemetry.route.topic", "events.telemetry");
        summaryEventsRouteTopic = config.get("router.events.summary.route.topic", "events.summary");
        logEventsRouteTopic = config.get("router.events.log.route.topic", "events.log");
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

    public String metricsTopic() {
        return metricsTopic;
    }

    public Integer ignorePeriodInMonths() {
        return ignorePeriodInMonths;
    }

    public String jobName() {
        return JOB_NAME;
    }

    public static String getJobFlag(String type) {
        switch (type) {
            case "device":
                return deviceDataJobFlag;
            case "user":
                return userDataJobFlag;
            case "content":
                return contentDataJobFlag;
            case "dialcode":
                return dialCodeDataJobFlag;
            default:
                return "";
        }
    }

    public static String getDeviceLocationJobFlag() {
        return deviceDataJobFlag;
    }

    public static String getUserLocationJobFlag() {
        return userDataJobFlag;
    }

    public static String getContentLocationJobFlag() { return contentDataJobFlag; }

    public static String getDialCodeLocationJobFlag() { return dialCodeDataJobFlag; }

    public List<String> getSummaryFilterEvents() {
        return this.summaryFilterEvents;
    }

    public String telemetrySchemaPath() {
        return telemetrySchemaPath;
    }

    public String summarySchemaPath() {
        return summarySchemaPath;
    }

    public String defaultSchemafile() {
        return defaultSchemafile;
    }

    public String telemetryEventsRouteTopic() {
        return telemetryEventsRouteTopic;
    }

    public String summaryEventsRouteTopic() {
        return summaryEventsRouteTopic;
    }

    public String logEventsRouteTopic() {
        return logEventsRouteTopic;
    }
}
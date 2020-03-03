package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.List;

public class DeNormalizationConfig {

    private final String JOB_NAME = "DeNormalization";
    private static final String deviceDataJobFlag = "device_data_retrieved";
    private static final String userDataJobFlag = "user_data_retrieved";
    private static final String contentDataJobFlag = "content_data_retrieved";
    private static final String dialCodeDataJobFlag = "dialcode_data_retrieved";
    private static final String collectionDataJobFlag = "collection_data_retrieved";
    private static String authorizationKey = "";

    private String successTopic;
    private String failedTopic;
    private String malformedTopic;
    private Integer ignorePeriodInMonths;
    private List<String> summaryFilterEvents;
    private String apiHost;
    private String dialCodeAPiEndPoint;


    public DeNormalizationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.denorm");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        ignorePeriodInMonths = config.getInt("telemetry.ignore.period.months", 6);
        List<String> defaultSummaryEvents = new ArrayList<String>();
        defaultSummaryEvents.add("ME_WORKFLOW_SUMMARY");
        summaryFilterEvents = config.getList("summary.filter.events", defaultSummaryEvents);
        apiHost = config.get("dialcode.api.host","https://qa.ekstep.in");
        dialCodeAPiEndPoint = config.get("dialcode.api.endpoint","/api/dialcode/v3/read/");
        authorizationKey = config.get("dialcode.api.authorizationkey","");
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
            case "collection":
                return collectionDataJobFlag;
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

    public static String getContentLocationJobFlag() {
        return contentDataJobFlag;
    }

    public static String getCollectionLocationJobFlag() { return collectionDataJobFlag; }
    public static String getDialCodeLocationJobFlag() {
        return dialCodeDataJobFlag;
    }

    public List<String> getSummaryFilterEvents() {
        return this.summaryFilterEvents;
    }
    public String getDialCodeAPIUrl(String key){
        return apiHost.concat(dialCodeAPiEndPoint).concat(key);
    }
    public static String getAuthorizationKey(){
        return authorizationKey;
    }

}
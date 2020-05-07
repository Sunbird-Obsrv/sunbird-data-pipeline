package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.List;

public class EventsRouterConfig {

    private final String JOB_NAME = "EventsRouter";

    private String failedTopic;
    private String duplicateTopic;
    private String telemetryEventsRouteTopic;
    private String summaryEventsRouteTopic;
    private String summaryRouteEvents;
    private String malformedTopic;
    private boolean dedupEnabled;
    private final int dupStore;
    private int expirySeconds;
    private List<String> excludedEids = new ArrayList<>();

    public EventsRouterConfig(Config config) {
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        duplicateTopic = config.get("output.duplicate.topic.name", "telemetry.duplicate");
        telemetryEventsRouteTopic = config.get("router.events.telemetry.route.topic", "events.telemetry");
        summaryRouteEvents = config.get("router.events.summary.route.events", "ME_WORKFLOW_SUMMARY");
        summaryEventsRouteTopic = config.get("router.events.summary.route.topic", "events.summary");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        dedupEnabled = config.getBoolean("dedup.enabled", true);
        dupStore = config.getInt("redis.database.duplicationstore.id", 8);
        expirySeconds = config.getInt("redis.database.key.expiry.seconds", 28800);
        if (!config.get("dedup.exclude.eids", "").isEmpty()) {
            excludedEids = config.getList("dedup.exclude.eids", new ArrayList<>());
        }
        else {
            excludedEids = new ArrayList<>();
        }
    }

    public String getTelemetryEventsRouteTopic() {
        return telemetryEventsRouteTopic;
    }


    public String getSummaryEventsRouteTopic() {
        return summaryEventsRouteTopic;
    }


    public String getSummaryRouteEvents() {
        return this.summaryRouteEvents;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String duplicateTopic() {
        return duplicateTopic;
    }

    public String malformedTopic() {
        return malformedTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }

    public Boolean isDedupEnabled() {
        return dedupEnabled;
    }

    public int dupStore() {
        return dupStore;
    }

    public int expirySeconds() {
        return expirySeconds;
    }

    public List<String> excludedEids() {
        return excludedEids;
    }
}
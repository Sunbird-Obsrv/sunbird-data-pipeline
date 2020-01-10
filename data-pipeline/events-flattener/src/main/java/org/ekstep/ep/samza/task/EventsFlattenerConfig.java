package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class EventsFlattenerConfig {

    private final String JOB_NAME = "SHARE_EVENTS_FLATTENER";

    private String successEventTopic;

    public EventsFlattenerConfig(Config config) {
        successEventTopic = config.get("output.success.topic.name", "telemetry.sink");
    }

    public String getSuccessTopic() {
        return successEventTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }

}
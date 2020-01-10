package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class EventsFlattenerConfig {

    private final String JOB_NAME = "SHARE_EVENTS_FLATTENER";

    private String successTopic;

    public EventsFlattenerConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.sink");
    }

    public String getSuccessTopic() {
        return successTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }

}
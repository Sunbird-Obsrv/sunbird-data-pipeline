package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class ObjectLifecycleManagementConfig {

    private String successTopic;
    private String failedTopic;

    public ObjectLifecycleManagementConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.objects");
        failedTopic = config.get("output.failed.topic.name", "telemetry.objects.fail");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }
}
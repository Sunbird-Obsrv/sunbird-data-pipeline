package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class ObjectDeNormalizationConfig {

    private String successTopic;
    private String failedTopic;

    public ObjectDeNormalizationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.objects.de_normalized");
        failedTopic = config.get("output.failed.topic.name", "telemetry.objects.de_normalized.fail");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }
}
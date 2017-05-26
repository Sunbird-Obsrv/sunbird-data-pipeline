package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class PrivateExhaustDeDuplicationConfig {

    private String successTopic;
    private String failedTopic;

    public PrivateExhaustDeDuplicationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.private_exhaust");
        failedTopic = config.get("output.failed.topic.name", "telemetry.private_exhaust.fail");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }
}
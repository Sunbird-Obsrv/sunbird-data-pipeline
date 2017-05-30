package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PublicExhaustDeDuplicationConfig {

    private String successTopic;
    private String failedTopic;
    private String duplicateTopic;

    public PublicExhaustDeDuplicationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.public_exhaust");
        failedTopic = config.get("output.failed.topic.name", "telemetry.public_exhaust.fail");
        duplicateTopic = config.get("output.duplicate.topic.name", "telemetry.public_exhaust.duplicate");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String duplicateTopic() {
        return duplicateTopic;
    }
}
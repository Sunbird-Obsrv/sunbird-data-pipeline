package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class EsRouterConfig {

    private String successTopic;
    private String failedTopic;


    public EsRouterConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.es-sink");
        failedTopic = config.get("output.failed.topic.name", "telemetry.es-sink.fail");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }
}

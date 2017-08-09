package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class EsIndexerSecondaryConfig {

    private final String failedTopic;
    private final String elasticSearchHosts;
    private final String elasticSearchPort;

    public EsIndexerSecondaryConfig(Config config) {
        failedTopic = config.get("output.failed.topic.name", "telemetry.es-indexer-secondary.fail");
        elasticSearchHosts = config.get("hosts.elastic_search","localhost");
        elasticSearchPort = config.get("port.elastic_search","9200");
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String[] esHost() {
        String[] hosts = elasticSearchHosts.split(",");
        return hosts;
    }

    public int esPort() {
        return Integer.parseInt(elasticSearchPort);
    }
}

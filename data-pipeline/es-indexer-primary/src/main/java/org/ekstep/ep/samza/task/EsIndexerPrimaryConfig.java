package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.List;

public class EsIndexerPrimaryConfig {

    private final String failedTopic;
    private final String elasticSearchHosts;
    private final String elasticSearchPort;

    public EsIndexerPrimaryConfig(Config config) {
        failedTopic = config.get("output.failed.topic.name", "telemetry.es-indexer-primary.fail");
        elasticSearchHosts = config.get("hosts.elastic_search","localhost");
        elasticSearchPort = config.get("port.elastic_search","9200");
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String[] esHosts() {
        String[] hosts = elasticSearchHosts.split(",");
        return hosts;
    }

    public int esPort() {
        return Integer.parseInt(elasticSearchPort);
    }

}

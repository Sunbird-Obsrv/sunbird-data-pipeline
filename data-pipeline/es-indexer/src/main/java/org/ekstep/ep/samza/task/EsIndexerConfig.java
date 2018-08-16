package org.ekstep.ep.samza.task;

import java.util.Map;

import org.apache.samza.config.Config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EsIndexerConfig {

    private final String failedTopic;
    private final String metricsTopic;
    private final String elasticSearchHosts;
    private final String elasticSearchPort;
    private final Map<String, String> indexMapping;
    private String jobName;
    private String primaryIndex;
    private String summaryIndex;
    private String summaryCumulativeIndex;
    private String failedTelemetryIndex;

	public EsIndexerConfig(Config config) {
    	
        failedTopic = config.get("output.failed.topic.name", "telemetry.indexer.failed");
        primaryIndex = config.get("indexer.primary.index", "telemetry");
        summaryIndex = config.get("indexer.summary.index", "summary");
        summaryCumulativeIndex = config.get("indexer.summary.cumulative.index", "summary-cumulative");
        failedTelemetryIndex = config.get("indexer.failed.index", "failed-telemetry");
        metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
        elasticSearchHosts = config.get("hosts.elastic_search","localhost");
        elasticSearchPort = config.get("port.elastic_search","9200");
        jobName = config.get("output.metrics.job.name", "EsIndexer");
        
        String indexMappingStr = config.get("indexer.stream.mapping", "{\"telemetry.denormalized\":\"default\",\"telemetry.log\":\"backend\",\"telemetry.failed\":\"failed-telemetry\"}");
        this.indexMapping = new Gson().fromJson(indexMappingStr, new TypeToken<Map<String, String>>() {}.getType());
    }
    
    public Map<String, String> indexMapping() {
    	return indexMapping;
    }
    
    public String primaryIndex() {
    	return primaryIndex;
    }
    
    public String derivedIndex() {
    	return summaryIndex;
    }
    
    public String derivedCumulativeIndex() {
    	return summaryCumulativeIndex;
    }

    public String failedTelemetryIndex() {
	    return failedTelemetryIndex;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String metricsTopic() {
        return metricsTopic;
    }

    public String[] esHosts() {
        String[] hosts = elasticSearchHosts.split(",");
        return hosts;
    }

    public int esPort() {
        return Integer.parseInt(elasticSearchPort);
    }

    public String jobName() {
        return jobName;
    }
}

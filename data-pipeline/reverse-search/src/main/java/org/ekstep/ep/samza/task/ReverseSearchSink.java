package org.ekstep.ep.samza.task;

import java.util.Map;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.system.Event;
import org.ekstep.ep.samza.util.Configuration;

public class ReverseSearchSink extends BaseSink {

	private JobMetrics metrics;
	private Configuration config;

	public ReverseSearchSink(MessageCollector collector, JobMetrics metrics, Configuration config) {
		super(collector);
		this.metrics = metrics;
		this.config = config;
	}

	public void sendToSuccessTopic(Event event) {
		toTopic(config.getSuccessTopic(), event.getObjectID(config.objectTaxonomy()), event.getMap());
		metrics.incSuccessCounter();
	}

	public void sendToErrorTopic(Map<String, Object> event) {
		toTopic(config.getFailedTopic(), event.get("mid") != null ? event.get("mid").toString() : null, event);
		metrics.incErrorCounter();
	}
	
	public void sendToMalformedTopic(String message) {
		toTopic(config.getFailedTopic(), null, message);
		metrics.incErrorCounter();
	}

}

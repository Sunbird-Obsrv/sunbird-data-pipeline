package org.ekstep.ep.samza.core;

import java.util.Map;

import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;

public class BaseSink {

	private MessageCollector collector;
	protected JobMetrics metrics;
	
	public BaseSink(MessageCollector collector) {
		this.collector = collector;
	}

	public BaseSink(MessageCollector collector, JobMetrics metrics) {
		this.collector = collector;
		this.metrics = metrics;
	}
	
	public void toTopic(String topic, String mid, String message) {
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", topic), mid, message));
	}
	
	public void toTopic(String topic, String mid, Map<String, Object> message) {
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", topic), mid, message));
	}

}

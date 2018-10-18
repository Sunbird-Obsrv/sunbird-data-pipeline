package org.ekstep.ep.samza.core;

import java.util.Map;

import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;

import static java.text.MessageFormat.format;

public class BaseSink {

	private MessageCollector collector;
	private static Logger LOGGER = new Logger(BaseSink.class);
	
	public BaseSink(MessageCollector collector) {
		this.collector = collector;
	}
	
	public void toTopic(String topic, String mid, String message) {
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", topic), mid, message));
	}
	
	public void toTopic(String topic, String mid, Map<String, Object> message) {
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", topic), mid, message));
	}
}

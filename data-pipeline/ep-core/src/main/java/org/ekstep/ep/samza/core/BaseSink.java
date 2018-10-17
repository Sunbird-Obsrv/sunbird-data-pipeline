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
		try {
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", topic), mid, message));
		} catch (Exception ex) {
			LOGGER.error(null, format(
					"PASSING EVENT MID {0} THROUGH FROM {1} TOPIC. EXCEPTION: ", mid, topic), ex.getMessage());
		}
	}
	
	public void toTopic(String topic, String mid, Map<String, Object> message) {
		try {
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", topic), mid, message));
		} catch (Exception ex) {
			LOGGER.error(null, format(
					"PASSING EVENT MID {0} THROUGH FROM {1} TOPIC. EXCEPTION: ", mid, topic), ex.getMessage());
		}
	}
}

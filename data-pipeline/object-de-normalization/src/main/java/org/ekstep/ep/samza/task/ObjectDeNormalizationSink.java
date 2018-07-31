package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class ObjectDeNormalizationSink extends BaseSink {

	private JobMetrics metrics;
	private ObjectDeNormalizationConfig config;

	public ObjectDeNormalizationSink(MessageCollector collector, JobMetrics metrics,
			ObjectDeNormalizationConfig config) {
		super(collector);
		this.metrics = metrics;
		this.config = config;
	}

	public void toSuccessTopic(Event event) {
		toTopic(config.successTopic(), event.mid(), event.getMap());
		metrics.incSuccessCounter();
	}

	public void toErrorTopic(Event event) {
		toTopic(config.failedTopic(), event.mid(), event.getMap());
		metrics.incErrorCounter();
	}

}

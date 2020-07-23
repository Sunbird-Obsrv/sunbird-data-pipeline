package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;

public class TelemetryExtractorSink extends BaseSink {

	private TelemetryExtractorConfig config;

	public TelemetryExtractorSink(MessageCollector collector, JobMetrics metrics, TelemetryExtractorConfig config) {
		super(collector, metrics);
		this.config = config;
	}
	
	public void toSuccessTopic(String message) {
		metrics.incSuccessCounter();
		toTopic(config.successTopic(), null, message);
	}
	
	public void toAssessTopic(String message) {
    metrics.incAssessRouteSuccessCounter();
    toTopic(config.assessTopic(), null, message);
  }
	
	public void toErrorTopic(String message) {
		metrics.incErrorCounter();
		toTopic(config.errorTopic(), null, message);
	}

	public void sinkBatchErrorEvents(String message) {
		metrics.incBatchErrorCounter();
		toTopic(config.errorTopic(), null, message);
	}

	public void toDuplicateTopic(String message)
	{
		metrics.incDuplicateCounter();
		toTopic(config.errorTopic(), null, message);
	}

}

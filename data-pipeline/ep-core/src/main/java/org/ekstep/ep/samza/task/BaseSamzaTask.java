package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;
import org.ekstep.ep.samza.core.JobMetrics;

abstract class BaseSamzaTask implements StreamTask, InitableTask, WindowableTask {
	
	protected JobMetrics metrics;
	private String metricsTopic;
	
	public BaseSamzaTask() {
		
	}
	
	public void initTask(Config config, JobMetrics metrics) {
		this.metrics = metrics;
		this.metricsTopic = config.get("output.metrics.topic.name", "telemetry.pipeline_metrics");
	}
	

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

		String mEvent = metrics.collect();
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", this.metricsTopic), mEvent));
		this.metrics.clear();
	}
}

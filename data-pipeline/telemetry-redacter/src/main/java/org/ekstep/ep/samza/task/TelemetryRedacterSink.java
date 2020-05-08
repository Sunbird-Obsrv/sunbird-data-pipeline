package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class TelemetryRedacterSink extends BaseSink {

  private TelemetryRedacterConfig config;

  public TelemetryRedacterSink(MessageCollector collector, JobMetrics metrics, TelemetryRedacterConfig config) {
    super(collector, metrics);
    this.config = config;
  }

  public void toRedactedRoute(Event event) {
    toTopic(config.getRedactedRouteTopic(), event.did(), event.getJson());
    metrics.incSuccessCounter();
  }
  
  public void toNonRedactedRoute(Event event) {
    toTopic(config.getNonRedactedRouteTopic(), event.did(), event.getJson());
    metrics.incAssessRouteSuccessCounter();
  }

  public void toErrorTopic(String message) {
    toTopic(config.failedTopic(), null, message);
    metrics.incFailedCounter();
  }

}

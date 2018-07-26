package org.ekstep.ep.es_router_secondary.task;

import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.es_router.EsRouter;
import org.ekstep.ep.es_router_secondary.domain.Event;
import org.ekstep.ep.es_router_secondary.util.EsRouterConfig;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.reader.NullableValue;

import java.io.IOException;
import java.util.Map;

public class EsRouterSecondaryTask implements StreamTask, InitableTask, WindowableTask {
  static Logger LOGGER = new Logger(Event.class);
  private EsRouterConfig config;
  private EsRouter esRouter;
  private JobMetrics metrics;


  @Override
  public void init(Config config, TaskContext context) throws Exception {
    try {
      this.config = new EsRouterConfig(config);
      metrics = new JobMetrics(context,this.config.jobName());
      esRouter = new EsRouter(this.config.additionConfigPath());
      esRouter.loadConfiguration();
    } catch (IOException e) {
      LOGGER.error("","Not able to load additional configuration for secondary es router",e);
    }
  }

  @Override
  public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
    try {
      Map<String, Object> eventData = (Map<String, Object>) envelope.getMessage();
      SystemStream systemStream = envelope.getSystemStreamPartition().getSystemStream();
      Event event = new Event(eventData, systemStream);
      processEvent(collector, event);
    } catch (Exception e) {
      LOGGER.error("","Error creating the event. Event will be lost.",e);
    }
  }

  private void processEvent(MessageCollector collector, Event event) {
    try {
      event.process(esRouter);
      sendToSuccess(collector,event);
    } catch (Exception e) {
      NullableValue<String> eventId = event.id();
      String eventIdValue = eventId.isNull() ? "" : eventId.value();
      LOGGER.error(eventIdValue,"Error while processing event", e);
      sendToFailed(collector,event);
    }
  }

  @Override
  public void window(MessageCollector collector, TaskCoordinator taskCoordinator) throws Exception {
    String mEvent = metrics.collect();
    Map<String,Object> mEventMap = new Gson().fromJson(mEvent,Map.class);
    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEventMap));
    metrics.clear();
  }

  private void sendToSuccess(MessageCollector collector, Event event) {
    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.successTopic()), event.getMap()));
    metrics.incSuccessCounter();
  }

  private void sendToFailed(MessageCollector collector, Event event) {
    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.failedTopic()), event.getMap()));
    metrics.incErrorCounter();
  }
}

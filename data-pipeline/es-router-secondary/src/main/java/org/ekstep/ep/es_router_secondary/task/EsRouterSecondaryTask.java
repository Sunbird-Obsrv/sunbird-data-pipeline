package org.ekstep.ep.es_router_secondary.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.es_router.EsRouter;
import org.ekstep.ep.es_router_secondary.domain.Event;
import org.ekstep.ep.es_router_secondary.util.EsRouterConfig;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;

import java.io.IOException;
import java.util.Map;

public class EsRouterSecondaryTask implements StreamTask, InitableTask {
  static Logger LOGGER = new Logger(Event.class);
  private EsRouterConfig config;
  private EsRouter esRouter;


  @Override
  public void init(Config config, TaskContext context) throws Exception {
    try {
      this.config = new EsRouterConfig(config);
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


  private void sendToSuccess(MessageCollector collector, Event event) {
    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.successTopic()), event.getMap()));
  }

  private void sendToFailed(MessageCollector collector, Event event) {
    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.failedTopic()), event.getMap()));
  }
}

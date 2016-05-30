package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.task.fixtures.EventFixture;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PartnerDataRouterTaskTest {

  @Test
  public void shouldNotGetTheTopicFromEventWhenTopicDoesNotBelongToPartner() throws Exception {
    Event eventMock = mock(Event.class);
    PartnerDataRouterTask task = new PartnerDataRouterTaskStub(eventMock, false);
    IncomingMessageEnvelope envelopeMock = mock(IncomingMessageEnvelope.class);
    HashMap<String, Object> message = new HashMap<String, Object>();
    stub(envelopeMock.getMessage()).toReturn(message);
    stub(eventMock.belongsToAPartner()).toReturn(false);

    task.process(envelopeMock, null, null);

    verify(eventMock, never()).routeTo();
  }

  @Test
  public void shouldGetTheTopicFromEventWhenTopicBelongToPartner() throws Exception {
    Event eventMock = mock(Event.class);
    PartnerDataRouterTask task = new PartnerDataRouterTaskStub(eventMock, true);
    IncomingMessageEnvelope envelopeMock = mock(IncomingMessageEnvelope.class);
    HashMap<String, Object> message = new HashMap<String, Object>();
    MetricsRegistry metricsRegistryMock = mock(MetricsRegistry.class);
    Counter counterMock = mock(Counter.class);
    Config configMock = Mockito.mock(Config.class);
    TaskContext contextMock = Mockito.mock(TaskContext.class);
    stub(envelopeMock.getMessage()).toReturn(message);
    stub(eventMock.belongsToAPartner()).toReturn(true);
    Map<String, Object> eventData = EventFixture.CreateProfile();
    stub(eventMock.getData()).toReturn(eventData);
    MessageCollector collectorMock = mock(MessageCollector.class);
    when(contextMock.getMetricsRegistry()).thenReturn(metricsRegistryMock);
    when(metricsRegistryMock.newCounter("org.ekstep.ep.samza.task.PartnerDataRouterTaskStub", "message-count"))
        .thenReturn(counterMock);

    task.init(configMock, contextMock);
    task.process(envelopeMock, collectorMock, null);

    verify(eventMock).routeTo();
    verify(collectorMock).send(any(OutgoingMessageEnvelope.class));
  }

  @Test
  public void shouldCleanThePartnerEventBeforeRoutingToCorrespondingTopic() throws Exception {
    Event eventMock = mock(Event.class);
    PartnerDataRouterTask task = new PartnerDataRouterTaskStub(eventMock, true);
    IncomingMessageEnvelope envelopeMock = mock(IncomingMessageEnvelope.class);
    HashMap<String, Object> message = new HashMap<String, Object>();
    MetricsRegistry metricsRegistryMock = mock(MetricsRegistry.class);
    Counter counterMock = mock(Counter.class);
    Config configMock = Mockito.mock(Config.class);
    TaskContext contextMock = Mockito.mock(TaskContext.class);
    stub(envelopeMock.getMessage()).toReturn(message);
    stub(eventMock.belongsToAPartner()).toReturn(true);
    Map<String, Object> eventData = EventFixture.CreateProfile();
    stub(eventMock.getData()).toReturn(eventData);
    MessageCollector collectorMock = mock(MessageCollector.class);
    when(contextMock.getMetricsRegistry()).thenReturn(metricsRegistryMock);
    when(metricsRegistryMock.newCounter("org.ekstep.ep.samza.task.PartnerDataRouterTaskStub", "message-count"))
        .thenReturn(counterMock);

    task.init(configMock, contextMock);
    task.process(envelopeMock, collectorMock, null);

    Map<String, Object> udata = (Map<String, Object>) eventData.get("udata");
    assertThat(udata, not(hasKey("is_group_user")));
    assertThat(udata, not(hasKey("handle")));
    assertThat(udata, not(hasKey("gender")));
  }

//    @Test(expected = PartnerTopicNotPresentException.class)
//    public void shouldThrowExceptionWhenTopicDoesNotExists() throws Exception {
//        Event eventMock = mock(Event.class);
//        PartnerDataRouterTask task = new PartnerDataRouterTaskStub(eventMock,false);
//        IncomingMessageEnvelope envelopeMock= mock(IncomingMessageEnvelope.class);
//        HashMap<String, Object> message = new HashMap<String, Object>();
//        stub(envelopeMock.getMessage()).toReturn(message);
//        stub(eventMock.belongsToAPartner()).toReturn(true);
//
//        task.process(envelopeMock, null, null);
//    }


}

class PartnerDataRouterTaskStub extends PartnerDataRouterTask {
  private Event event;

  public PartnerDataRouterTaskStub(Event event, Boolean topicExists) {
    this.event = event;
  }

  @Override
  protected Event getEvent(Map<String, Object> message) {
    return event;
  }
}
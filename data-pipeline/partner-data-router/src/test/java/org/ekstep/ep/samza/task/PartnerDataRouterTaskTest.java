package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PartnerDataRouterTaskTest {
    private final String EVENTS_TO_SKIP = "ME_.*";
    private MessageCollector collectorMock;
    private Config configMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistryMock;
    private IncomingMessageEnvelope envelopMock;
    private PartnerDataRouterTask partnerDataRouterTask;
    private Counter counterMock;

    @Before
    public void setUp() {
        collectorMock = mock(MessageCollector.class);
        contextMock = mock(TaskContext.class);
        envelopMock = mock(IncomingMessageEnvelope.class);
        metricsRegistryMock = mock(MetricsRegistry.class);
        counterMock = mock(Counter.class);

        configMock = mock(Config.class);
        stub(configMock.get("output.success.topic.prefix")).toReturn("partner");
        stub(configMock.get("events.to.skip", "")).toReturn(EVENTS_TO_SKIP);
        stub(metricsRegistryMock.newCounter("org.ekstep.ep.samza.task.PartnerDataRouterTask", "message-count")).toReturn(counterMock);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistryMock);
        partnerDataRouterTask = new PartnerDataRouterTask();
    }

    @Test
    public void shouldNotGetTheTopicFromEventWhenTopicDoesNotBelongToPartner() throws Exception {
        Event eventMock = mock(Event.class);
        HashMap<String, Object> message = new HashMap<String, Object>();
        stub(envelopMock.getMessage()).toReturn(message);
        stub(eventMock.belongsToAPartner()).toReturn(false);

        partnerDataRouterTask.process(envelopMock, null, null);

        verify(eventMock, never()).routeTo();
    }

    @Test
    public void shouldGetTheTopicFromEventWhenTopicBelongToPartner() throws Exception {
        Event eventMock = mock(Event.class);
        stub(eventMock.getData()).toReturn(EventFixture.PartnerData());
        stub(eventMock.eid()).toReturn("GE_PARTNER_DATA");
        stub(eventMock.belongsToAPartner()).toReturn(true);
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        partnerDataRouterTask.init(configMock, contextMock);
        partnerDataRouterTask.processEvent(collectorMock, eventMock);

        verify(eventMock).routeTo();
        verify(collectorMock, times(1)).send(argument.capture());
    }

    @Test
    public void shouldCleanThePartnerEventBeforeRoutingToCorrespondingTopic() throws Exception {
        Event event = new Event(EventFixture.CreateProfile());

        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        partnerDataRouterTask.init(configMock, contextMock);
        partnerDataRouterTask.processEvent(collectorMock, event);

        Map<String, Object> udata = (Map<String, Object>) event.getData().get("udata");
        assertThat(udata, not(hasKey("is_group_user")));
        assertThat(udata, not(hasKey("handle")));
        assertThat(udata, not(hasKey("gender")));
    }

    @Test
    public void shouldSkipLearningEvents() throws Exception {
        Event event = new Event(EventFixture.LearningEvent());

        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        partnerDataRouterTask.init(configMock, contextMock);
        partnerDataRouterTask.processEvent(collectorMock, event);

        verify(collectorMock, times(0)).send(argument.capture());
    }

    @Test
    public void shouldSkipAllVersionOneEvents() throws Exception {
        Event event = new Event(EventFixture.VersionOneEvent());

        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        partnerDataRouterTask.init(configMock, contextMock);
        partnerDataRouterTask.processEvent(collectorMock, event);

        verify(collectorMock, times(0)).send(argument.capture());
    }
}
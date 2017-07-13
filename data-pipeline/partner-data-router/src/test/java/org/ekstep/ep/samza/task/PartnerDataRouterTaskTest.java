package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PartnerDataRouterTaskTest {

    private final String EVENTS_TO_SKIP = "ME_.*";
    private final String EVENTS_TO_ALLOW = "GE_.*,OE_.*";
    private final String SUCCESS_TOPIC = "partners";
    private final String FAILED_TOPIC = "partners.fail";
    private MessageCollector collectorMock;
    private Config configMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistryMock;
    private IncomingMessageEnvelope envelopMock;
    private PartnerDataRouterTask partnerDataRouterTask;
    private Counter counterMock;
    private TaskCoordinator coordindatorMock;

    @Before
    public void setUp() {
        collectorMock = mock(MessageCollector.class);
        coordindatorMock = mock(TaskCoordinator.class);
        contextMock = mock(TaskContext.class);
        envelopMock = mock(IncomingMessageEnvelope.class);
        metricsRegistryMock = mock(MetricsRegistry.class);
        counterMock = mock(Counter.class);

        configMock = mock(Config.class);
        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("events.to.skip", "")).toReturn(EVENTS_TO_SKIP);
        stub(configMock.get("events.to.allow", "")).toReturn(EVENTS_TO_ALLOW);
        stub(metricsRegistryMock.newCounter("org.ekstep.ep.samza.task.PartnerDataRouterTask", "message-count")).toReturn(counterMock);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistryMock);
        partnerDataRouterTask = new PartnerDataRouterTask();
    }


    @Test
    public void shouldReadPartnerIdFromTagsAndSendToSuccessTopicWhenEventBelongToPartner() throws Exception {

        Event event =  new Event(EventFixture.PartnerData());
        stub(envelopMock.getMessage()).toReturn(event.getMap());

        partnerDataRouterTask.init(configMock, contextMock);
        partnerDataRouterTask.process(envelopMock, collectorMock, coordindatorMock);

        verify(collectorMock).send(argThat(validateOutputTopic(SUCCESS_TOPIC)));
        assertTrue(((Map<String,Object>) event.getMap().get("metadata")).containsKey("partner_name"));
    }

    @Test
    public void shouldReadPartnerIdFromETagsAndSendToSuccessTopicWhenEventBelongToPartner() throws Exception {

        Event event =  new Event(EventFixture.PartnerDataV2());
        stub(envelopMock.getMessage()).toReturn(event.getMap());

        partnerDataRouterTask.init(configMock, contextMock);
        partnerDataRouterTask.process(envelopMock, collectorMock, coordindatorMock);

        verify(collectorMock).send(argThat(validateOutputTopic(SUCCESS_TOPIC)));
        assertTrue(((Map<String,Object>) event.getMap().get("metadata")).containsKey("partner_name"));
    }

    @Test
    public void shouldNotSendEventsToSuccessTopicIfEventNotBelongToPartner() throws Exception {
        Event eventMock = mock(Event.class);
        stub(eventMock.getMap()).toReturn(EventFixture.PartnerData());
        stub(eventMock.eid()).toReturn("GE_PARTNER_DATA");
        stub(eventMock.belongsToAPartner()).toReturn(false);
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        HashMap<String, Object> message = new HashMap<String, Object>();
        partnerDataRouterTask.init(configMock, contextMock);
        partnerDataRouterTask.process(envelopMock, collectorMock, coordindatorMock);

        verify(collectorMock, times(1)).send(argument.capture());
    }

    @Test
    public void shouldCleanThePartnerEventBeforeSendingToSuccessTopic() throws Exception {
        Event event = new Event(EventFixture.CreateProfile());

        partnerDataRouterTask.init(configMock, contextMock);
        partnerDataRouterTask.processEvent(collectorMock, event);

        Map<String, Object> udata = (Map<String, Object>) event.getMap().get("udata");
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

    private ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final String stream) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
                assertEquals("kafka", systemStream.getSystem());
                assertEquals(stream, systemStream.getStream());
                return true;
            }
        };
    }
}
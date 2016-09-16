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
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class PublicTelemetryTaskTest {
    private final String SUCCESS_TOPIC = "telemetry.public";
    private final String FAILED_TOPIC = "telemetry.public.fail";
    private final String EVENTS_TO_SKIP = "GE_ERROR, GE_SERVICE_API_CALL, GE_API_CALL, GE_REGISTER_PARTNER, GE_PARTNER_DATA, GE_START_PARTNER_SESSION, GE_STOP_PARTNER_SESSION";
    private final String EVENTS_TO_ALLOW = "GE_.*, OE_.*";
    private MessageCollector collectorMock;
    private Config configMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private IncomingMessageEnvelope envelopMock;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private PublicTelemetryTask publicTelemetryTask;

    @Before
    public void setUp(){
        collectorMock = mock(MessageCollector.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        envelopMock = mock(IncomingMessageEnvelope.class);

        configMock = Mockito.mock(Config.class);
        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("events.to.skip", "")).toReturn(EVENTS_TO_SKIP);
        stub(configMock.get("events.to.allow", "")).toReturn(EVENTS_TO_ALLOW);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(metricsRegistry.newCounter("org.ekstep.ep.samza.task.TelemetryCleanerTask", "message-count")).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

        publicTelemetryTask = new PublicTelemetryTask();
    }

    @Test
    public void shouldProcessEventsStartsWithGE() throws Exception {
        Event event = new Event(EventFixture.CreateProfile());

        publicTelemetryTask.init(configMock, contextMock);
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        publicTelemetryTask.processEvent(collectorMock, event);

        verify(collectorMock,times(1)).send(argument.capture());
    }

    @Test
    public void shouldProcessEventsStartsWithOE() throws Exception {
        Event event = new Event(EventFixture.AssessmentEvent());

        publicTelemetryTask.init(configMock, contextMock);
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        publicTelemetryTask.processEvent(collectorMock, event);

        verify(collectorMock,times(1)).send(argument.capture());
    }

    @Test
    public void shouldNotAllowVersionOneEvents() throws Exception {
        Event event = new Event(EventFixture.VersionOneEvent());

        publicTelemetryTask.init(configMock, contextMock);
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        publicTelemetryTask.processEvent(collectorMock, event);

        verify(collectorMock,times(0)).send(argument.capture());
    }

    @Test
    public void shouldNotProcessNonPublicEvents() throws Exception {
        Event event = new Event(EventFixture.PartnerData());

        publicTelemetryTask.init(configMock, contextMock);
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        publicTelemetryTask.processEvent(collectorMock, event);

        verify(collectorMock,times(0)).send(argument.capture());
    }

    @Test
    public void shouldNotProcessAnyLearningEvents() throws Exception {
        List<Event> events = Arrays.asList(new Event(EventFixture.LearningEvent1()),new Event(EventFixture.LearningEvent2()));
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        publicTelemetryTask.init(configMock, contextMock);

        for (Event event : events) {
            publicTelemetryTask.processEvent(collectorMock, event);
        }

        verify(collectorMock,times(0)).send(argument.capture());
    }
}
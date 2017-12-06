package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class PublicTelemetryTaskTest {
    private final String SUCCESS_TOPIC = "telemetry.public";
    private final String FAILED_TOPIC = "telemetry.public.fail";
    private final String EVENTS_TO_SKIP = "ERROR, LOG, EXDATA, ME_.*";
    private final String DEFAULT_CHANNEL = "in.ekstep";
    private MessageCollector collectorMock;
    private Config configMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private IncomingMessageEnvelope envelopeMock;
    private PublicTelemetryTask publicTelemetryTask;

    @Before
    public void setUp(){
        collectorMock = mock(MessageCollector.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);

        configMock = Mockito.mock(Config.class);
        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("events.to.skip", "")).toReturn(EVENTS_TO_SKIP);
        stub(configMock.get("default.channel", "in.ekstep")).toReturn(DEFAULT_CHANNEL);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(metricsRegistry.newCounter("org.ekstep.ep.samza.task.TelemetryCleanerTask", "message-count")).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

        publicTelemetryTask = new PublicTelemetryTask();
    }

    @Test
    public void shouldProcessTelemetryV3Events() throws Exception {
        Event event = new Event(EventFixture.Interact());
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        publicTelemetryTask.init(configMock, contextMock);
        publicTelemetryTask.processEvent(collectorMock, event);

        verify(collectorMock,times(1)).send(argument.capture());
    }

    @Test
    public void shouldProcessAssessmentEvents() throws Exception {
        Event event = new Event(EventFixture.AssessmentEvent());

        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        publicTelemetryTask.init(configMock, contextMock);
        publicTelemetryTask.processEvent(collectorMock, event);

        verify(collectorMock,times(1)).send(argument.capture());
    }

    @Test
    public void shouldNotAllowVersionOneEvents() throws Exception {
        Event event = new Event(EventFixture.VersionOneEvent());

        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        publicTelemetryTask.init(configMock, contextMock);
        publicTelemetryTask.processEvent(collectorMock, event);

        verify(collectorMock,times(0)).send(argument.capture());
    }

    @Test
    public void shouldNotProcessNonPublicEvents() throws Exception {
        Event event = new Event(EventFixture.PartnerData());

        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);
        publicTelemetryTask.init(configMock, contextMock);
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

    @Test
    public void shouldSkipOtherChannelEvents() throws Exception {
        Event event = new Event(EventFixture.OtherChannel());
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        publicTelemetryTask.init(configMock, contextMock);
        publicTelemetryTask.processEvent(collectorMock, event);

        verify(collectorMock,times(0)).send(argument.capture());
    }
}
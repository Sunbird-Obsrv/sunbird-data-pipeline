
package org.ekstep.ep.samza.task;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.fixtures.SummaryV1;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

public class SummaryValidatorTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.denorm.valid";
    private static final String FAILED_TOPIC = "telemetry.failed";
    private static final String MALFORMED_TOPIC = "telemetry.malformed";
    private static final String TELEMETRY_SCHEMA_PATH = "src/main/resources/schemas/telemetry";
    private static final String SUMMARY_SCHEMA_PATH = "src/main/resources/schemas/summary";
    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private TelemetryValidatorTask telemetryValidatorTask;

    @Before
    public void setUp() {
        collectorMock = mock(MessageCollector.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);

        when(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).thenReturn(SUCCESS_TOPIC);
        when(configMock.get("output.failed.topic.name", FAILED_TOPIC)).thenReturn(FAILED_TOPIC);
        when(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).thenReturn(MALFORMED_TOPIC);
        when(configMock.get("telemetry.schema.path", TELEMETRY_SCHEMA_PATH)).thenReturn(TELEMETRY_SCHEMA_PATH);
        when(configMock.get("summary.schema.path", SUMMARY_SCHEMA_PATH)).thenReturn(SUMMARY_SCHEMA_PATH);
        when(metricsRegistry.newCounter(anyString(), anyString())).thenReturn(counter);
        when(contextMock.getMetricsRegistry()).thenReturn(metricsRegistry);
        telemetryValidatorTask = new TelemetryValidatorTask(configMock, contextMock);
    }

    @Test
    public void shouldSendEventToSuccessTopicIfEventIsValid() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(SummaryV1.VALID_EVENT);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldSendEventToFaildTopicIfEventIsNotValid() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(SummaryV1.INVALID_EVENT);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
    }

    @Test
    public void shouldSendEventToFaildTopicIfEventIsNotValid_CASE_1() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(SummaryV1.INVALID_EVENT_CASE_1);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
    }

    public ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
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


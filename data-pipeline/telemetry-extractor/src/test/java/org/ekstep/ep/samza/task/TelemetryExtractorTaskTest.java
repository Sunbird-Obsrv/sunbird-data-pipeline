package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.system.SystemStream;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.util.ExtractorUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class TelemetryExtractorTaskTest {

    class TestMessageCollector implements MessageCollector {

        public OutgoingMessageEnvelope outgoingEnvelope;

        @Override
        public void send(OutgoingMessageEnvelope outgoingMessageEnvelope) {
            outgoingEnvelope = outgoingMessageEnvelope;
        }
    }

    private TaskContext context;
    private Config config;
    private IncomingMessageEnvelope envelope;
    private MessageCollector collector;
    private TaskCoordinator coordinator;
    private Counter counter;
    private MetricsRegistry metricsRegistry;
    private final String successTopic = "telemetry.raw";

    @Before
    public void setup() {
        context = mock(TaskContext.class);
        config = mock(Config.class);
        envelope = mock(IncomingMessageEnvelope.class);
        collector = new TestMessageCollector();
        coordinator = mock(TaskCoordinator.class);
        counter = mock(Counter.class);
        metricsRegistry = mock(MetricsRegistry.class);
        stub(context.getMetricsRegistry()).toReturn(metricsRegistry);
        stub(metricsRegistry.newCounter(anyString(), anyString()))
                .toReturn(counter);
        stub(config.get("output.success.topic.name", "telemetry.raw")).toReturn(successTopic);
    }

    @Test
    public void synctsShouldBeStamped() throws Exception {
        String spec = EventFixture.getEventAsString("event");
        stub(envelope.getMessage()).toReturn(spec);
        TelemetryExtractorTask task = new TelemetryExtractorTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.raw", stream.getStream());

        String output = (String) envelope.getMessage();
        assertEquals(true, output.contains("syncts"));
    }
}

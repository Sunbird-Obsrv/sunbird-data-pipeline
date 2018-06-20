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
    private final String successTopic = "telemetry.extracted";
    private final String failedTopic = "telemetry.extracted.fail";

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
        stub(config.get("output.success.topic.name", "telemetry.extracted")).toReturn(successTopic);
        stub(config.get("output.failed.topic.name", "telemetry.extracted.fail")).toReturn(failedTopic);
    }

    @Test
    public void shouldExtractZippedRawData() throws Exception {
        // If metadata are already there in the event, converters should not overwrite. Instead it should merge
        String eventString = EventFixture.getEventAsString("event");
        byte[] data = eventString.getBytes(StandardCharsets.UTF_8);
        byte[] compressed = ExtractorUtils.compress(data);
        String encodedString = Base64.getEncoder().encodeToString(compressed);
        Map event = new HashMap();
        event.put("Timestamp", new DateTime(System.currentTimeMillis()).toString());
        event.put("DataType", "gzip");
        event.put("RawData", encodedString);

        byte[] eventBytes = new Gson().toJson(event).getBytes();
        byte[] compresedEvent = ExtractorUtils.compress(eventBytes);

        stub(envelope.getMessage()).toReturn(compresedEvent);
        TelemetryExtractorTask task = new TelemetryExtractorTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();
        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.extracted", stream.getStream());

        Map<String, Object> output = (Map<String, Object>) envelope.getMessage();
        assertEquals(true, output.containsKey("eid"));
        assertEquals(true, output.containsKey("@timestamp"));
        assertEquals("baa87c960c9559eb44d99c6b4509aabdf6988d71", ((String)output.get("did")));
    }


    @Test
    public void shouldExtractUnZippedRawData() throws Exception {
        // If metadata are already there in the event, converters should not overwrite. Instead it should merge
        String eventString = EventFixture.getEventAsString("event");
        byte[] data = eventString.getBytes(StandardCharsets.UTF_8);
        String encodedString = Base64.getEncoder().encodeToString(data);
        Map event = new HashMap();
        event.put("Timestamp", new DateTime(System.currentTimeMillis()).toString());
        event.put("DataType", "json");
        event.put("RawData", encodedString);

        byte[] eventBytes = new Gson().toJson(event).getBytes();
        byte[] compresedEvent = ExtractorUtils.compress(eventBytes);

        stub(envelope.getMessage()).toReturn(compresedEvent);
        TelemetryExtractorTask task = new TelemetryExtractorTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();
        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.extracted", stream.getStream());

        Map<String, Object> output = (Map<String, Object>) envelope.getMessage();
        assertEquals(true, output.containsKey("eid"));
        assertEquals(true, output.containsKey("@timestamp"));
        assertEquals("baa87c960c9559eb44d99c6b4509aabdf6988d71", ((String)output.get("did")));
    }
}

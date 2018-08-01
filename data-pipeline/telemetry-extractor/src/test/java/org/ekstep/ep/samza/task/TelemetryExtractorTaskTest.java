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
import java.util.List;
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
    private final String errorTopic = "telemetry.extractor.failed";

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
        stub(config.get("output.error.topic.name", "telemetry.extractor.failed")).toReturn(errorTopic);
        
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
        assertEquals(true, output.contains("@timestamp"));
    }

    @Test
    public void eventCountShouldZero() throws Exception {
        String spec = EventFixture.getEventAsString("empty_events");
        stub(envelope.getMessage()).toReturn(spec);
        TelemetryExtractorTask task = new TelemetryExtractorTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.raw", stream.getStream());

        String output = (String) envelope.getMessage();
        Map<String, Object> event = (Map<String, Object>) new Gson().fromJson(output, Map.class);
        List<Map<String, Object>> edata = (List<Map<String, Object>>)((Map<String, Object>)event.get("edata")).get("params");
        int event_count = ((Number)edata.get(0).get("events_count")).intValue();
        assertEquals(0, event_count);
    }
    
    @Test
    public void specWithoutSyncts() throws Exception {
        String spec = EventFixture.getEventAsString("event_without_syncts");
        stub(envelope.getMessage()).toReturn(spec);
        TelemetryExtractorTask task = new TelemetryExtractorTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        String output = (String) envelope.getMessage();
        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.raw", stream.getStream());
        assertEquals(true, output.contains("syncts"));
    }
    
    @Test
    public void specDoesNotContainJsonData() throws Exception {
        String spec = EventFixture.getEventAsString("not_json_event");
        stub(envelope.getMessage()).toReturn(spec);
        TelemetryExtractorTask task = new TelemetryExtractorTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        String output = (String) envelope.getMessage();
        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.extractor.failed", stream.getStream());
        List<String> events = (List<String>)((Map<String, Object>) new Gson().fromJson(output, Map.class)).get("events");
        String content = events.get(0);
        assertEquals("testing events", content);
    }
    
    @Test
    public void specDoesNotContainEventsKey() throws Exception {
        String spec = EventFixture.getEventAsString("without_event_key");
        stub(envelope.getMessage()).toReturn(spec);
        TelemetryExtractorTask task = new TelemetryExtractorTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        String output = (String) envelope.getMessage();
        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.extractor.failed", stream.getStream());
        assertEquals(false, output.contains("events"));
    }
    
//    @Test
//    public void auditEventMetrics() throws Exception {
//        String spec = EventFixture.getEventAsString("event");
//        stub(envelope.getMessage()).toReturn(spec);
//        TelemetryExtractorTask task = new TelemetryExtractorTask(config, context);
//        task.process(envelope, collector, coordinator);
//
//        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
//        SystemStream stream = envelope.getSystemStream();
//
//        String output = (String) envelope.getMessage();
//        System.out.println(output);
//        String output2 = (String) envelope.getMessage();
//        System.out.println(output2);
//        assertEquals("kafka", stream.getSystem());
//        assertEquals("telemetry.raw", stream.getStream());
//    }
}

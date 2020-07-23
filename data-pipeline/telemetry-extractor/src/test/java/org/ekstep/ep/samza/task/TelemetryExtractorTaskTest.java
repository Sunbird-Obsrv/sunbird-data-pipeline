package org.ekstep.ep.samza.task;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.DeDupEngine;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

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
    private final String defaultChannel = "01250894314817126443";
    private Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    TelemetryExtractorTask task;
    private DeDupEngine deDupEngineMock;

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
        stub(envelope.getOffset()).toReturn("2");
        stub(envelope.getSystemStreamPartition()).toReturn(new SystemStreamPartition("kafka", "input.topic", new Partition(1)));
        stub(config.get("output.success.topic.name", "telemetry.raw")).toReturn(successTopic);
        stub(config.get("output.assess.topic.name", "telemetry.assess.redact")).toReturn("telemetry.assess.redact");
        stub(config.get("output.error.topic.name", "telemetry.extractor.failed")).toReturn(errorTopic);
        stub(config.get("default.channel", "01250894314817126443")).toReturn(defaultChannel);
        stub(config.get("default.channel", "01250894314817126443")).toReturn(defaultChannel);
        stub(config.getInt("raw.individual.event.maxsize", 996148)).toReturn(996148);
        deDupEngineMock = mock(DeDupEngine.class);
            task = new TelemetryExtractorTask(config, context, deDupEngineMock);

    }
    
    public ArgumentMatcher<OutgoingMessageEnvelope> validateEventObject() {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope envelope = (OutgoingMessageEnvelope) o;
                String message = (String) envelope.getMessage();
                SystemStream stream = envelope.getSystemStream();
                Map<String, Object> event = new Gson().fromJson(message, Map.class);
                System.out.println("Eid" + event.get("eid"));
                
                if ("ASSESS".equals(event.get("eid"))) {
                    assertEquals("ASSESS:6ac822896cd8a1736d55806c13ada64c", event.get("mid"));
                    assertEquals("kafka", stream.getSystem());
                    assertEquals("telemetry.assess.redact", stream.getStream());
                } else if ("RESPONSE".equals(event.get("eid"))) {
                    assertEquals("RESPONSE:17d97e7cba61bd7c996d3ae71b9706ef", event.get("mid"));
                    assertEquals("kafka", stream.getSystem());
                    assertEquals("telemetry.assess.redact", stream.getStream());
                } else {
                    assertEquals("kafka", stream.getSystem());
                    assertEquals("telemetry.raw", stream.getStream());
                }
                return true;
            }
        };
    }

    @Test
    public void synctsShouldBeStamped() throws Exception {
        String spec = EventFixture.getEventAsString("event");
        stub(envelope.getMessage()).toReturn(spec);

        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.raw", stream.getStream());

        Map<String, Object> output = new Gson().fromJson((String) envelope.getMessage(), mapType);
        assertTrue(output.containsKey("syncts"));
        assertTrue(output.containsKey("@timestamp"));
        assertEquals(1529500243955L, ((Number) output.get("syncts")).longValue());
        assertEquals("2018-06-20T13:10:43.955Z", output.get("@timestamp"));

    }

    @Test
    public void assessEventShouldBeRouted() throws Exception {
        String spec = EventFixture.getEventAsString("event");
        stub(envelope.getMessage()).toReturn(spec);
        MessageCollector collectorMock = mock(MessageCollector.class);
        
        task.process(envelope, collectorMock, coordinator);
        verify(collectorMock, times(23)).send(argThat(validateEventObject()));
    }
    
    @Test
    public void shouldSendDuplicateBatchEventToFailedTopic() throws Exception {
        String spec = EventFixture.getEventAsString("event_app");
        stub(envelope.getMessage()).toReturn(spec);
        stub(deDupEngineMock.isUniqueEvent(anyString())).toReturn(false);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();


        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.extractor.failed", stream.getStream());

        Map<String, Object> output = new Gson().fromJson((String) envelope.getMessage(), mapType);
        assertTrue(output.containsKey("flags"));

    }


    @Test
    public void eventCountShouldZero() throws Exception {
        String spec = EventFixture.getEventAsString("empty_events");
        stub(envelope.getMessage()).toReturn(spec);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.raw", stream.getStream());

        String output = (String) envelope.getMessage();
        Map<String, Object> event = (Map<String, Object>) new Gson().fromJson(output, Map.class);
        List<Map<String, Object>> edata = (List<Map<String, Object>>) ((Map<String, Object>) event.get("edata")).get("params");
        int event_count = ((Number) edata.get(0).get("events_count")).intValue();
        assertEquals(0, event_count);

        Map<String, Object> context = (Map<String, Object>) event.get("context");
        String channel = (String) context.get("channel");
        assertEquals("01250894314817126443", channel);

        Map<String, String> padata = (Map<String, String>) context.get("pdata");
        assertEquals("pipeline", padata.get("id"));
    }

    @Test
    public void specWithoutSyncts() throws Exception {
        String spec = EventFixture.getEventAsString("event_without_syncts");
        stub(envelope.getMessage()).toReturn(spec);
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
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        String output = (String) envelope.getMessage();
        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.extractor.failed", stream.getStream());
        List<String> events = (List<String>) ((Map<String, Object>) new Gson().fromJson(output, Map.class)).get("events");
        String content = events.get(0);
        assertEquals("testing events", content);
    }

    @Test
    public void specDoesNotContainEventsKey() throws Exception {
        String spec = EventFixture.getEventAsString("without_event_key");
        stub(envelope.getMessage()).toReturn(spec);

        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        String output = (String) envelope.getMessage();
        assertEquals("kafka", stream.getSystem());
        assertEquals("telemetry.extractor.failed", stream.getStream());
        assertEquals(false, output.contains("events"));
    }

    @Test
    public void auditEventMetrics() throws Exception {
        String spec = EventFixture.getEventAsString("event1");
        stub(envelope.getMessage()).toReturn(spec);

        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        String output = (String) envelope.getMessage();
        Map<String, Object> event = (Map<String, Object>) new Gson().fromJson(output, Map.class);
        assertEquals("kafka", stream.getSystem());
        assertEquals("LOG", (String) event.get("eid"));
        Map<String, Object> edata = (Map<String, Object>) event.get("edata");
        assertEquals("telemetry_audit", (String) edata.get("type"));
        assertEquals("INFO", (String) edata.get("level"));

        Map<String, Object> param = (Map<String, Object>) ((List<Object>) edata.get("params")).get(0);
        assertEquals(2, ((Number) param.get("events_count")).intValue());
        assertEquals("SUCCESS", (String) param.get("sync_status"));
    }
}

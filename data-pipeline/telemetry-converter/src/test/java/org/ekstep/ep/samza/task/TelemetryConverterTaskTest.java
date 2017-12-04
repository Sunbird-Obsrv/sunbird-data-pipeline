package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class TelemetryConverterTaskTest {

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
    private final String successTopic = "kafka.success";
    private final String failedTopic = "kafka.failure";

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
        stub(config.get("output.success.topic.name", "telemetry.v3")).toReturn(successTopic);
        stub(config.get("output.failed.topic.name", "telemetry.v3.fail")).toReturn(failedTopic);
    }

    @Test
    public void v3EventsShouldNotBeProcessed() throws Exception {
        String v3Event = EventFixture.getEventAsString("START");
        stub(envelope.getMessage()).toReturn(v3Event);
        TelemetryConverterTask task = new TelemetryConverterTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        assertEquals("kafka", stream.getSystem());
        assertEquals("kafka.success", stream.getStream());

        String output = (String) envelope.getMessage();
        assertEquals(v3Event, output);
    }

    @Test
    public void flagsShouldNotBeOverwritten() throws Exception {
        // If flags are already there in the event, converter should not overwrite. Instead it should merge
        String v2Event = EventFixture.getEventAsString("CE_END");
        stub(envelope.getMessage()).toReturn(v2Event);
        TelemetryConverterTask task = new TelemetryConverterTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        assertEquals("kafka", stream.getSystem());
        assertEquals("kafka.success", stream.getStream());

        Map<String, Object> v3Event = (Map<String, Object>) new Gson().fromJson((String) envelope.getMessage(), Map.class);
        Map<String, Object> flags = (Map<String, Object>) v3Event.get("flags");
        String[] existingFlags = new String[]{"tv_skipped", "dd_processed", "ppm_skipped", "ldata_obtained", "ldata_processed", "od_processed"};
        for (String existingFlag : existingFlags) {
            assertEquals("flags don't have " + existingFlag, true, flags.containsKey(existingFlag));
        }

        String newFlag = "v2_converted";
        assertEquals("flags don't have " + newFlag, true, flags.containsKey(newFlag));
    }

    @Test
    public void metadataShouldNotBeOverwritten() throws Exception {
        // If metadata are already there in the event, converter should not overwrite. Instead it should merge
        String v2Event = EventFixture.getEventAsString("CE_END");
        stub(envelope.getMessage()).toReturn(v2Event);
        TelemetryConverterTask task = new TelemetryConverterTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        assertEquals("kafka", stream.getSystem());
        assertEquals("kafka.success", stream.getStream());

        Map<String, Object> v3Event = (Map<String, Object>) new Gson().fromJson((String) envelope.getMessage(), Map.class);
        Map<String, Object> metadata = (Map<String, Object>) v3Event.get("metadata");
        String[] existingMetadata = new String[]{"checksum", "cachehit", "od_last_processed_at", "od_processed_count", "index_name", "index_type"};
        for (String m : existingMetadata) {
            assertEquals("metadata don't have " + m, true, metadata.containsKey(m));
        }

        String[] newMetadata = new String[]{"source_eid", "source_mid"};
        for (String m : newMetadata) {
            assertEquals("metadata don't have " + m, true, metadata.containsKey(m));
        }
    }

    @Test
    public void processToSuccessTopic() throws Exception {
        String v2Event = EventFixture.getEventAsString("GE_START");
        stub(envelope.getMessage()).toReturn(v2Event);
        TelemetryConverterTask task = new TelemetryConverterTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        assertEquals("kafka", stream.getSystem());
        assertEquals("kafka.success", stream.getStream());

        Map<String, Object> v3Event = (Map<String, Object>) new Gson().fromJson((String) envelope.getMessage(), Map.class);
        assertEquals("START", v3Event.get("eid"));
    }

    @Test
    public void flagsShouldBeStamped() throws Exception {
        String v2Event = EventFixture.getEventAsString("GE_START");
        stub(envelope.getMessage()).toReturn(v2Event);
        TelemetryConverterTask task = new TelemetryConverterTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;

        Map<String, Object> v3Event = (Map<String, Object>) new Gson().fromJson((String) envelope.getMessage(), Map.class);
        assert (v3Event.containsKey("flags"));
        Map<String, Object> flags = (Map<String, Object>) v3Event.get("flags");
        assertEquals(true, flags.get("v2_converted"));
    }

    @Test
    public void metadataShouldBeStamped() throws Exception {
        String v2Event = EventFixture.getEventAsString("GE_START");
        stub(envelope.getMessage()).toReturn(v2Event);
        TelemetryConverterTask task = new TelemetryConverterTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;

        Map<String, Object> v3Event = (Map<String, Object>) new Gson().fromJson((String) envelope.getMessage(), Map.class);
        assert (v3Event.containsKey("metadata"));
        Map<String, Object> metadata = (Map<String, Object>) v3Event.get("metadata");
        assertEquals("06a89a02-c5b5-4225-a0d1-ba52312d2246", metadata.get("source_mid"));
        assertEquals("GE_START", metadata.get("source_eid"));
    }

    @Test
    public void conversionFailuresShouldGoToFailedTopic() throws Exception {
        String v2Event = corruptedGE_START();
        stub(envelope.getMessage()).toReturn(v2Event);
        TelemetryConverterTask task = new TelemetryConverterTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;
        SystemStream stream = envelope.getSystemStream();

        assertEquals("kafka", stream.getSystem());
        assertEquals("kafka.failure", stream.getStream());
    }

    @Test
    public void conversionFailuresShouldStampReasonForFailure() throws Exception {
        String v2Event = corruptedGE_START();
        stub(envelope.getMessage()).toReturn(v2Event);
        TelemetryConverterTask task = new TelemetryConverterTask(config, context);
        task.process(envelope, collector, coordinator);

        OutgoingMessageEnvelope envelope = ((TestMessageCollector) collector).outgoingEnvelope;

        Map<String, Object> failedEvent = (Map<String, Object>) new Gson().fromJson((String) envelope.getMessage(), Map.class);
        assert (failedEvent.containsKey("flags"));
        Map<String, Object> flags = (Map<String, Object>) failedEvent.get("flags");
        assertEquals(false, flags.get("v2_converted"));
        assertEquals("mid is not available in GE_START", flags.get("error"));
        assert (flags.containsKey("stack"));
    }

    private String corruptedGE_START() throws IOException, URISyntaxException {
        Map<String, Object> geStart = (Map<String, Object>) new Gson().fromJson(EventFixture.getEventAsString("GE_START"), Map.class);
        geStart.remove("mid"); // mid is a mandatory value and removing it should fail the conversion
        return new Gson().toJson(geStart);
    }

}

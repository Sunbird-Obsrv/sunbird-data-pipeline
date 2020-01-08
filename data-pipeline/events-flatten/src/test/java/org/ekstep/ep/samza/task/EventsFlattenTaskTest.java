package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EventsFlattenTaskTest {

    private static final String TELEMETRY_EVENTS_TOPIC = "events.telemetry";
    private static final String FAILED_TOPIC = "telemetry.failed";
    private static final String MALFORMED_TOPIC = "telemetry.malformed";
    private static final String EVENT_NAME = "SHARE";

    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private EventsFlattenTask eventsFlattenTask;

    @Before
    public void setUp() {
        collectorMock = mock(MessageCollector.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);

        stub(configMock.get("output.success.topic.name", TELEMETRY_EVENTS_TOPIC)).toReturn(TELEMETRY_EVENTS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);
        stub(configMock.get("flatten.telemetry.event.name", EVENT_NAME)).toReturn(EVENT_NAME);

        stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
        stub(envelopeMock.getOffset()).toReturn("2");
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.denorm.valid", new Partition(1)));
    }

    @Test
    public void shouldRouteTelemetryEventsToTelemetryTopic() {
       try {
           stub(configMock.get("router.events.summary.route.events", "ME_WORKFLOW_SUMMARY")).toReturn("ME_WORKFLOW_SUMMARY");
           eventsFlattenTask = new EventsFlattenTask(configMock, contextMock);
           stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_SHARE_EVENT);
           eventsFlattenTask.process(envelopeMock, collectorMock, coordinatorMock);
           Mockito.verify(collectorMock, times(4)).send(Matchers.argThat(validateEventObject(Arrays.asList("File", "imported", "download"), true)));
       }catch (Exception e){
           System.out.println(e.getMessage());
       }
    }

    public ArgumentMatcher<OutgoingMessageEnvelope> validateEventObject(List<String> edataType, Boolean ef_processed) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                Map<String, Object> shareEvent;
                Map<String, Object> shareEventEdata;
                OutgoingMessageEnvelope flattenEvent = (OutgoingMessageEnvelope) o;
                String message = (String) flattenEvent.getMessage();
                shareEvent = new Gson().fromJson(message, Map.class);
                assertEquals(ef_processed, new Gson().fromJson(shareEvent.get("flags").toString(), Map.class).get("ef_processed"));
                if (shareEvent.get("eid").equals("SHARE_ITEM")) {
                    shareEventEdata = new Gson().fromJson(shareEvent.get("edata").toString(), Map.class);
                    assertEquals(true, edataType.contains(shareEventEdata.get("type")));
                    assertNull(shareEventEdata.get("items"));
                } else {
                    shareEventEdata = new Gson().fromJson(shareEvent.get("edata").toString(), Map.class);
                    assertNotNull(shareEventEdata.get("items"));
                }
                return true;
            }
        };
    }
}

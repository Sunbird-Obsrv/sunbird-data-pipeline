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

public class EventsFlattenerTaskTest {

    private static final String TELEMETRY_EVENTS_TOPIC = "telemetry.sink";
    private static final String FAILED_TOPIC = "telemetry.failed";

    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private EventsFlattenerTask eventsFlattenerTask;

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

        stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
    }

    /**
     * When share event having multiple edata items
     */
    @Test
    public void shouldFlattenTheShareEvent() {
        try {
            eventsFlattenerTask = new EventsFlattenerTask(configMock, contextMock);
            stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_SHARE_EVENT_WITHOU_SIZE);
            eventsFlattenerTask.process(envelopeMock, collectorMock, coordinatorMock);
            Mockito.verify(collectorMock, times(2)).send(Matchers.argThat(validateEventObject(Arrays.asList("File", "import", "download"), true)));
        } catch (Exception e) {
           System.out.println(e.getMessage());
        }
    }

    @Test
    public void shouldFlattenTheShareEventWhenParamsHavingMultipleObject() {
        try {
            eventsFlattenerTask = new EventsFlattenerTask(configMock, contextMock);
            stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_SHARE_EVENT);
            eventsFlattenerTask.process(envelopeMock, collectorMock, coordinatorMock);
            Mockito.verify(collectorMock, times(4)).send(Matchers.argThat(validateEventObject(Arrays.asList("File", "import", "download"), true)));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void shouldFlattenTheEventWhenTransferIsNul() {
        try {
            eventsFlattenerTask = new EventsFlattenerTask(configMock, contextMock);
            stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_SHARE_EVENT_WITHOU_SIZE);
            eventsFlattenerTask.process(envelopeMock, collectorMock, coordinatorMock);
            Mockito.verify(collectorMock, times(2)).send(Matchers.argThat(validateEventObject(Arrays.asList("File", "import", "download"), true)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void shouldFlattenTheEventWhenSizeIsEmpty() {
        try {
            eventsFlattenerTask = new EventsFlattenerTask(configMock, contextMock);
            stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_SHARE_EVENT_WHEN_EMPTY_SIZE);
            eventsFlattenerTask.process(envelopeMock, collectorMock, coordinatorMock);
            Mockito.verify(collectorMock, times(2)).send(Matchers.argThat(validateEventObject(Arrays.asList("File", "import", "download"), true)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * When Event is other than "SHARE" Then it should route to success topic
     */
    @Test
    public void shouldRouteToSuccessTopic() {
        try {
            stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_START_EVENT);
            eventsFlattenerTask = new EventsFlattenerTask(configMock, contextMock);
            eventsFlattenerTask.process(envelopeMock, collectorMock, coordinatorMock);
            verify(collectorMock, times(4)).send(argThat(validateOutputTopic(envelopeMock.getMessage(), TELEMETRY_EVENTS_TOPIC)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
                assertEquals("kafka", systemStream.getSystem());
                assertNotNull(outgoingMessageEnvelope.getMessage());
                return true;
            }
        };
    }

    public ArgumentMatcher<OutgoingMessageEnvelope> validateEventObject(List<String> edataType, Boolean ef_processed) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                Map<String, Object> shareEvent;
                Map<String, Object> shareEventEdata;
                OutgoingMessageEnvelope flattenEvent = (OutgoingMessageEnvelope) o;
                String message = (String) flattenEvent.getMessage();
                System.out.println(message);
                shareEvent = new Gson().fromJson(message, Map.class);
                assertEquals(ef_processed, new Gson().fromJson(shareEvent.get("flags").toString(), Map.class).get("ef_processed"));
                if (shareEvent.get("eid").equals("SHARE_ITEM")) {
                    shareEventEdata = new Gson().fromJson(new Gson().toJson(shareEvent.get("edata")), Map.class);
                    assertEquals(true, edataType.contains(shareEventEdata.get("type")));
                    assertNull(shareEventEdata.get("items"));
                } else {
                    shareEventEdata = new Gson().fromJson(new Gson().toJson(shareEvent.get("edata")), Map.class);
                    assertNotNull(shareEventEdata.get("items"));
                }
                return true;
            }
        };
    }
}

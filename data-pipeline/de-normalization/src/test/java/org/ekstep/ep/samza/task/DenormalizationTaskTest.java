package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.Child;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.external.UserService;
import org.ekstep.ep.samza.external.UserServiceClient;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.BackendEventFactory;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DenormalizationTaskTest {
    private final String SUCCESS_TOPIC = "events_with_de_normalization";
    private final String FAILED_TOPIC = "events_failed_de_normalization";
    private final String RETRY_TOPIC = "events_retry";
    private final String BACKEND_EVENTS = "CE_.*, CP_.*, BE_.*";
    private final String EVENTS_TO_SKIP = "ME_APP_SESSION_SUMMARY,ME_APP_USAGE_SUMMARY,ME_CE_SESSION_SUMMARY,ME_AUTHOR_USAGE_SUMMARY,ME_TEXTBOOK_SESSION_SUMMARY";
    private final String DEFAULT_CHANNEL = "in.ekstep";
    private MessageCollector collectorMock;
    private Event eventMock;
    private UserService userServiceMock;
    private DeNormalizationTask deNormalizationTask;
    private Config configMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private IncomingMessageEnvelope envelopMock;
    private KeyValueStore<String, Object> deviceStore;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private int retryBackoffBase;
    private int retryBackoffLimit;
    private KeyValueStore<String, Object> retryStore;
    private BackendEventFactory backendEventFactoryMock;

    @Before
    public void setUp() {
        collectorMock = mock(MessageCollector.class);
        eventMock = mock(Event.class);
        userServiceMock = mock(UserServiceClient.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        envelopMock = mock(IncomingMessageEnvelope.class);
        deviceStore = mock(KeyValueStore.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        backendEventFactoryMock = mock(BackendEventFactory.class);

        configMock = Mockito.mock(Config.class);
        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("output.retry.topic.name", RETRY_TOPIC)).toReturn(RETRY_TOPIC);
        stub(configMock.get("backend.events", "")).toReturn(BACKEND_EVENTS);
        stub(configMock.get("events.to.skip", EVENTS_TO_SKIP)).toReturn(EVENTS_TO_SKIP);
        stub(metricsRegistry.newCounter("org.ekstep.ep.samza.task.DeNormalizationTask", "message-count")).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
        stub(configMock.get("retry.backoff.base")).toReturn("10");
        stub(configMock.get("retry.backoff.limit")).toReturn("4");
//        stub(eventMock.read("eid")).toReturn(new NullableValue<Object>("BE_CONTENT_USAGE_SUMMARY"));
//        stub(eventMock.isBackendEvent()).toReturn(false);

        deNormalizationTask = new DeNormalizationTask();
    }

    @Test
    public void ShouldInitializeEvent() {
        deNormalizationTask.processEvent(collectorMock, eventMock, userServiceMock);

        verify(eventMock).initialize();
    }

    @Test
    public void ShouldProcessEvent() throws Exception {
        stub(eventMock.canBeProcessed()).toReturn(true);

        deNormalizationTask.processEvent(collectorMock, eventMock, userServiceMock);

        verify(eventMock).process(eq(userServiceMock), any(DateTime.class));
    }

    @Test
    public void ShouldNotProcessEvent() throws Exception {
        HashMap<String, Object> message = new HashMap<String, Object>();

        stub(eventMock.canBeProcessed()).toReturn(false);
        stub(eventMock.getData()).toReturn(message);

        deNormalizationTask.init(configMock, contextMock);
        deNormalizationTask.processEvent(collectorMock, eventMock, userServiceMock);

        verify(collectorMock).send(argThat(validateOutputTopic(message, SUCCESS_TOPIC)));
        verify(eventMock, never()).process(eq(userServiceMock), any(DateTime.class));
    }

    @Test
    public void ShouldSendOutputToReTryTopicIfUIDIsNotPresentInDb() throws Exception {
        stub(eventMock.canBeProcessed()).toReturn(true);
        stub(eventMock.shouldPutInRetry()).toReturn(true);

        HashMap<String, Object> message = new HashMap<String, Object>();
        stub(eventMock.getData()).toReturn(message);
        deNormalizationTask.init(configMock, contextMock);
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        deNormalizationTask.processEvent(collectorMock, eventMock, userServiceMock);

        verify(collectorMock, times(1)).send(argument.capture());
        validateStreams(argument, message, new String[]{RETRY_TOPIC});
    }

    //TODO: needs to revisit. Still applicable : GAURAV
    @Test
    public void ShouldSendOutputToRetryTopicWhenProblemWithDb() throws Exception {
        stub(eventMock.shouldPutInRetry()).toReturn(true);
//        stub(eventMock.hadIssueWithDb()).toReturn(true);
        stub(eventMock.shouldPutInRetry()).toReturn(true);

        HashMap<String, Object> message = new HashMap<String, Object>();
        stub(eventMock.getData()).toReturn(message);
        deNormalizationTask.init(configMock, contextMock);
        ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        deNormalizationTask.processEvent(collectorMock, eventMock, userServiceMock);

        verify(collectorMock, times(1)).send(argument.capture());
        validateStreams(argument, message, new String[]{RETRY_TOPIC});
    }

    @Test
    public void ShouldSendOutputToSuccessTopic() throws Exception {
        stub(eventMock.shouldPutInRetry()).toReturn(false);
        HashMap<String, Object> message = new HashMap<String, Object>();
        stub(eventMock.getData()).toReturn(message);
        deNormalizationTask.init(configMock, contextMock);

        deNormalizationTask.processEvent(collectorMock, eventMock, userServiceMock);

        verify(collectorMock).send(argThat(validateOutputTopic(message, SUCCESS_TOPIC)));
    }

    @Test
    public void ShouldNotRetryIfBackingOff() throws Exception {
        deNormalizationTask.init(configMock, contextMock);
        when(eventMock.shouldBackoff()).thenReturn(true);
        deNormalizationTask.processEvent(collectorMock, eventMock, userServiceMock);
    }

    @Test
    public void ShouldTryIfBackingOff() throws Exception {
        stub(eventMock.shouldPutInRetry()).toReturn(false);
        HashMap<String, Object> message = new HashMap<String, Object>();
        stub(eventMock.getData()).toReturn(message);
        deNormalizationTask.init(configMock, contextMock);
        when(eventMock.shouldBackoff()).thenReturn(false);
        deNormalizationTask.processEvent(collectorMock, eventMock, userServiceMock);
        verify(collectorMock).send(argThat(validateOutputTopic(message, SUCCESS_TOPIC)));
    }

    @Test
    public void ShouldAddLastSkippedAtToMetadataIfSkipping() throws Exception {
        HashMap<String, Object> message = new HashMap<String, Object>();
        stub(eventMock.canBeProcessed()).toReturn(true);
        stub(eventMock.getData()).toReturn(message);
        stub(eventMock.shouldBackoff()).toReturn(true);
        stub(eventMock.shouldPutInRetry()).toReturn(true);
        deNormalizationTask.init(configMock, contextMock);
        deNormalizationTask.processEvent(collectorMock, eventMock, userServiceMock);
        verify(collectorMock).send(argThat(validateOutputTopic(message, RETRY_TOPIC)));
        verify(eventMock, times(1)).addLastSkippedAt(any(DateTime.class));
    }

    //TODO: needs to be revisited. Test intent not clear : GAURAV
    @Test
    public void ShouldAddBackendEventTypeIfEventBelongToAnyOfTheBackendEvents() throws Exception {
        HashMap<String, Object> message = new HashMap<String, Object>();
        stub(eventMock.canBeProcessed()).toReturn(true);
        stub(eventMock.getData()).toReturn(message);
        stub(eventMock.shouldBackoff()).toReturn(false);
        stub(eventMock.shouldPutInRetry()).toReturn(true);
//        stub(eventMock.isBackendEvent()).toReturn(true);
        deNormalizationTask.init(configMock, contextMock);
        deNormalizationTask.processEvent(collectorMock, eventMock, userServiceMock);
        verify(collectorMock).send(argThat(validateOutputTopic(message, RETRY_TOPIC)));
//        verify(eventMock, times(1)).setBackendTrue();
    }

    @Test
    public void ShouldNotProcessAnyOfTheBackendEvent() throws Exception {
        Map<String, Object> message = EventFixture.BeEvent();
        KeyValueStore<String, Child> childStore = Mockito.mock(KeyValueStore.class);
        KeyValueStore<String, Object> retryStore = Mockito.mock(KeyValueStore.class);
        Event event = new Event(message, childStore, Arrays.asList("BE_.*"), Arrays.asList("ME_CE_SESSION_SUMMARY"), 10, retryStore, DEFAULT_CHANNEL);

        deNormalizationTask.init(configMock, contextMock);
        deNormalizationTask.processEvent(collectorMock, event, userServiceMock);

        Assert.assertFalse(event.canBeProcessed());
        verify(collectorMock).send(argThat(validateOutputTopic(message, SUCCESS_TOPIC)));
    }

    @Test
    public void ShouldNotProcessAnyOfTheEventMarkedToBeSkiped() throws Exception {
        Map<String, Object> message = EventFixture.MeEvent();
        KeyValueStore<String, Child> childStore = Mockito.mock(KeyValueStore.class);
        KeyValueStore<String, Object> retryStore = Mockito.mock(KeyValueStore.class);
        Event event = new Event(message, childStore, Arrays.asList("BE_.*"), Arrays.asList("ME_CE_SESSION_SUMMARY"), 10, retryStore, DEFAULT_CHANNEL);

        deNormalizationTask.init(configMock, contextMock);
        deNormalizationTask.processEvent(collectorMock, event, userServiceMock);

        Assert.assertFalse(event.canBeProcessed());
        Assert.assertFalse(event.isBackendEvent());
        verify(collectorMock).send(argThat(validateOutputTopic(message, SUCCESS_TOPIC)));
    }

    private ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
                assertEquals("kafka", systemStream.getSystem());
                assertEquals(stream, systemStream.getStream());
                assertEquals(message, outgoingMessageEnvelope.getMessage());
                return true;
            }
        };
    }

    private void validateStreams(ArgumentCaptor<OutgoingMessageEnvelope> argument, HashMap<String, Object> message, String[] topics) {
        List<OutgoingMessageEnvelope> envelops = argument.getAllValues();

        ArrayList<Object> messages = new ArrayList<Object>();
        ArrayList<String> streams = new ArrayList<String>();
        for (OutgoingMessageEnvelope envelope : envelops) {
            messages.add(envelope.getMessage());
            streams.add(envelope.getSystemStream().getStream());
        }

        assertTrue(messages.contains(message));
        for (String topic : topics)
            assertTrue(streams.contains(topic));
    }
}
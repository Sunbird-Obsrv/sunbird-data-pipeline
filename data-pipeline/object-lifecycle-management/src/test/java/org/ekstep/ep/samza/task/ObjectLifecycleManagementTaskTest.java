package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.fixture.EventFixture;
import org.ekstep.ep.samza.fixture.SaveObjectFixture;
import org.ekstep.ep.samza.object.service.ObjectServiceClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class ObjectLifecycleManagementTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.objects";
    private static final String FAILED_TOPIC = "telemetry.objects.fail";
    private final String OBJECT_LIFECYCLE_EVENTS = "BE_OBJECT_LIFECYCLE";
    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private ObjectServiceClient objectServiceMock;
    private ObjectLifecycleManagementTask objectLifecycleManagementTask;

    @Before
    public void setUp() throws Exception {
        collectorMock = mock(MessageCollector.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);
        objectServiceMock = Mockito.mock(ObjectServiceClient.class);

        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("lifecycle.events", "")).toReturn(OBJECT_LIFECYCLE_EVENTS);
        stub(metricsRegistry.newCounter(anyString(), anyString()))
                .toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

        objectLifecycleManagementTask = new ObjectLifecycleManagementTask(configMock, contextMock,objectServiceMock);
    }

    @Test
    public void shouldNotProcessOtherEvents() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OtherEvent());

        objectLifecycleManagementTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(objectServiceMock, times(0)).createOrUpdate(anyMap());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldProcessLifeCycleEventsAndShouldCallObjectService() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.LifecycleEvent());
        stub(objectServiceMock.createOrUpdate(SaveObjectFixture.getObjectRequest())).toReturn(SaveObjectFixture.getObjectResponse());

        objectLifecycleManagementTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(objectServiceMock, times(1)).createOrUpdate(anyMap());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldUpdateFlagsForSuccessfulObjectServiceResponse() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.LifecycleEvent());
        stub(objectServiceMock.createOrUpdate(SaveObjectFixture.getObjectRequest())).toReturn(SaveObjectFixture.getObjectResponse());

        objectLifecycleManagementTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(objectServiceMock, times(1)).createOrUpdate(anyMap());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
        Assert.assertEquals((((Map<String, Object>) ((Map<String, Object>) envelopeMock.getMessage()).get("flags")).get("olm_processed")),true);
    }

    @Test
    public void shouldUpdateFlagsForFailureObjectServiceResponse() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.LifecycleEvent());
        stub(objectServiceMock.createOrUpdate(SaveObjectFixture.getObjectRequest())).toReturn(SaveObjectFixture.getFailureResponse());

        objectLifecycleManagementTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(objectServiceMock, times(1)).createOrUpdate(anyMap());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
        Assert.assertEquals((((Map<String, Object>) ((Map<String, Object>) envelopeMock.getMessage()).get("flags")).get("olm_processed")),false);
    }

    @Test
    public void shouldUpdateErrorDetailsInMetadataForFailureObjectServiceResponse() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.LifecycleEvent());
        stub(objectServiceMock.createOrUpdate(SaveObjectFixture.getObjectRequest())).toReturn(SaveObjectFixture.getFailureResponse());

        objectLifecycleManagementTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(objectServiceMock, times(1)).createOrUpdate(anyMap());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
        Assert.assertEquals((((Map<String, Object>) ((Map<String, Object>) envelopeMock.getMessage()).get("metadata")).get("olm_process_err")),"BAD_REQUEST");
        Assert.assertEquals((((Map<String, Object>) ((Map<String, Object>) envelopeMock.getMessage()).get("metadata")).get("olm_process_err_msg")),"TYPE IS MANDATORY, ID IS MANDATORY");
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
}
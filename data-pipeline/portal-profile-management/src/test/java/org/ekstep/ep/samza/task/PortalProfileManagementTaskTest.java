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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class PortalProfileManagementTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.portal_profiles";
    private static final String FAILED_TOPIC = "telemetry.portal_profiles.fail";
    private static final String CONTENT_CACHE_TTL = "60000";
    private static final String CP_UPDATE_PROFILE_EVENT = "CP_UPDATE_PROFILE";
    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private PortalProfileManagementTask portalProfileManagementTask;

    @Before
    public void setUp() throws Exception {
        collectorMock = mock(MessageCollector.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);

        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("cp.update.profile.event", CP_UPDATE_PROFILE_EVENT)).toReturn(CP_UPDATE_PROFILE_EVENT);
        stub(metricsRegistry.newCounter(anyString(), anyString()))
                .toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

        portalProfileManagementTask = new PortalProfileManagementTask(configMock, contextMock);
    }

    @Test
    public void shouldPassEventThrough() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.cpUpdateProfileEvent());
        portalProfileManagementTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
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
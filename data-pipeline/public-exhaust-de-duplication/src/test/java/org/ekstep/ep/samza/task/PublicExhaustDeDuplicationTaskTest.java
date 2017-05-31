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
import org.ekstep.ep.samza.dedup.DeDupEngine;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class PublicExhaustDeDuplicationTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.public_exhaust";
    private static final String FAILED_TOPIC = "telemetry.public_exhaust.fail";
    private static final String DUPLICATE_TOPIC = "telemetry.public_exhaust.duplicate";
    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private PublicExhaustDeDuplicationTask publicExhaustDeDuplicationTask;
    private KeyValueStore publicExhaustStoreMock;
    private DeDupEngine deDupEngineMock;

    @Before
    public void setUp() throws Exception {
        collectorMock = mock(MessageCollector.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);
        publicExhaustStoreMock = mock(KeyValueStore.class);
        deDupEngineMock = mock(DeDupEngine.class);

        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("output.duplicate.topic.name", DUPLICATE_TOPIC)).toReturn(DUPLICATE_TOPIC);
        stub(metricsRegistry.newCounter(anyString(), anyString()))
                .toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

        publicExhaustDeDuplicationTask = new PublicExhaustDeDuplicationTask(configMock, contextMock,publicExhaustStoreMock,deDupEngineMock);
    }

    @Test
    public void ShouldSendEventToSuccessTopicIfEventIsUnique() throws Exception{
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithChecksum());
        when(deDupEngineMock.isUniqueEvent(anyString())).thenReturn(true);

        publicExhaustDeDuplicationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
        Assert.assertEquals((((Map<String, Object>) ((Map<String, Object>) envelopeMock.getMessage()).get("flags")).get("public_de_dup_processed")), true);
    }

    @Test
    public void ShouldSendEventsToDuplicateTopicIfEventIsDuplicate() throws Exception{
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithChecksum());
        when(deDupEngineMock.isUniqueEvent(anyString())).thenReturn(false);

        publicExhaustDeDuplicationTask.process(envelopeMock,collectorMock,coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), DUPLICATE_TOPIC)));

        Assert.assertEquals((((Map<String, Object>) ((Map<String, Object>) envelopeMock.getMessage()).get("flags")).get("public_de_dup_processed")), false);
        Assert.assertEquals((((Map<String, Object>) ((Map<String, Object>) envelopeMock.getMessage()).get("flags")).get("public_de_dup_duplicate_event")), true);
    }

    @Test
    public void ShouldSkipEventsAndSendToSuccessTopicIfChecksumIsAbsent() throws Exception{
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithoutChecksum());

        publicExhaustDeDuplicationTask.process(envelopeMock,collectorMock,coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));

        Assert.assertEquals((((Map<String, Object>) ((Map<String, Object>) envelopeMock.getMessage()).get("flags")).get("public_de_dup_processed")), false);
        Assert.assertEquals((((Map<String, Object>) ((Map<String, Object>) envelopeMock.getMessage()).get("flags")).get("public_de_dup_checksum_present")), false);
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
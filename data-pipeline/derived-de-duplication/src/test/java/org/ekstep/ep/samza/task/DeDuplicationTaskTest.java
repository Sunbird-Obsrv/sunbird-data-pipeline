package org.ekstep.ep.samza.task;

import com.fiftyonred.mock_jedis.MockJedis;
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
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class DeDuplicationTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.derived.unique";
    private static final String FAILED_TOPIC = "telemetry.unique.fail";
    private static final String DUPLICATE_TOPIC = "telemetry.duplicate";
    private static final String MALFORMED_TOPIC = "telemetry.malformed";
    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private DeDuplicationTask deDuplicationTask;
    private DeDupEngine deDupEngineMock;
    private Jedis jedisMock = new MockJedis("duplicationtest");
    private int dupStoreId = 1;

    @Before
    public void setUp() {
        collectorMock = mock(MessageCollector.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);
        RedisConnect redisConnectMock = mock(RedisConnect.class);
        deDupEngineMock = mock(DeDupEngine.class);
        stub(redisConnectMock.getConnection()).toReturn(jedisMock);
        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("output.duplicate.topic.name", DUPLICATE_TOPIC)).toReturn(DUPLICATE_TOPIC);
        stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);
        stub(metricsRegistry.newCounter(anyString(), anyString()))
                .toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
        stub(envelopeMock.getOffset()).toReturn("2");
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "input.topic", new Partition(0)));
        stub(configMock.getInt("redis.database.duplicationstore.id", dupStoreId)).toReturn(dupStoreId);
        when(configMock.get("dedup.producer.include.ids", "")).thenReturn("dev.diskha.portal");
        stub(configMock.getList("dedup.producer.include.ids", new ArrayList<>())).toReturn(Arrays.asList("dev.diksha.portal"));


        deDuplicationTask = new DeDuplicationTask(configMock, contextMock, deDupEngineMock);
    }

    @Test
    public void ShouldSendEventToSuccessTopicIfEventIsUnique() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithChecksumJson());

        when(deDupEngineMock.isUniqueEvent(anyString())).thenReturn(true);

        deDuplicationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void ShouldSendEventsToDuplicateTopicIfEventIsDuplicate() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithChecksumJson());
        when(deDupEngineMock.isUniqueEvent(anyString())).thenReturn(false);

        deDuplicationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), DUPLICATE_TOPIC)));
    }

    @Test
    public void ShouldSendEventsToMalformedTopicIfEventIsMalformed() throws Exception {

        when(envelopeMock.getMessage()).thenReturn("{'metadata':{'checksum':'sajksajska'}");

        deDuplicationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
    }

    @Test(expected = Exception.class)
    public void ShouldSendEventsToFailedTopicForAnyUnhandledException() throws Exception {

        when(envelopeMock.getMessage()).thenReturn(EventFixture.EventWithChecksumJson());
        when(deDupEngineMock.isUniqueEvent(anyString())).thenThrow(new Exception());

        deDuplicationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
    }

    @Test
    public void ShouldStoreChecksumIfEventIsUnique() throws Exception {

        when(envelopeMock.getMessage()).thenReturn(EventFixture.EventWithChecksumJson());
        when(deDupEngineMock.isUniqueEvent(anyString())).thenReturn(true);

        deDuplicationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(deDupEngineMock, times(1)).isUniqueEvent(anyString());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    public ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
                assertEquals("kafka", systemStream.getSystem());
                assertEquals(stream, systemStream.getStream());
                return true;
            }
        };
    }
}

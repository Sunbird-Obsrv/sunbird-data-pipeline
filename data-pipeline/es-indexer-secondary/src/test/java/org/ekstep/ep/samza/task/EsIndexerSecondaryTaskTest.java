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
import org.ekstep.ep.samza.esclient.ElasticSearchService;
import org.ekstep.ep.samza.esclient.IndexResponse;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public class EsIndexerSecondaryTaskTest {

    private static final String FAILED_TOPIC = "telemetry.es-sink-secondary.fail";
    private static final String ES_HOST = "localhost";
    private static final String ES_PORT = "9200";
    private static final String DEFAULT_INDEX_NAME = "failed-telemetry-retry";
    private static final String DEFAULT_INDEX_TYPE = "events";
    private MessageCollector collectorMock;
    private ElasticSearchService esServiceMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private EsIndexerSecondaryTask esIndexerSecondaryTask;

    @Before
    public void setUp() throws Exception {

        collectorMock = mock(MessageCollector.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);
        esServiceMock = Mockito.mock(ElasticSearchService.class);

        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("host.elastic_search", ES_HOST)).toReturn(ES_HOST);
        stub(configMock.get("port.elastic_search", ES_PORT)).toReturn(ES_PORT);
        stub(configMock.get("default.failed.index_name", DEFAULT_INDEX_NAME)).toReturn(DEFAULT_INDEX_NAME);
        stub(configMock.get("default.failed.index_type", DEFAULT_INDEX_TYPE)).toReturn(DEFAULT_INDEX_TYPE);
        stub(metricsRegistry.newCounter(anyString(), anyString()))
                .toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

        esIndexerSecondaryTask = new EsIndexerSecondaryTask(configMock, contextMock, esServiceMock);
    }

    @Test
    public void shouldIndexEventsToElasticSearchIfIndexNameAndTypeIsPresent() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithIndexDetails());
        stub(esServiceMock.index(anyString(),anyString(),anyString(),anyString())).toReturn(new IndexResponse("200", null));

        esIndexerSecondaryTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(esServiceMock, times(1)).index(anyString(),anyString(),anyString(),anyString());
        verify(collectorMock, times(0)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldSendTheEventsToFailedTopicIfIndexActionIsFailed() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithIndexDetails());
        stub(esServiceMock.index(anyString(),anyString(),anyString(),anyString())).toReturn(new IndexResponse("400", null));

        esIndexerSecondaryTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(esServiceMock, times(2)).index(anyString(),anyString(),anyString(),anyString());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
    }

    @Test
    public void shouldWriteEventsToFailedIndexAndSendToFailedTopicIfIndexDetailsAreMissingInEvent() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithoutIndexDetails());
        stub(esServiceMock.index(anyString(),anyString(),anyString(),anyString())).toReturn(new IndexResponse("200", null));

        esIndexerSecondaryTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(esServiceMock, times(1)).index(anyString(),anyString(),anyString(),anyString());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
    }

    @Test
    public void shouldWriteEventsToFailedIndexAndSendToFailedTopicIfIndexActionFailed() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithIndexDetails());
        stub(esServiceMock.index(anyString(),anyString(),anyString(),anyString())).toReturn(new IndexResponse("400", null));

        esIndexerSecondaryTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(esServiceMock, times(2)).index(anyString(),anyString(),anyString(),anyString());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
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
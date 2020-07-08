package org.ekstep.ep.samza.task;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import com.google.gson.Gson;
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
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Aggregate;
import org.ekstep.ep.samza.domain.BatchEvent;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.service.AssessmentAggregatorService;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.ContentCache;
import org.ekstep.ep.samza.util.RestUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AssessmentAggregatorTest {

    private IncomingMessageEnvelope envelope;
    private MessageCollector collector;
    private AssessmentAggregatorTask assessmentAggregatorTask;
    private TaskCoordinator taskCoordinator;
    private TaskContext taskContext;
    private CassandraConnect cassandraConnect;
    private AssessmentAggregatorConfig config;
    private Counter counter;
    private ContentCache contentCache;
    private RestUtil restUtilMock;

    @Before
    public void setUp() {
        Config configMock = mock(Config.class);
        envelope = mock(IncomingMessageEnvelope.class);
        collector = mock(MessageCollector.class);
        taskCoordinator = mock(TaskCoordinator.class);
        taskContext = mock(TaskContext.class);
        contentCache = mock(ContentCache.class);
        restUtilMock = mock(RestUtil.class);
        MetricsRegistry metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        stub(metricsRegistry.newCounter(anyString(), anyString()))
                .toReturn(counter);
        stub(taskContext.getMetricsRegistry()).toReturn(metricsRegistry);
        stub(envelope.getOffset()).toReturn("2");
        stub(envelope.getSystemStreamPartition()).toReturn(new SystemStreamPartition("kafka", "input.topic", new Partition(1)));
        stub(configMock.get("middleware.cassandra.host", "127.0.0.1")).toReturn("127.0.0.1");
        stub(configMock.get("middleware.cassandra.port", "9042")).toReturn("9042");
        stub(configMock.get("middleware.cassandra.courses_keyspace", "sunbird_courses")).toReturn("sunbird_courses");
        stub(configMock.get("middleware.cassandra.aggregator_table", "assessment_aggregator")).toReturn("assessment_aggregator");
        stub(configMock.get("middleware.cassandra.question_type", "question")).toReturn("question");
        stub(configMock.get("output.failed.topic.name", "telemetry.assess.failed")).toReturn("telemetry.assess.failed");
        UserType userType = mock(UserType.class);
        UDTValue udtValue = mock(UDTValue.class);
        when(udtValue.setString(anyString(), anyString())).thenReturn(udtValue);
        when(udtValue.setString(any(), any())).thenReturn(udtValue);
        when(udtValue.setDouble(anyString(), anyDouble())).thenReturn(udtValue);
        when(udtValue.setDecimal(anyString(), any())).thenReturn(udtValue);
        when(udtValue.setList(anyString(), anyList())).thenReturn(udtValue);
        when(udtValue.setTimestamp(anyString(), any())).thenReturn(udtValue);
        when(userType.newValue()).thenReturn(udtValue);
        cassandraConnect = mock(CassandraConnect.class);
        when(cassandraConnect.getUDTType("sunbird_courses", "question")).thenReturn(userType);
        when(restUtilMock.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(EventFixture.CONTENT_GET_RESPONSE);
        config = new AssessmentAggregatorConfig(configMock);
        assessmentAggregatorTask = new AssessmentAggregatorTask(configMock, taskContext, cassandraConnect, contentCache, restUtilMock);

    }

    @Test
    public void shouldUpdateCassandra() throws Exception {

        String event = EventFixture.BATCH_ASSESS_EVENT;
        BatchEvent batchEvent = new BatchEvent((Map<String, Object>) new Gson().fromJson(event, Map.class));
        stub(envelope.getMessage()).toReturn(event);
        assessmentAggregatorTask.process(envelope, collector, taskCoordinator);
        AssessmentAggregatorService assessmentAggregatorService = new AssessmentAggregatorService
                (cassandraConnect, config, contentCache, restUtilMock);
        AssessmentAggregatorSink sink = new AssessmentAggregatorSink(collector,
                new JobMetrics(taskContext, "AssessmentAggregator"), config);
        Aggregate assess = assessmentAggregatorService.getAggregateData(batchEvent, 1565198476000L);
        assertEquals(2.0, assess.getTotalMaxScore(), 0.001);
        assertEquals(1.33, assess.getTotalScore(), 0.001);
        assertEquals("1.33/2.0", assess.getGrandTotal());
    }

    @Test
    public void shouldSkipBatchIfAssessmentDateIsOlder() throws Exception {

        String event = EventFixture.BATCH_ASSESS__OLDER_EVENT;
        stub(envelope.getMessage()).toReturn(event);
        Row row = mock(Row.class);
        stub(row.getTimestamp("last_attempted_on")).toReturn(new Date(1567444876000L));
        when(cassandraConnect.findOne(anyString())).thenReturn(row);
        assessmentAggregatorTask.process(envelope, collector, taskCoordinator);
        verify(collector).send(argThat(validateOutputTopic(envelope.getMessage(), "telemetry.assess.failed")));
        verify(counter, times(2)).inc();
    }

    @Test
    public void shouldSendDuplicateBatchEventToFailedTopic() throws Exception {
        stub(envelope.getMessage()).toReturn(EventFixture.BATCH_ASSESS_FAIL_EVENT);
        AssessmentAggregatorService assessmentAggregatorService = new AssessmentAggregatorService(cassandraConnect, config, contentCache, restUtilMock);
        AssessmentAggregatorSource assessmentAggregatorSource = new AssessmentAggregatorSource(envelope);
        AssessmentAggregatorSink sink = new AssessmentAggregatorSink(collector, mock(JobMetrics.class), config);
        assessmentAggregatorService.process(assessmentAggregatorSource, sink);
        verify(collector).send(argThat(validateOutputTopic(envelope.getMessage(), "telemetry.assess.failed")));
    }

    @Test
    public void shouldTakeLatestAssessEventInBatchWhenDuplicates() throws Exception {
        String event = EventFixture.BATCH_DUPLICATE_QUESTION_EVENT;
        BatchEvent batchEvent = new BatchEvent((Map<String, Object>) new Gson().fromJson(event, Map.class));
        stub(envelope.getMessage()).toReturn(event);
        AssessmentAggregatorService assessmentAggregatorService = new AssessmentAggregatorService
                (cassandraConnect, config, contentCache, restUtilMock);
        Aggregate assess = assessmentAggregatorService.getAggregateData(batchEvent, 1565198476000L);
        assertEquals(2, assess.getQuestionsList().size());
        assertEquals(2.0, assess.getTotalMaxScore(), 0.001);
        assertEquals(2.0, assess.getTotalScore(), 0.001);

    }

    @Test
    public void shouldUpdateCassandraWithArrayValues() throws Exception {

        String event = EventFixture.QUESTION_EVENT_RES_VALUES;
        BatchEvent batchEvent = new BatchEvent((Map<String, Object>) new Gson().fromJson(event, Map.class));
        stub(envelope.getMessage()).toReturn(event);
        assessmentAggregatorTask.process(envelope, collector, taskCoordinator);
        AssessmentAggregatorService assessmentAggregatorService = new AssessmentAggregatorService
                (cassandraConnect, config, contentCache, restUtilMock);
        AssessmentAggregatorSink sink = new AssessmentAggregatorSink(collector,
                new JobMetrics(taskContext, "AssessmentAggregator"), config);
        Aggregate assess = assessmentAggregatorService.getAggregateData(batchEvent, new Date().getTime());
        assertEquals(1.0, assess.getTotalMaxScore(), 0.001);
        assertEquals(1.0, assess.getTotalScore(), 0.001);
        assertEquals("1.0/1.0", assess.getGrandTotal());
    }

    @Test
    public void shouldSkipBatchIfContentDoesNotBelongToCourse() throws Exception {
        String event = EventFixture.BATCH_ASSESS_EVENT_WITH_INVALID_CONTENT_ID;
        stub(envelope.getMessage()).toReturn(event);
        Row row = mock(Row.class);
        stub(row.getTimestamp("last_attempted_on")).toReturn(new Date(1567444876000L));
        when(cassandraConnect.findOne(anyString())).thenReturn(row);
        assessmentAggregatorTask.process(envelope, collector, taskCoordinator);
        verify(collector).send(argThat(validateOutputTopic(envelope.getMessage(), "telemetry.assess.failed")));
    }


    private ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
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
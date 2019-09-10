package org.ekstep.ep.samza.task;

import com.datastax.driver.core.Row;
import com.google.gson.Gson;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.ekstep.ep.samza.domain.Aggregate;
import org.ekstep.ep.samza.domain.BatchEvent;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.service.AssessmentAggregatorService;
import org.ekstep.ep.samza.util.DBUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class AssessmentAggregatorTest {

    private Config config;
    private IncomingMessageEnvelope envelope;
    private DBUtil dbUtil;

    @Before
    public void setup() {

        config = mock(Config.class);
        envelope = mock(IncomingMessageEnvelope.class);

        stub(envelope.getOffset()).toReturn("2");
        stub(envelope.getSystemStreamPartition()).toReturn(new SystemStreamPartition("kafka", "input.topic", new Partition(1)));
        stub(config.get("middleware.cassandra.host", "127.0.0.1")).toReturn("127.0.0.1");
        stub(config.get("middleware.cassandra.port", "9042")).toReturn("9042");
        stub(config.get("middleware.cassandra.courses_keyspace", "sunbird_courses")).toReturn("sunbird_courses");
        stub(config.get("middleware.cassandra.aggregator_table", "assessment_aggregator")).toReturn("assessment_aggregator");
        stub(config.get("middleware.cassandra.question_type", "question")).toReturn("question");
        dbUtil = mock(DBUtil.class);

    }

    @Test
    public void shouldUpdateCassandra() {

        String event = EventFixture.BATCH_ASSESS_EVENT;
        BatchEvent batchEvent = new BatchEvent((Map<String, Object>) new Gson().fromJson(event, Map.class));
        stub(envelope.getMessage()).toReturn(event);
        AssessmentAggregatorService assessmentAggregatorService = new AssessmentAggregatorService
                (dbUtil);
        Aggregate assess = assessmentAggregatorService.getAggregateData(batchEvent, 1565198476000L);
        assertEquals(11, assess.getTotalMaxScore());
        assertEquals(5, assess.getTotalScore());
    }

    @Test
    public void shouldSkipBatchIfAssessmentDateIsOlder() {

        String event = EventFixture.BATCH_ASSESS__OLDER_EVENT;
        BatchEvent batchEvent = new BatchEvent((Map<String, Object>) new Gson().fromJson(event, Map.class));
        stub(envelope.getMessage()).toReturn(event);
        AssessmentAggregatorService assessmentAggregatorService = new AssessmentAggregatorService(dbUtil);
        Row row = mock(Row.class);
        stub(row.getTimestamp("last_attempted_on")).toReturn(new Date(1567444876000L));
        boolean status = assessmentAggregatorService.isBatchEventValid(batchEvent, row);
        assertFalse(status);


    }


}
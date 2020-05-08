package org.ekstep.ep.samza.task;


import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

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
import org.ekstep.ep.samza.fixtures.TelemetryV3;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.Map;

public class TelemetryEventsValidatorTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.denorm.valid";
    private static final String FAILED_TOPIC = "telemetry.failed";
    private static final String MALFORMED_TOPIC = "telemetry.malformed";
    private static final String TELEMETRY_SCHEMA_PATH = "schemas/telemetry";
    private static final String SUMMARY_SCHEMA_PATH = "schemas/summary";
    private static final String DEFAULT_SCHEMA_NAME = "envelope.json";
    private static final String SEARCH_SCHEMA_NAME = "search.json";
    private static final String LOG_SCHEMA_NAME = "log.json";
    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private DruidEventsValidatorTask druidEventsValidatorTask;

    @Before
    public void setUp() throws Exception {
        collectorMock = mock(MessageCollector.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);

        when(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).thenReturn(SUCCESS_TOPIC);
        when(configMock.get("output.failed.topic.name", FAILED_TOPIC)).thenReturn(FAILED_TOPIC);
        when(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).thenReturn(MALFORMED_TOPIC);
        when(configMock.get("telemetry.schema.path", TELEMETRY_SCHEMA_PATH)).thenReturn(TELEMETRY_SCHEMA_PATH);
        when(configMock.get("summary.schema.path", SUMMARY_SCHEMA_PATH)).thenReturn(SUMMARY_SCHEMA_PATH);
        when(configMock.get("event.schema.file", DEFAULT_SCHEMA_NAME)).thenReturn(DEFAULT_SCHEMA_NAME);
        when(configMock.get("search.schema.file",SEARCH_SCHEMA_NAME)).thenReturn(SEARCH_SCHEMA_NAME);
        when(configMock.get("log.schema.file",LOG_SCHEMA_NAME)).thenReturn(LOG_SCHEMA_NAME);
        when(metricsRegistry.newCounter(anyString(), anyString())).thenReturn(counter);
        when(contextMock.getMetricsRegistry()).thenReturn(metricsRegistry);
        stub(envelopeMock.getOffset()).toReturn("2");
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.denorm", new Partition(1)));
        druidEventsValidatorTask = new DruidEventsValidatorTask(configMock, contextMock);
    }

    @Test
    public void shouldSendEventToSuccessTopicIfEventIsValid() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.VALID_EVENT);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(true, "")));
    }

    @Test
    public void shouldSendEventToFaildTopicIfEventIsNotValid() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_EVENT);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "contentdata/pkgversion")));
    }

    @Test
    public void shouldSendEventToFaildTopicIfInvalidDeviceData_CASE1() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_DEVICEDATA_CASE_1);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "devicedata/firstaccess")));
    }

    @Test
    public void shouldSendEventToFaildTopicIfInvalidDeviceData_CASE2() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_DEVICEDATA_CASE_2);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "devicedata/uaspec")));
    }

    @Test
    public void shouldSendEventToFaildTopicIfInvalidDeviceData_CASE3() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_DEVICEDATA_CASE_3);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "devicedata/country")));
    }

    @Test
    public void shouldSendEventToSuccessTopicIfInvalidDeviceData_CASE4() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_DEVICEDATA_CASE_4);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(true, "")));
    }

    @Test
    public void shouldSendEventToFaildTopicIfInvalidContentData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_CONTENTDATA);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "contentdata/language")));
    }

    @Test
    public void shouldSendEventToFaildTopicIfInvalidDialCodeData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_DIALCODEDATA);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "dialcodedata/objecttype")));
    }

    @Test
    public void shouldSendEventToFaildTopicIfInvalidUserData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_USERDATA);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "userdata/gradelist")));
    }

    @Test
    // Case sensitive dialcode keyword validation
    public void shouldSendEventToFaildTopicIfInvalidDialCodeKeywordAppears() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_DIALCODE_KEY);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
    }

    @Test
    // Should support for the both array and object type format for dialcodedata .
    public void shouldSendToSuccessTopic() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.VALID_DIALCODETYPE);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(true, "")));
    }

    @Test
    // When array of object properties is having invalid type ex: generatedon.
    public void shouldSendToFailedTopic() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_DIALCODETYPE_CASE_1);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "generatedon")));
    }

    @Test
    // When dialcodedata type is object and having invalid  property type ex: channel
    public void shouldSendEventToFailedTopic() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_DIALCODETYPE_CASE_2);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "channel")));
    }

    @Test
    public void shouldSendLogEventToSuccessTopic() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.VALID_LOG_EVENT);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(true, "")));
    }

    @Test
    public void shouldSendEventToSuccessTopicForValidUserDeclaredAndDerivedLocationData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.VALID_USER_DECLARED_AND_DERIVED_LOCATION_EVENT);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(true, "")));
    }

    @Test
    public void shouldSendEventToFaildTopicForInvalidDerivedLocationData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.IVALID_DERIVED_LOCATION_EVENT);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "derivedlocationdata/state")));
    }

    @Test
    public void shouldSendEventToFaildTopicForInvalidUserDeclaredLocationData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_USER_DECLARED_LOCATION_EVENT);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        verify(collectorMock).send(argThat(validateEvent(false, "devicedata/userdeclared/district")));
    }

    /**
     * When Json exception occurs then it should send the event to malformed topic
     * @throws Exception
     */
    @Test
    public void shouldAddInvalidEventToMalformedTopic() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(TelemetryV3.INVALID_JSON);
        druidEventsValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
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

    public ArgumentMatcher<OutgoingMessageEnvelope> validateEvent(final boolean dv_processed, final String field) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                Map<String, Object> event;
                Map<String, Boolean> flags;
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String message = (String) outgoingMessageEnvelope.getMessage();
                event = new Gson().fromJson(message, Map.class);
                flags = new Gson().fromJson(event.get("flags").toString(), Map.class);
                assertEquals(dv_processed, flags.get("dv_processed"));
                if (!dv_processed) {
                    assertEquals(true, event.get("metadata").toString().contains(field));
                }
                return true;
            }
        };
    }
}

package org.ekstep.ep.samza.task;


import com.google.common.reflect.TypeToken;
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
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.TelemetrySchemaValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.lang.reflect.Type;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class TelemetryValidatorTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.valid";
    private static final String FAILED_TOPIC = "telemetry.failed";
    private static final String MALFORMED_TOPIC = "telemetry.malformed";
    private static final String SCHEMA_PATH = "src/test/resources";
    private static final String SCHEMA_VERSION = "3.0";
    private MessageCollector collectorMock;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private TelemetryValidatorTask telemetryValidatorTask;

    @Before
    public void setUp() throws Exception {
        collectorMock = mock(MessageCollector.class);
        TaskContext contextMock = mock(TaskContext.class);
        MetricsRegistry metricsRegistry = mock(MetricsRegistry.class);

        Counter counter = mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        Config configMock = mock(Config.class);

        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);
        stub(configMock.get("telemetry.schema.path", "/etc/samza-jobs/schemas")).toReturn(SCHEMA_PATH);
        stub(configMock.get("telemetry.schema.version", "3.0")).toReturn(SCHEMA_VERSION);
        TelemetrySchemaValidator telemetrySchemaValidator = new TelemetrySchemaValidator(new TelemetryValidatorConfig(configMock));
        stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
        stub(envelopeMock.getOffset()).toReturn("2");
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "input.topic", new Partition(1)));


        telemetryValidatorTask = new TelemetryValidatorTask(configMock, contextMock, telemetrySchemaValidator);
    }

    @Test
    public void shouldSendEventToSuccessTopicIfEventIsValid() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_GE_ERROR_EVENT);

        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldSendEventToValidTopicIfSchemaNotFound() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.INVALID_GE_ERROR_EVENT);

        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldSendEventToMalformedTopicIfEventIsNotParseable() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.UNPARSABLE_GE_GENIE_UPDATE_EVENT);

        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
    }

    @Test
    public void shouldSendEventToMalformedTopicIfEventIsAnyRandomString() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.ANY_STRING);

        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
    }

    @Test
    public void shouldSendEventToSuccessTopicIfEventIsEmptyJSON() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.EMPTY_JSON);

        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldSendEventToSuccessTopicIfSchemaIsNotPresent() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_GE_INTERACT_EVENT);

        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldSendEventToFailedTopicIfEidNotPresent() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EVENT_WITH_EID_MISSING);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
    }

    @Test
    public void shouldRemoveFederatedUserIdPrefixIfPresent() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_GE_INTERACT_EVENT);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                Map<String, Object> actorData = new Gson().fromJson(new Gson().toJson(outputEvent.get("actor")), mapType);
                assertEquals("874ed8a5-782e-4f6c-8f36-e0288455901e", actorData.get("id"));
                return true;
            }
        }));
    }

    @Test
    public void whenInvalidEventisPassed() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INVALID_EVENT);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                Map<String, Object> flags = new Gson().fromJson(new Gson().toJson(outputEvent.get("flags")), mapType);
                assertEquals(false, flags.get("tv_processed"));
                return true;
            }
        }));
    }

    /**
     * When valid log event is passed then it should push to success topic
     *
     * @throws Exception
     */
    @Test
    public void shouldAddTheValidEventToSuccessTopic() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_LOG_EVENT);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    /**
     * When channel field is not present in the context then it should push the event to failed topic
     * @throws Exception
     */
    @Test
    public void shouldValidateTheChannelField() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_LOG_WITH_MISSING_CHANNEL);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                Map<String, Object> flags = new Gson().fromJson(new Gson().toJson(outputEvent.get("flags")), mapType);
                assertEquals(false, flags.get("tv_processed"));
                return true;
            }
        }));
    }

    /**
     * When synctc and @timestamp fields are missed, Then job should add these fileds to event.
     * @throws Exception
     */
    @Test
    public void shouldUpdateTheDefaultsToValidEvent() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.VALID_LOG_WITH_MISSING_SYNCTS);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                Map<String, Object> flags = new Gson().fromJson(new Gson().toJson(outputEvent.get("flags")), mapType);
                assertEquals(true, flags.get("tv_processed"));
                assertEquals(true, outputEvent.containsKey("@timestamp"));
                assertEquals(true, outputEvent.containsKey("syncts"));
                return true;
            }
        }));
    }

    @Test
    public void shouldConvertDialcodesKeytoLowerCaseIfPresent() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_INCORRECT_DIALCODES_KEY);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                Map<String, Object> edata = new Gson().fromJson(new Gson().toJson(outputEvent.get("edata")), mapType);
                Map<String, Object> edataFilters = new Gson().fromJson(new Gson().toJson(edata.get("filters")), mapType);
                assertTrue(edataFilters.containsKey("dialcodes"));
                assertFalse(edataFilters.containsKey("dialCodes"));
                return true;
            }
        }));
    }

    @Test
    public void shouldConvertDialcodeValueToUpperCaseIfPresentInLowerCase() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.IMPRESSION_EVENT_WITH_DIALCODE_OBJECT_IN_LOWERCASE);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                Map<String, Object> object = new Gson().fromJson(new Gson().toJson(outputEvent.get("object")), mapType);
                assertFalse(object.get("id").toString().equals("977d3i"));
                assertTrue(object.get("id").toString().equals("977D3I"));
                return true;
            }
        }));
    } @Test
    public void shouldNotConvertDialcodeValueToUpperCaseIfNotPresentInLowerCase() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.IMPRESSION_EVENT_WITH_DIALCODE_OBJECT_IN_UPPERCASE);
        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                Map<String, Object> object = new Gson().fromJson(new Gson().toJson(outputEvent.get("object")), mapType);
                assertTrue(object.get("id").toString().equals("977D3I"));
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventToFailedTopicIfEidMissing() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.EVENT_WITHOUT_EID);

        telemetryValidatorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                System.out.println(outputMessage);
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                Map<String, Object> flags = new Gson().fromJson(new Gson().toJson(outputEvent.get("flags")), mapType);
                Map<String, Object> metadata = new Gson().fromJson(new Gson().toJson(outputEvent.get("metadata")), mapType);
                assertEquals(false, flags.get("tv_processed"));
                assertEquals("validation failed. eid id missing", metadata.get("tv_error"));
                assertEquals("TelemetryValidator", metadata.get("src"));
                return true;
            }
        }));
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
package org.ekstep.ep.samza.task;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.gson.Gson;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixtures.Telemetry;
import org.ekstep.ep.samza.util.SchemaValidator;
import org.junit.Before;
import org.junit.Test;

import org.apache.samza.config.Config;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;


public class TelemetryEventValidatorTest {

    private SchemaValidator validator;
    private DruidProcessorConfig configMock;

    @Before
    public void setUp() throws IOException, ProcessingException {
        configMock = mock(DruidProcessorConfig.class);
        validator = new SchemaValidator(configMock);
    }

    @Test
    public void shouldSendEventToSuccessTopicIfEventIsValid() throws Exception {
        Map<String, Object> jsonMap = (Map<String, Object>) new Gson().fromJson(Telemetry.VALID_EVENT, Map.class);
        Event event = new Event(jsonMap);
        stub(configMock.telemetrySchemaPath()).toReturn("schemas/telemetry");
        stub(configMock.telemetrySchemaPath()).toReturn("schemas/summary");
        stub(configMock.telemetrySchemaPath()).toReturn("schemas/telemetry");
        stub(configMock.defaultSchemafile()).toReturn("envelope.json");
        ProcessingReport report = validator.validate(event);
        assertTrue(report.isSuccess());
    }

    /*
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
        verify(collectorMock).send(argThat(validateEvent(false, "dialcodedata/status")));
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
    */

}

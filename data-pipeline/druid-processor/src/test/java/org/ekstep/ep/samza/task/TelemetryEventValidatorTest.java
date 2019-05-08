package org.ekstep.ep.samza.task;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.gson.Gson;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixtures.Telemetry;
import org.ekstep.ep.samza.util.SchemaValidator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.mock;


public class TelemetryEventValidatorTest {

    private SchemaValidator validator;
    private DruidProcessorConfig configMock;

    @Before
    public void setUp() throws IOException, ProcessingException {
        configMock = mock(DruidProcessorConfig.class);
        stub(configMock.telemetrySchemaPath()).toReturn("schemas/telemetry");
        stub(configMock.summarySchemaPath()).toReturn("schemas/summary");
        stub(configMock.defaultSchemafile()).toReturn("envelope.json");
        validator = new SchemaValidator(configMock);
    }

    private Event convertJsonToEvent(String jsonEvent) {
        Map<String, Object> jsonMap = (Map<String, Object>) new Gson().fromJson(jsonEvent, Map.class);
        return new Event(jsonMap);
    }

    @Test
    public void validateValidEvent() throws Exception {
        Event event = convertJsonToEvent(Telemetry.VALID_EVENT);
        ProcessingReport report = validator.validate(event);
        assertTrue(report.isSuccess());
    }

    @Test
    public void validateInvalidEvent() throws Exception {
        Event event = convertJsonToEvent(Telemetry.INVALID_EVENT);
        ProcessingReport report = validator.validate(event);
        assertFalse(report.isSuccess());
    }

    @Test
    public void validateInvalidDeviceDataFirstAccessField() throws Exception {
        Event event = convertJsonToEvent(Telemetry.INVALID_DEVICEDATA_CASE_1);
        ProcessingReport report = validator.validate(event);
        assertFalse(report.isSuccess());
        String fieldName = validator.getInvalidFieldName(report.toString());
        assertEquals("devicedata/firstaccess", fieldName);
    }

    @Test
    public void validateInvalidDeviceDataUaspecField() throws Exception {

        Event event = convertJsonToEvent(Telemetry.INVALID_DEVICEDATA_CASE_2);
        ProcessingReport report = validator.validate(event);
        assertFalse(report.isSuccess());
        String fieldName = validator.getInvalidFieldName(report.toString());
        assertEquals("devicedata/uaspec", fieldName);

    }

    @Test
    public void validateInvalidDeviceDataCountryField() throws Exception {

        Event event = convertJsonToEvent(Telemetry.INVALID_DEVICEDATA_CASE_3);
        ProcessingReport report = validator.validate(event);
        assertFalse(report.isSuccess());
        String fieldName = validator.getInvalidFieldName(report.toString());
        assertEquals("devicedata/country", fieldName);

    }

    @Test
    public void validateIncorrectFieldDataType() throws Exception {

        Event event = convertJsonToEvent(Telemetry.INVALID_DEVICEDATA_CASE_4);
        ProcessingReport report = validator.validate(event);
        assertTrue(report.isSuccess());

    }

    @Test
    public void validateInvalidContentData() throws Exception {

        Event event = convertJsonToEvent(Telemetry.INVALID_CONTENTDATA);
        ProcessingReport report = validator.validate(event);
        assertFalse(report.isSuccess());
        String fieldName = validator.getInvalidFieldName(report.toString());
        assertEquals("contentdata/language", fieldName);

    }

    @Test
    public void validateInvalidDialCodeData() throws Exception {

        Event event = convertJsonToEvent(Telemetry.INVALID_DIALCODEDATA);
        ProcessingReport report = validator.validate(event);
        assertFalse(report.isSuccess());
        String fieldName = validator.getInvalidFieldName(report.toString());
        assertEquals("dialcodedata/status", fieldName);

    }

    @Test
    public void validateInvalidUserData() throws Exception {

        Event event = convertJsonToEvent(Telemetry.INVALID_USERDATA);
        ProcessingReport report = validator.validate(event);
        assertFalse(report.isSuccess());
        String fieldName = validator.getInvalidFieldName(report.toString());
        assertEquals("userdata/gradelist", fieldName);

    }

    @Test
    // Case sensitive dialcode keyword validation
    public void validateInvalidDialCodeKeywordAppears() throws Exception {

        Event event = convertJsonToEvent(Telemetry.INVALID_DIALCODE_KEY);
        ProcessingReport report = validator.validate(event);
        assertFalse(report.isSuccess());

    }

    @Test
    // Should support for the both array and object type format for dialcodedata .
    public void validateValidDialcodeData() throws Exception {

        Event event = convertJsonToEvent(Telemetry.VALID_DIALCODETYPE);
        ProcessingReport report = validator.validate(event);
        assertTrue(report.isSuccess());

    }

    @Test
    // When array of object properties is having invalid type ex: generatedon.
    public void validateInvalidDataInArrayField() throws Exception {

        Event event = convertJsonToEvent(Telemetry.INVALID_DIALCODETYPE_CASE_1);
        ProcessingReport report = validator.validate(event);
        assertFalse(report.isSuccess());
        String fieldName = validator.getInvalidFieldName(report.toString());
        assertEquals("dialcodedata/1/generatedon", fieldName);

    }

    @Test
    // When dialcodedata type is object and having invalid  property type ex: channel
    public void validateInvalidDialCodeProperties() throws Exception {

        Event event = convertJsonToEvent(Telemetry.INVALID_DIALCODETYPE_CASE_2);
        ProcessingReport report = validator.validate(event);
        assertFalse(report.isSuccess());
        String fieldName = validator.getInvalidFieldName(report.toString());
        assertEquals("dialcodedata/channel", fieldName);

    }

}

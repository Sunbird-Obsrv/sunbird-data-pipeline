package org.ekstep.ep.samza.service;

import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.config.DataDenormalizationConfig;
import org.ekstep.ep.samza.config.EventDenormalizationConfig;
import org.ekstep.ep.samza.config.ObjectDenormalizationAdditionalConfig;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixture.EventFixture;
import org.ekstep.ep.samza.fixture.GetObjectFixture;
import org.ekstep.ep.samza.object.service.ObjectService;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.config.ObjectDeNormalizationConfig;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSink;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ObjectDeNormalizationServiceTest {
    @Mock
    private ObjectDeNormalizationConfig config;
    @Mock
    private ObjectDeNormalizationSource source;
    @Mock
    private ObjectDeNormalizationSink sink;
    @Mock
    private ObjectService objectService;

    private ObjectDenormalizationAdditionalConfig additionalConfig;
    private ObjectDeNormalizationService denormalizationService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(config.retryStore()).thenReturn(new KeyValueStore<String, Object>() {
            @Override
            public Object get(String key) {
                return null;
            }

            @Override
            public Map<String, Object> getAll(List<String> keys) {
                return null;
            }

            @Override
            public void put(String key, Object value) {

            }

            @Override
            public void putAll(List<Entry<String, Object>> entries) {

            }

            @Override
            public void delete(String key) {

            }

            @Override
            public void deleteAll(List<String> keys) {

            }

            @Override
            public KeyValueIterator<String, Object> range(String from, String to) {
                return null;
            }

            @Override
            public KeyValueIterator<String, Object> all() {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public void flush() {

            }
        });
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSkipEventsWhichDontNeedDenormalization() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Portal events", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("uid", "portaluserdata")))));
        Event event = new Event(new Telemetry(EventFixture.event()),config);
        when(config.fieldsToDenormalize()).thenReturn(asList("id", "type", "subtype", "parentid", "parenttype", "code", "name"));
        when(config.defaultChannel()).thenReturn("in.ekstep");
        when(source.getEvent(config)).thenReturn(event);

        denormalizationService = new ObjectDeNormalizationService(config, additionalConfig, objectService);
        denormalizationService.process(source, sink);

        verify(sink).toSuccessTopic(argThat(validateSkippedEvent()));
    }

    @Test
    public void shouldDenormalizeEventWhichIsConfiguredToBeDenormalized() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Portal events", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("uid", "portaluserdata")))));
        Event event = new Event(new Telemetry(EventFixture.cpInteractEvent()),config);
        when(config.fieldsToDenormalize()).thenReturn(asList("id", "type", "subtype", "parentid", "parenttype", "code", "name"));
        when(config.defaultChannel()).thenReturn("in.ekstep");
        when(source.getEvent(config)).thenReturn(event);
        when(objectService.get("111","in.ekstep")).thenReturn(GetObjectFixture.getObjectSuccessResponse());

        denormalizationService = new ObjectDeNormalizationService(config, additionalConfig, objectService);
        denormalizationService.process(source, sink);

        Event expectedEvent = new Event(new Telemetry(EventFixture.denormalizedCpInteractEvent()),config);
        verify(sink).toSuccessTopic(argThat(validateEvent(expectedEvent)));
        verify(objectService).get("111","in.ekstep");
    }

    @Test
    public void shouldDenormalizeEventWithoutDetailsFieldsWhenJsonIsInvalid() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Portal events", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("uid", "portaluserdata")))));

        Event event = new Event(new Telemetry(EventFixture.cpInteractEvent()),config);
        when(config.fieldsToDenormalize()).thenReturn(asList("id", "type", "subtype", "parentid", "parenttype", "code", "name"));
        when(config.defaultChannel()).thenReturn("in.ekstep");
        when(source.getEvent(config)).thenReturn(event);
        when(objectService.get("111","in.ekstep")).thenReturn(GetObjectFixture.getObjectSuccessResponseWithMalformedDetails());

        denormalizationService = new ObjectDeNormalizationService(config, additionalConfig, objectService);
        denormalizationService.process(source, sink);

        Event expectedEvent = new Event(new Telemetry(EventFixture.denormalizedCpInteractEventWithoutDetails()),config);
        verify(sink).toSuccessTopic(argThat(validateEvent(expectedEvent)));
        verify(objectService).get("111","in.ekstep");
    }

    @Test
    public void shouldDenormalizeEventWithoutDetailsFieldsWhenDetailsIsNotPresent() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Portal events", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("uid", "portaluserdata")))));
        Event event = new Event(new Telemetry(EventFixture.cpInteractEvent()),config);
        when(config.fieldsToDenormalize()).thenReturn(asList("id", "type", "subtype", "parentid", "parenttype", "code", "name"));
        when(config.defaultChannel()).thenReturn("in.ekstep");
        when(source.getEvent(config)).thenReturn(event);
        when(objectService.get("111","in.ekstep")).thenReturn(GetObjectFixture.getObjectSuccessResponseWithNoDetails());

        denormalizationService = new ObjectDeNormalizationService(config, additionalConfig, objectService);
        denormalizationService.process(source, sink);

        Event expectedEvent = new Event(new Telemetry(EventFixture.denormalizedCpInteractEventWithoutDetails()),config);
        verify(sink).toSuccessTopic(argThat(validateEvent(expectedEvent)));
        verify(objectService).get("111","in.ekstep");
    }

    @Test
    public void shouldSinkEventToRetryTopicWhenServiceReturnsError() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Portal events", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("uid", "portaluserdata")))));
        Event event = new Event(new Telemetry(EventFixture.cpInteractEvent()),config);
        when(config.fieldsToDenormalize()).thenReturn(asList("id", "type", "subtype", "parentid", "parenttype", "code", "name"));
        when(config.defaultChannel()).thenReturn("in.ekstep");
        when(source.getEvent(config)).thenReturn(event);
        when(objectService.get("111","in.ekstep")).thenReturn(GetObjectFixture.getFailureResponse());

        denormalizationService = new ObjectDeNormalizationService(config, additionalConfig, objectService);
        denormalizationService.process(source, sink);

        verify(sink).toRetryTopic(argThat(validateRetriedEvent("BAD_REQUEST", "TYPE IS MANDATORY, ID IS MANDATORY")));
        verify(objectService).get("111","in.ekstep");
    }

    @Test
    public void shouldDenormalizePartnerData() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Partner data", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("tags.partnerid", "partnerdata")))));
        Event event = new Event(new Telemetry(EventFixture.cpInteractEventForPartner()),config);
        when(config.fieldsToDenormalize()).thenReturn(asList("id", "type", "subtype", "parentid", "parenttype", "code", "name"));
        when(config.defaultChannel()).thenReturn("in.ekstep");
        when(source.getEvent(config)).thenReturn(event);
        when(objectService.get("org.ekstep.partner.partner1","in.ekstep")).thenReturn(GetObjectFixture.getPartnerObjectSuccessResponse());

        denormalizationService = new ObjectDeNormalizationService(config, additionalConfig, objectService);
        denormalizationService.process(source, sink);

        Event expectedEvent = new Event(new Telemetry(EventFixture.denormalizedCpInteractEventForParther()),config);
        verify(sink).toSuccessTopic(argThat(validateEvent(expectedEvent)));
        verify(objectService).get("org.ekstep.partner.partner1","in.ekstep");
    }
//
    private ArgumentMatcher<Event> validateEvent(final Event expectedEvent) {
        return new ArgumentMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                Event actualEvent = (Event) o;
                assertThat(readValue(actualEvent, "portaluserdata.id"), is(readValue(expectedEvent, "portaluserdata.id")));
                assertThat(readValue(actualEvent, "portaluserdata.type"), is(readValue(expectedEvent, "portaluserdata.type")));
                assertThat(readValue(actualEvent, "portaluserdata.subtype"), is(readValue(expectedEvent, "portaluserdata.subtype")));
                assertThat(readValue(actualEvent, "portaluserdata.parentid"), is(readValue(expectedEvent, "portaluserdata.parentid")));
                assertThat(readValue(actualEvent, "portaluserdata.parenttype"), is(readValue(expectedEvent, "portaluserdata.parenttype")));
                assertThat(readValue(actualEvent, "portaluserdata.code"), is(readValue(expectedEvent, "portaluserdata.code")));
                assertThat(readValue(actualEvent, "portaluserdata.name"), is(readValue(expectedEvent, "portaluserdata.name")));

                //Details field
                assertThat(readValue(actualEvent, "portaluserdata.email"), is(readValue(expectedEvent, "portaluserdata.email")));
                assertThat(readValue(actualEvent, "portaluserdata.channel"), is(readValue(expectedEvent, "portaluserdata.channel")));
                assertNull(actualEvent.<Boolean>read("flags.od_skipped").value());
                assertTrue(actualEvent.<Boolean>read("flags.od_processed").value());
                return true;
            }
        };
    }
//
    private ArgumentMatcher<Event> validateSkippedEvent() {
        return new ArgumentMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                Event actualEvent = (Event) o;
                assertTrue(actualEvent.<Boolean>read("flags.od_skipped").value());
                assertNull(actualEvent.<Boolean>read("flags.od_processed").value());
                return true;
            }
        };
    }

    private ArgumentMatcher<Event> validateRetriedEvent(final String err, final String errmsg) {
        return new ArgumentMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                Event actualEvent = (Event) o;
                assertNull(actualEvent.<Boolean>read("flags.od_skipped").value());
                assertNull(actualEvent.<Boolean>read("flags.od_processed").value());
                assertThat(readValue(actualEvent, "metadata.od_err"), is(err));
                assertThat(readValue(actualEvent, "metadata.od_errmsg"), is(errmsg));
                return true;
            }
        };
    }
//
    private String readValue(Event event, String path) {
        return event.<String>read(path).value();
    }
//
}
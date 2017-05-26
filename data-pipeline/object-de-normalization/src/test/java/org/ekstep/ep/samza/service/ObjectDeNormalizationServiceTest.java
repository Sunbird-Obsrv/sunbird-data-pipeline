package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.config.DataDenormalizationConfig;
import org.ekstep.ep.samza.config.EventDenormalizationConfig;
import org.ekstep.ep.samza.config.ObjectDenormalizationAdditionalConfig;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixture.EventFixture;
import org.ekstep.ep.samza.fixture.GetObjectFixture;
import org.ekstep.ep.samza.object.service.ObjectService;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.ObjectDeNormalizationConfig;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSink;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
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
        Event event = new Event(new Telemetry(EventFixture.event()));
        when(config.fieldsToDenormalize()).thenReturn(asList("id", "type", "subtype", "parentid", "parenttype", "code", "name"));
        when(source.getEvent()).thenReturn(event);

        denormalizationService = new ObjectDeNormalizationService(config, additionalConfig, objectService);
        denormalizationService.process(source, sink);

        verify(sink).toSuccessTopic(event);
    }

    @Test
    public void shouldDenormalizeEventWhichIsConfiguredToBeDenormalized() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Portal events", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("uid", "portaluserdata")))));
        Event event = new Event(new Telemetry(EventFixture.cpInteractEvent()));
        when(config.fieldsToDenormalize()).thenReturn(asList("id", "type", "subtype", "parentid", "parenttype", "code", "name"));
        when(source.getEvent()).thenReturn(event);
        when(objectService.get("111")).thenReturn(GetObjectFixture.getObjectSuccessResponse());

        denormalizationService = new ObjectDeNormalizationService(config, additionalConfig, objectService);
        denormalizationService.process(source, sink);

        Event expectedEvent = new Event(new Telemetry(EventFixture.denormalizedCpInteractEvent()));
        verify(sink).toSuccessTopic(argThat(validateEvent(expectedEvent)));
        verify(objectService).get("111");
    }

    @Test
    public void shouldDenormalizeEventWithoutDetailsFieldsWhenJsonIsInvalid() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Portal events", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("uid", "portaluserdata")))));
        Event event = new Event(new Telemetry(EventFixture.cpInteractEvent()));
        when(config.fieldsToDenormalize()).thenReturn(asList("id", "type", "subtype", "parentid", "parenttype", "code", "name"));
        when(source.getEvent()).thenReturn(event);
        when(objectService.get("111")).thenReturn(GetObjectFixture.getObjectSuccessResponseWithMalformedDetails());

        denormalizationService = new ObjectDeNormalizationService(config, additionalConfig, objectService);
        denormalizationService.process(source, sink);

        Event expectedEvent = new Event(new Telemetry(EventFixture.denormalizedCpInteractEventWithoutDetails()));
        verify(sink).toSuccessTopic(argThat(validateEvent(expectedEvent)));
        verify(objectService).get("111");
    }

    @Test
    public void shouldSinkEventToBothSuccessAndFailedTopicWhenServiceReturnsError() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Portal events", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("uid", "portaluserdata")))));
        Event event = new Event(new Telemetry(EventFixture.cpInteractEvent()));
        when(config.fieldsToDenormalize()).thenReturn(asList("id", "type", "subtype", "parentid", "parenttype", "code", "name"));
        when(source.getEvent()).thenReturn(event);
        when(objectService.get("111")).thenReturn(GetObjectFixture.getFailureResponse());

        denormalizationService = new ObjectDeNormalizationService(config, additionalConfig, objectService);
        denormalizationService.process(source, sink);

        verify(sink).toSuccessTopic(event);
        verify(sink).toFailedTopic(event);
        verify(objectService).get("111");
    }

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
                return true;
            }
        };
    }

    private String readValue(Event event, String path) {
        return event.read(path).value();
    }

}
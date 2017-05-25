package org.ekstep.ep.samza.service;

import org.apache.samza.metrics.MetricsRegistry;
import org.ekstep.ep.samza.config.DataDenormalizationConfig;
import org.ekstep.ep.samza.config.EventDenormalizationConfig;
import org.ekstep.ep.samza.config.ObjectDenormalizationAdditionalConfig;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixture.EventFixture;
import org.ekstep.ep.samza.task.ObjectDeNormalizationConfig;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSink;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ObjectDeNormalizationServiceTest {
    @Mock
    private MetricsRegistry metricsRegistry;
    @Mock
    private ObjectDeNormalizationConfig config;
    @Mock
    private ObjectDeNormalizationSource source;
    @Mock
    private ObjectDeNormalizationSink sink;

    private ObjectDenormalizationAdditionalConfig additionalConfig;
    private ObjectDeNormalizationService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSkipEventsWhichDontNeedDenormalization() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Portal events", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("uid", "portaluserdata")))));
        service = new ObjectDeNormalizationService(config, additionalConfig);
        Event event = new Event(EventFixture.event());
        when(source.getEvent()).thenReturn(event);

        service.process(source, sink);

        verify(sink).toSuccessTopic(event);
    }

    @Test
    public void shouldDenormalizeEventWhichIsConfiguredToBeDenormalized() throws Exception {
        additionalConfig = new ObjectDenormalizationAdditionalConfig(
                asList(new EventDenormalizationConfig("Portal events", "C[PE]\\_.*",
                        asList(new DataDenormalizationConfig("uid", "portaluserdata")))));
        service = new ObjectDeNormalizationService(config, additionalConfig);
        Event event = new Event(EventFixture.cpInteractEvent());
        when(source.getEvent()).thenReturn(event);

        service.process(source, sink);

        verify(sink).toSuccessTopic(event);
    }
}
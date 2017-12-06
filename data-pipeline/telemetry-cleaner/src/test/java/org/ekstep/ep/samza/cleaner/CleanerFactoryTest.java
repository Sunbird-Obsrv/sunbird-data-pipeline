package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CleanerFactoryTest {
    @Test
    public void shouldSkipNonPublicEvents() throws Exception {
        List<String> nonPublicEvent = Arrays.asList("ME_.*");
        CleanerFactory cleanerFactory = new CleanerFactory(nonPublicEvent);

        assertTrue(cleanerFactory.shouldSkipEvent("ME_SESSION_SUMMARY"));
    }

    @Test
    public void shouldCleanAllOtherEvents() throws Exception {
        List<String> nonPublicEvent = Arrays.asList("ME_.*");
        Map<String,Object> eventMap = EventFixture.LearningEvent();
        CleanerFactory cleaner = new CleanerFactory(nonPublicEvent);

        cleaner.clean(new Telemetry(eventMap));

        assertThat(eventMap, not(hasKey("metadata")));
        assertThat(eventMap, not(hasKey("flags")));
    }
}
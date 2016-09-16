package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.fixtures.EventFixture;
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
        List<String> publicEvent = Arrays.asList("GE.*");
        CleanerFactory cleanerFactory = new CleanerFactory(publicEvent,nonPublicEvent);

        assertTrue(cleanerFactory.shouldSkipEvent("ME_SESSION_SUMMARY"));
    }

    @Test
    public void shouldCleanAllOtherEvents() throws Exception {
        List<String> nonPublicEvent = Arrays.asList("ME_.*");
        List<String> publicEvent = Arrays.asList("GE.*");
        Map<String,Object> eventMap = EventFixture.LearningEvent();
        CleanerFactory cleaner = new CleanerFactory(publicEvent,nonPublicEvent);

        cleaner.clean(eventMap);

        Map<String, Object> dspec = (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) eventMap.get("edata")).get("eks")).get("dspec");

        assertThat(dspec, not(hasKey("dlocname")));
        assertThat(dspec, not(hasKey("dname")));
        assertThat(dspec, not(hasKey("id")));
    }
}
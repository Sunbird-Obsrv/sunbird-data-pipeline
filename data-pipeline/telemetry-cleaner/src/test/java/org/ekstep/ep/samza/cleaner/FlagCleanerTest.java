package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.*;

public class FlagCleanerTest {
    @Test
    public void shouldRemoveAllFlags() throws Exception {
        Map<String, Object> eventMap = EventFixture.CreateProfile();
        FlagCleaner flagCleaner = new FlagCleaner();

        flagCleaner.clean(eventMap);

        Map<String, Object> flags = (Map<String, Object>) eventMap.get("flags");
        assertThat(flags, not(hasKey("ldata_processed")));
        assertThat(flags, not(hasKey("ldata_obtained")));
        assertThat(flags, not(hasKey("child_data_processed")));
    }
}
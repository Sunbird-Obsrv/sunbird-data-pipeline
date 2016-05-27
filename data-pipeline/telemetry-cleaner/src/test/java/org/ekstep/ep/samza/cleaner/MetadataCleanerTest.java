package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.*;

public class MetadataCleanerTest {
    @Test
    public void shouldRemoveAllFlags() throws Exception {
        Map<String, Object> eventMap = EventFixture.CreateProfile();
        MetadataCleaner metadataCleaner = new MetadataCleaner();

        metadataCleaner.clean(eventMap);

        assertThat(eventMap, not(hasKey("metadata")));
        assertThat(eventMap, not(hasKey("flags")));
        assertThat(eventMap, not(hasKey("ready_to_index")));
        assertThat(eventMap, not(hasKey("type")));
        assertThat(eventMap, not(hasKey("key")));
        assertThat(eventMap, not(hasKey("@version")));
        assertThat(eventMap, not(hasKey("@timestamp")));
    }
}
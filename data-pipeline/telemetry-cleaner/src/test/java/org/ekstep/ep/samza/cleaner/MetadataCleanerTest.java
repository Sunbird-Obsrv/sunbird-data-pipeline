package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

public class MetadataCleanerTest {
    @Test
    public void shouldRemoveAllFlags() throws Exception {
        Map<String, Object> eventMap = EventFixture.CreateProfile();
        MetadataCleaner metadataCleaner = new MetadataCleaner();

        metadataCleaner.clean(new Telemetry(eventMap));

        assertThat(eventMap, not(hasKey("metadata")));
        assertThat(eventMap, not(hasKey("flags")));
        assertThat(eventMap, not(hasKey("ready_to_index")));
        assertThat(eventMap, not(hasKey("key")));

    }
}
package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

public class LocationDataCleanerTest {

    @Test
    public void ShouldRemoveLocationDataFromEks() throws Exception {
        Map<String, Object> eventMap = EventFixture.GenieStart();
        LocationDataCleaner locationDataCleaner = new LocationDataCleaner();

        locationDataCleaner.clean(new Telemetry(eventMap));

        Map<String, Object> edata = (Map<String, Object>) eventMap.get("edata");
        Map<String, Object> ldata = (Map<String, Object>) eventMap.get("ldata");
        assertThat(edata, not(hasKey("loc")));
        assertThat(eventMap, hasKey("ldata"));
        assertThat(ldata, hasKey("state"));
        assertThat(ldata, hasKey("locality"));
        assertThat(ldata, hasKey("district"));
        assertThat(ldata, hasKey("country"));
    }
}
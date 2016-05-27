package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.*;

public class DeviceDataCleanerTest {
    @Test
    public void ShouldRemoveChildInformationInUdata() throws Exception {
        Map<String, Object> eventMap = EventFixture.GenieStart();
        DeviceDataCleaner deviceDataCleaner = new DeviceDataCleaner();

        deviceDataCleaner.clean(eventMap);

        Map<String, Object> dspec = (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) eventMap.get("edata")).get("eks")).get("dspec");

        assertThat(dspec, not(hasKey("dlocname")));
        assertThat(dspec, not(hasKey("dname")));
        assertThat(dspec, not(hasKey("id")));
    }

    @Test
    public void ShouldHandleEventsWithoutDeviseSpec() throws Exception {
        Map<String, Object> eventMap = EventFixture.CreateProfile();
        DeviceDataCleaner deviceDataCleaner = new DeviceDataCleaner();

        deviceDataCleaner.clean(eventMap);

        Map<String, Object> eks = ((Map<String, Object>) ((Map<String, Object>) eventMap.get("edata")).get("eks"));

        assertThat(eks, not(hasKey("dspec")));
    }
}
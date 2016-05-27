package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

public class ChildDataCleanerTest {

    @Test
    public void ShouldRemoveChildInformationInUdata() throws Exception {
        Map<String, Object> eventMap = EventFixture.CreateProfile();
        ChildDataCleaner childDataCleaner = new ChildDataCleaner();

        childDataCleaner.clean(eventMap);

        Map<String, Object> udata = (Map<String, Object>) eventMap.get("udata");
        assertThat(udata, not(hasKey("is_group_user")));
        assertThat(udata, not(hasKey("handle")));
        assertThat(udata, not(hasKey("gender")));

        assertThat(udata, hasKey("standard"));
        assertThat(eventMap, hasKey("uid"));
        assertThat(udata, hasKey("age_completed_years"));
    }

    @Test
    public void ShouldRemoveChildInformationInEdataEks() throws Exception {
        Map<String, Object> eventMap = EventFixture.CreateProfile();
        ChildDataCleaner childDataCleaner = new ChildDataCleaner();

        childDataCleaner.clean(eventMap);

        Map<String, Object> eks = (Map<String, Object>) ((Map<String, Object>) eventMap.get("edata")).get("eks");
        assertThat(eks, not(hasKey("day")));
        assertThat(eks, not(hasKey("gender")));
        assertThat(eks, not(hasKey("handle")));
        assertThat(eks, not(hasKey("is_group_user")));
        assertThat(eks, not(hasKey("loc")));
        assertThat(eks, not(hasKey("month")));

        assertThat(eks, hasKey("age"));
        assertThat(eks, hasKey("language"));
        assertThat(eks, hasKey("standard"));
        assertThat(eks, hasKey("uid"));
    }
}
package org.ekstep.ep.samza.domain;

import java.util.Map;
import java.util.HashMap;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventTest {

    @Test
    public void shouldUpdateEventWithAlteredETSField() throws Exception {

        Map eventMap = new HashMap();
        Long ets = new DateTime().plusDays(2).getMillis();
        eventMap.put("ets", ets);
        Event input = new Event(eventMap);
        Long output = input.compareAndAlterEts();
        assertTrue(output < ets);
    }

    @Test
    public void shouldNotUpdateEventWithETSField() throws Exception {

        Map eventMap = new HashMap();
        Long ets = new DateTime().getMillis();
        eventMap.put("ets", ets);
        Event input = new Event(eventMap);
        Long output = input.compareAndAlterEts();
        assertTrue(output == ets);
    }

    @Test
    public void shouldReturnTrueForOlderData() throws Exception {

        Map eventMap = new HashMap();
        Long ets = new DateTime().minusMonths(7).getMillis();
        eventMap.put("ets", ets);
        Event input = new Event(eventMap);
        assertTrue(input.isOlder(6));
    }

    @Test
    public void shouldReturnFalseForOlderData() throws Exception {

        Map eventMap = new HashMap();
        Long ets = new DateTime().minusMonths(3).getMillis();
        eventMap.put("ets", ets);
        Event input = new Event(eventMap);
        assertFalse(input.isOlder(6));
    }

    @Test
    public void shouldReturnLatestVersion() throws Exception {

        Map eventMap = new HashMap();
        eventMap.put("ver", "3.0");
        Event input = new Event(eventMap);
        String updatedVer = input.getUpgradedVersion();
        assertTrue(updatedVer.equals("3.1"));
    }

    @Test
    public void shouldReturnSummaryLatestVersion() throws Exception {

        Map eventMap = new HashMap();
        eventMap.put("ver", "2.1");
        Event input = new Event(eventMap);
        String updatedVer = input.getUpgradedVersion();
        assertTrue(updatedVer.equals("2.2"));
    }
}

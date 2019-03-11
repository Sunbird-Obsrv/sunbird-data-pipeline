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
        assertTrue(input.isOlder());
    }

    @Test
    public void shouldReturnFalseForOlderData() throws Exception {

        Map eventMap = new HashMap();
        Long ets = new DateTime().minusMonths(3).getMillis();
        eventMap.put("ets", ets);
        Event input = new Event(eventMap);
        assertFalse(input.isOlder());
    }
}

package org.ekstep.ep.samza;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixture.EventFixture;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class EventTest {

    HashMap<String, Object> itemEventMap = new HashMap<String, Object>();

    @Before
    public void setUp() {
        itemEventMap.put("ME", asList("edata", "eks", "itemId"));
        itemEventMap.put("ME_ITEM_USAGE_SUMMARY", asList("dimensions", "item_id"));
    }

    @Test
    public void shouldGetItemIdOfEventTypeWhoseItemIdIsExplicitlyOverridden() throws Exception {
        System.out.println(itemEventMap);
        Event e = new Event(EventFixture.MeItemUsageSummaryEvent(), itemEventMap);

        assertEquals("domain_4502", e.itemId());
    }

    @Test
    public void shouldGetItemIdOfEventTypeWhoseItemIdOverriddenByEidPrefix() throws Exception {
        Event e = new Event(EventFixture.MeItemSummaryEvent(), itemEventMap);

        assertEquals("domain_4502", e.itemId());
    }

}
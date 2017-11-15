package org.ekstep.ep.samza;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixture.EventFixture;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class EventTest {

    HashMap<String, Object> contentTaxonomy = new HashMap<String,Object>();

    @Before
    public void setUp() {
        createContentTaxonomy();
    }

    @Test
    public void testGetContentIdOfOeEvent() throws Exception {
        Event e = new Event(EventFixture.OeEvent(), contentTaxonomy);

        assertEquals("do_30076072", e.getObjectID());
    }

    @Test
    public void testGetContentIdOfMeEvent() throws Exception {
        Event e = new Event(EventFixture.MeEvent(), contentTaxonomy);

        assertEquals("do_30076072", e.getObjectID());
    }

    @Test
    public void testGetContentIdReturnsNullWhenItIsAbsent() throws Exception {
        Event e = new Event(EventFixture.EventWithoutObjectID(), contentTaxonomy);

        assertEquals(null, e.getObjectID());
    }

    private void createContentTaxonomy() {
        contentTaxonomy.put("ME", asList("dimensions", "content_id"));
    }
}
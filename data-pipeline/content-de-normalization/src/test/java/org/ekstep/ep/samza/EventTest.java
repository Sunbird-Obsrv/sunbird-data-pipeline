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
    public void testGetContentIdOfGeLaunchEvent() throws Exception {
        Event e = new Event(EventFixture.GeLaunchEvent(), contentTaxonomy);

        assertEquals("do_30076072", e.getContentId());
    }

    @Test
    public void testGetContentIdOfOeEvent() throws Exception {
        Event e = new Event(EventFixture.OeEvent(), contentTaxonomy);

        assertEquals("do_30076072", e.getContentId());
    }

    @Test
    public void testGetContentIdOfMeEvent() throws Exception {
        Event e = new Event(EventFixture.MeEvent(), contentTaxonomy);

        assertEquals("do_30076072", e.getContentId());
    }

    @Test
    public void testGetContentIdOfBeEvent() throws Exception {
        Event e = new Event(EventFixture.BeEvent(), contentTaxonomy);

        assertEquals("do_30076072", e.getContentId());
    }

    @Test
    public void testGetContentIdOfCpEvent() throws Exception {
        Event e = new Event(EventFixture.CpEvent(), contentTaxonomy);

        assertEquals("do_30076072", e.getContentId());
    }

    @Test
    public void testGetContentIdOfCeEvent() throws Exception {
        Event e = new Event(EventFixture.CeEvent(), contentTaxonomy);

        assertEquals("do_30076072", e.getContentId());
    }

    @Test
    public void testGetContentIdReturnsNullWhenItIsAbsent() throws Exception {
        Event e = new Event(EventFixture.EventWithoutContentId(), contentTaxonomy);

        assertEquals(null, e.getContentId());
    }

    private void createContentTaxonomy() {
        contentTaxonomy.put("OE", asList("gdata", "id"));
        contentTaxonomy.put("GE", asList("edata", "eks", "gid"));
        contentTaxonomy.put("ME", asList("dimensions", "content_id"));
        contentTaxonomy.put("CE", asList("context", "content_id"));
        contentTaxonomy.put("CP", asList("edata", "eks", "action"));
        contentTaxonomy.put("BE", asList("edata", "eks", "cid"));
    }
}
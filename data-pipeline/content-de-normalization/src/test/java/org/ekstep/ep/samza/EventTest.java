package org.ekstep.ep.samza;

import org.junit.Test;

import java.util.HashMap;
import org.ekstep.ep.samza.fixture.EventFixture;

import static org.junit.Assert.*;

public class EventTest {

    @Test
    public void testGetContentIdOfGeLaunchEvent() throws Exception {
        Event e = new Event(EventFixture.GeLaunchEvent());
        assertEquals("do_30074541", e.getContentId());
    }

    @Test
    public void testGetContentIdOfOeEvent() throws Exception {
        Event e = new Event(EventFixture.OeEvent());
        assertEquals("do_30076072", e.getContentId());
    }

    @Test
    public void testGetContentIdWhenItIsAbsent() throws Exception {
        Event e = new Event(EventFixture.EventWithoutContentId());
        assertEquals(null, e.getContentId());
    }
}
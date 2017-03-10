package org.ekstep.ep.samza;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixture.EventFixture;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventTest {

    HashMap<String, Object> contentEventMap = new HashMap<String,Object>();

    @Before
    public void setUp() {
        createContentEventMap();
    }

    @Test
    public void testGetContentIdOfGeLaunchEvent() throws Exception {
        Event e = new Event(EventFixture.GeLaunchEvent(),contentEventMap);

        assertEquals("do_30076072", e.getContentId());
    }

    @Test
    public void testGetContentIdOfOeEvent() throws Exception {
        Event e = new Event(EventFixture.OeEvent(),contentEventMap);

        assertEquals("do_30076072", e.getContentId());
    }

    @Test
    public void testGetContentIdOfMeEvent() throws Exception {
        Event e = new Event(EventFixture.MeEvent(),contentEventMap);

        assertEquals("do_30076072", e.getContentId());
    }

    @Test
    public void testGetContentIdReturnsNullWhenItIsAbsent() throws Exception {
        Event e = new Event(EventFixture.EventWithoutContentId(),contentEventMap);

        assertEquals(null, e.getContentId());
    }

    private  HashMap<String,Object> getContentFieldMap(HashMap<String,Object> eventTypes, HashMap<String,Object> fields) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        List<String> events = commaSeparatedStringToList(eventTypes, "gid.overridden.events");
        for (String event : events) {
            String eventType = event.split("\\.")[0];
            List<String> gidPath = getGidPath(fields, event);
            map.put(eventType.toUpperCase(), gidPath);
        }
        return map;
    }

    private List<String> commaSeparatedStringToList(HashMap<String,Object> config, String key) {
        String[] split = ((String) config.get(key)).split(",");
        List<String> list = new ArrayList<String>();
        for (String event : split) {
            list.add(event.trim());
        }
        return list;
    }

    private List<String> getGidPath(HashMap<String,Object> config, String key) {
        String[] split = ((String) config.get(key)).split("\\.");
        List<String> list = new ArrayList<String>();
        for (String e : split) {
            list.add(e.trim());
        }
        return list;
    }

    private void createContentEventMap() {
        HashMap<String, Object> overRiddenEvents = new HashMap<String, Object>();
        HashMap<String, Object> eventGidFields = new HashMap<String, Object>();
        overRiddenEvents.put("gid.overridden.events","me.gid.field,ge.gid.field,oe.gid.field,ce.gid.field");
        eventGidFields.put("oe.gid.field","gdata.id");
        eventGidFields.put("ge.gid.field","edata.eks.gid");
        eventGidFields.put("me.gid.field","dimensions.content_id");
        eventGidFields.put("ce.gid.field","context.content_id");

        contentEventMap = getContentFieldMap(overRiddenEvents, eventGidFields);
    }
}
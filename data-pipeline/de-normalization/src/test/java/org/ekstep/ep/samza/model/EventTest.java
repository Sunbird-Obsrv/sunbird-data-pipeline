package org.ekstep.ep.samza.model;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static junit.framework.Assert.*;

public class EventTest {
    @Test
    public void ShouldGiveANullChildIfEventDoesNotHaveTime() {
        Event event = new Event(new HashMap<String, Object>());

        assertNull(event.getChild());
    }

    @Test
    public void ShouldGiveANullChildIfEventDoesNotHaveValidTime() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ts", "invalid-time");

        Event event = new Event(map);

        assertNull(event.getChild());
    }
    
    @Test
    public void ShouldGetTheChildWithProperData() {
        HashMap<String, Object> map = new HashMap<String, Object>();

        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        flags.put("child_data_processed", true);

        map.put("udata", getUdata());
        map.put("flags", flags);
        map.put("ts", "2008-06-16T00:00:00 +0530");

        Event event = new Event(map);
        Child child = event.getChild();
        HashMap<String, Object> childData = child.getData();
        
        assertEquals(112233445L,childData.get("age"));
        assertEquals("2008-06-16 00:00:00 +0530", childData.get("dob"));
        assertEquals(7, childData.get("age_completed_years"));
        assertEquals("male", childData.get("gender"));
        assertEquals("batman", childData.get("uname"));
        assertEquals("dark_knight", childData.get("uekstep_id"));
        assertTrue(child.isProcessed());
    }

    @Test
    public void ShouldUpdateItselfFromChild() {
        Child child = new Child("1234567abcd", true, 123456789, getUdata());

        Event event = new Event(new HashMap<String, Object>());

        event.update(child);

        Map<String, Object> data = event.getData();
        Map<String, Object> expectedUdata = (Map<String, Object>) data.get("udata");
        Map<String, Boolean> flags = (Map<String, Boolean>) data.get("flags");

        assertTrue(flags.get("child_data_processed"));
        assertEquals(112233445L, expectedUdata.get("age"));
        assertEquals(getUdata().get("dob"), expectedUdata.get("dob"));
        assertEquals(getUdata().get("age_completed_years"), expectedUdata.get("age_completed_years"));
        assertEquals(getUdata().get("uname"), expectedUdata.get("uname"));
        assertEquals(getUdata().get("uekstep_id"), expectedUdata.get("uekstep_id"));
    }

    @Test
    public void ShouldRetainTheFlagsInTheEvent() {

        HashMap<String, Object> map = new HashMap<String, Object>();
        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        flags.put("old_flag", true);
        flags.put("old_false_flag", false);
        map.put("flags", flags);

        Event event = new Event(map);

        Child child = new Child("1234321", true, 123456789, getUdata());

        event.update(child);
        Map<String, Boolean> expectedFlags = (Map<String, Boolean>) event.getData().get("flags");

        assertTrue(expectedFlags.get("old_flag"));
        assertFalse(expectedFlags.get("old_false_flag"));
        assertTrue(expectedFlags.get("child_data_processed"));
    }

    private HashMap<String, Object> getUdata() {
        HashMap<String, Object> udata = new HashMap<String, Object>();
        udata.put("age", 112233445);
        udata.put("dob", "2008-06-16 00:00:00 +0530");
        udata.put("age_completed_years", 7);
        udata.put("gender", "male");
        udata.put("uname", "batman");
        udata.put("uekstep_id", "dark_knight");
        return udata;
    }

}
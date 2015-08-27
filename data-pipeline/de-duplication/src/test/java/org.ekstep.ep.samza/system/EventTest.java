package org.ekstep.ep.samza.system;


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class EventTest {
    @Test
    public void shouldAddLocation(){
        Map<String,String> checksum = new HashMap<String, String>();
        checksum.put("checksum","1234");

        Map<String,Object> metadata = new HashMap<String, Object>();
        metadata.put("metadata",checksum);

        Event event = new Event(metadata);

        Assert.assertEquals("1234", (String) event.getChecksum());
    }
}


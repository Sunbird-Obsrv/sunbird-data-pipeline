package org.ekstep.ep.samza.system;


import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class EventTest {
    @Test
    public void shouldGetChecksumFromMetadataChecksumIfPresent(){

        Event event = new Event(EventFixture.EventWithChecksum());
        Assert.assertEquals("22e1430f2e5f339230dbf9595b060008", (String) event.getChecksum());
    }

    @Test
    public void shouldGetChecksumFromMidIfPresent(){

        Event event = new Event(EventFixture.EventWithMid());
        Assert.assertEquals("22e1430f2e5f339230dbf9595b060008", (String) event.getChecksum());
    }

    @Test
    public void shouldReturnNullIfChecksumFieldIsAbsent(){

        Event event = new Event(EventFixture.EventWithoutChecksumField());
        Assert.assertEquals(null, (String) event.getChecksum());
    }
}


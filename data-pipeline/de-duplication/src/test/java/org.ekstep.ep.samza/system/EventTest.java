package org.ekstep.ep.samza.system;


import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Test;

public class EventTest {
    @Test
    public void shouldReturnChecksumIfPresent(){

        Event event = new Event(EventFixture.EventWithChecksumMap());
        Assert.assertEquals("22e1430f2e5f339230dbf9595b060008", (String) event.getChecksum());
    }

    @Test
    public void shouldReturnMidIfPresent(){

        Event event = new Event(EventFixture.EventWithMidMap());
        Assert.assertEquals("22e1430f2e5f339230dbf9595b060008", (String) event.getChecksum());
    }

    @Test
    public void shouldReturnNullIfChecksumAndMidAreAbsent(){

        Event event = new Event(EventFixture.EventWithoutChecksumFieldMap());
        Assert.assertEquals(null, (String) event.getChecksum());
    }
}


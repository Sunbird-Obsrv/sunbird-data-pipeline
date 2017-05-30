package org.ekstep.ep.samza.task.domain;


import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Test;

public class EventTest {
    @Test
    public void shouldReturnChecksumIfPresent(){

        Event event = new Event(EventFixture.EventWithChecksum());
        Assert.assertEquals("22e1430f2e5f339230dbf9595b060008", (String) event.getChecksum());
    }

    @Test
    public void shouldReturnMidIfPresent(){

        Event event = new Event(EventFixture.EventWithMid());
        Assert.assertEquals("22e1430f2e5f339230dbf9595b060008", (String) event.getChecksum());
    }

    @Test
    public void shouldReturnNullIfChecksumAndMidAreAbsent(){

        Event event = new Event(EventFixture.EventWithoutChecksum());
        Assert.assertEquals(null, (String) event.getChecksum());
    }
}


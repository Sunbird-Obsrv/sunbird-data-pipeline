package org.ekstep.ep.samza.system;


import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.task.DeDuplicationConfig;
import org.junit.Assert;
import org.junit.Test;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void shouldAddChannelIfChannelIsAbsentNullOrEmpty(){
        DeDuplicationConfig conf = mock(DeDuplicationConfig.class);
        when(conf.defaultChannel()).thenReturn("in.ekstep");

        Event event = new Event(EventFixture.EventWithMidMap());
        event.updateDefaults(conf);
        Assert.assertEquals("in.ekstep", ( (Map<String,Object>) event.getMap().get("context")).get("channel") );


        Event event2 = new Event(EventFixture.EventWithEmptyChannel());
        event2.updateDefaults(conf);
        Assert.assertEquals("in.ekstep", ( (Map<String,Object>) event.getMap().get("context")).get("channel"));
    }
}


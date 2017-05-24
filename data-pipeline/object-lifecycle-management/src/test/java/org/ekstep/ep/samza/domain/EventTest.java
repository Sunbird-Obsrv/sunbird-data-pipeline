package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.fixture.EventFixture;
import org.ekstep.ep.samza.fixture.ObjectFixture;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class EventTest {

    @Test
    public void testIfEventCanBeProcessedReturnsTrueForLifeCycleEvent() throws Exception {
        Event event = new Event(EventFixture.LifecycleEvent(), Arrays.asList("BE_OBJECT_LIFECYCLE"));
        Assert.assertEquals(event.canBeProcessed(),true);
    }

    @Test
    public void testIfEventCanBeProcessedReturnsFalseForOtherEvents() throws Exception {
        Event event = new Event(EventFixture.OtherEvent(), Arrays.asList("BE_OBJECT_LIFECYCLE"));
        Assert.assertEquals(event.canBeProcessed(),false);
    }
}

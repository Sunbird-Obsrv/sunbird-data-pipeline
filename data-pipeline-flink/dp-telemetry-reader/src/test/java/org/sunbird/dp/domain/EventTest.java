package org.sunbird.dp.domain;

import org.sunbird.dp.reader.NullableValue;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class EventTest {

    @Test
    public void shouldReturnMid() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("IMPRESSION:0093c96434557b2ead169c7156e95770", event.mid());
    }

    @Test
    public void shouldReturnDid() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("f1a99b5cf111be23bd0e8d48a50458c6", event.did());
    }

    @Test
    public void shouldReturnEid() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("IMPRESSION", event.eid());
    }

    @Test
    public void shouldReturnPid() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("sunbird-portal", event.pid());
    }

    @Test
    public void shouldReturnVersion() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("3.0", event.version());
    }

    @Test
    public void shouldReturunContextPID() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("sunbird-portal", event.producerPid());
    }

    @Test
    public void shouldReturunEts() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Long ets = 1574945199426L;
        Assert.assertEquals(ets, event.ets());
    }

    @Test
    public void shouldReturnChannel() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("505c7c48ac6dc1edc9b08f21db5a571d", event.channel());
    }

    @Test
    public void shouldReturnActorId() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("f1a99b5cf111be23bd0e8d48a50458c6", event.actorId());
    }

    @Test
    public void shouldReturnActorType() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("User", event.actorType());
    }

    @Test
    public void shouldReturnObjectId() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("658374_49785", event.objectID());
    }

    @Test
    public void shoudlReturnNullWhenObjectIdIsNotPresent() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT_MISSING_FIELDS));
        Assert.assertNull(event.objectID());
    }

    @Test
    public void shouldReturnProducerPID() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("prod.diksha.portal", event.producerId());
    }

    @Test
    public void shouldReturnObjectType() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("content", event.objectType());
    }

    @Test
    public void shouldReturnNullWhenObjectTypeIsNotPresent() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT_MISSING_FIELDS));
        Assert.assertNull(event.objectType());
    }

    @Test
    public void shouldReturnEdataType() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals("view", event.edataType());
    }

    @Test
    public void shouldGetEventJsonObject() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertNotNull(event.getJson());
    }

    @Test
    public void shouldGetTheMetaDataId() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertNotNull(event.id());
    }

    @Test
    public void shouldGetTheCheckSumValue() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertNotNull(event.getChecksum());
    }

    @Test
    public void shouldGetTheStringObject() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertNotNull(event.toString());
    }

    @Test
    public void shouldUpdateTheTSValue() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        event.updateTs("5468376530");
        Assert.assertEquals(event.getData().value(), "5468376530");
    }

    @Test
    public void shouldGetMidValueIfTheCheckSumIsNotPresent() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT_MISSING_FIELDS));

        Assert.assertEquals(event.getChecksum(), "IMPRESSION:0093c96434557b2ead169c7156e95770");
    }

    @Test
    public void shouldUpdateFlags() {
        Event event = new Event(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        event.updateFlags("test_flag", true);
        Assert.assertEquals(event.getFlags().size(), 1);
        Assert.assertEquals(event.getFlags().get("test_flag"), true);
    }


}


/**
 * Creating a dummy event class to to test the base abstract class object
 */

class Event extends Events {

    public Event(Map<String, Object> map) {
        super(map);
    }

    public <T> NullableValue<T> getData() {
        return telemetry.read("@timestamp");
    }

}



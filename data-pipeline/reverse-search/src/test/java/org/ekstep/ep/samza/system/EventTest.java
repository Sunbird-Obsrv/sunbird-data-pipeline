package org.ekstep.ep.samza.system;


import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class EventTest {
    @Test
    public void shouldReturnEmptyLocationIfNotPresent() {
        Map<String,Object> map = new HashMap<String, Object>();
        Event event = new Event(map);
        Assert.assertEquals("",event.getGPSCoordinates());
    }

    @Test
    public void shouldReturnLocation(){
        Map<String,Object> map = getMap("12.44,11.56");
        Event event = new Event(map);
        Assert.assertEquals("12.44,11.56", event.getGPSCoordinates());
    }

    @Test
    public void shouldGetDeviceIDFromDimensionsFirst(){
        HashMap<String, Object> map = new HashMap<String,Object>();
        map.put("did","device_id1");
        HashMap<String, Object> dimensions = new HashMap<String,Object>();
        dimensions.put("did","device_id2");
        map.put("dimensions", dimensions);

        Event event = new Event(map);
        Assert.assertEquals("device_id2", event.getDid());
    }

    @Test
    public void shouldGetEmptyDeviceIDFromDimensionsContainsEmptyDeviceId(){
        HashMap<String, Object> map = new HashMap<String,Object>();
        map.put("did","device_id1");
        HashMap<String, Object> dimensions = new HashMap<String,Object>();
        dimensions.put("did","");
        map.put("dimensions", dimensions);

        Event event = new Event(map);
        Assert.assertEquals("", event.getDid());
    }

    @Test
    public void shouldGetDeviceIDFromRootIfDimensionsDoesNotContainDid(){
        HashMap<String, Object> map = new HashMap<String,Object>();
        map.put("did","device_id1");
        HashMap<String, Object> dimensions = new HashMap<String,Object>();
        map.put("dimensions", dimensions);

        Event event = new Event(map);
        Assert.assertEquals("device_id1", event.getDid());
    }

    @Test
    public void shouldGetDeviceIDFromRootWhenDimensionIsNotPresent(){
        HashMap<String, Object> map = new HashMap<String,Object>();
        map.put("did", "device_id1");

        Event event = new Event(map);
        Assert.assertEquals("device_id1", event.getDid());
    }

    @Test
    public void shouldGetNullDidWhenAbsent(){
        HashMap<String, Object> map = new HashMap<String,Object>();

        Event event = new Event(map);
        Assert.assertNull(event.getDid());
    }

    @Test
    public void shouldAddLocation(){
        Map<String,Object> map = new HashMap<String, Object>();
        Event event = new Event(map);
        Location location = getLocation();
        event.AddLocation(location);
        Map<String,Object>ldata = (Map<String, Object>) map.get("ldata");
        Assert.assertEquals("City1",(String)ldata.get("locality"));
    }

    @Test
    public void shouldSetFlagIfNotPresent(){
        Map<String,Object> map = new HashMap<String, Object>();
        Event event = new Event(map);
        event.setFlag("ldata_obtained", true);

        Map<String,Object> flags = (Map<String, Object>) map.get("flags");
        Assert.assertNotNull(flags);

        Assert.assertEquals(true, flags.get("ldata_obtained"));
    }



    @Test
    public void shouldReuseFlagIfPresent(){
        Map<String,Object> map = new HashMap<String, Object>();
        Map<String, Object> flags = new HashMap<String, Object>();
        flags.put("ldata_obtained",false);
        map.put("flags",flags);

        Event event = new Event(map);
        event.setFlag("ldata_obtained", true);

        Map<String,Object> flagsResult = (Map<String, Object>) map.get("flags");
        Assert.assertNotNull(flagsResult);

        Assert.assertEquals(true, flagsResult.get("ldata_obtained"));
    }

    @Test
    public void shouldReuseFlagIfPresentForNewFlagEntry(){
        Map<String,Object> map = new HashMap<String, Object>();
        Map<String, Object> flags = new HashMap<String, Object>();
        flags.put("ldata_obtained",true);
        map.put("flags",flags);

        Event event = new Event(map);
        event.setFlag("ldata_processed", true);

        Map<String,Object> flagsResult = (Map<String, Object>) map.get("flags");
        Assert.assertNotNull(flagsResult);

        Assert.assertEquals(true, flagsResult.get("ldata_obtained"));
        Assert.assertEquals(true, flagsResult.get("ldata_processed"));
    }

    @Test
    public void shouldReturnSameMap(){
        Map<String,Object> map = new HashMap<String, Object>();
        Event event = new Event(map);
        Assert.assertEquals(map,event.getMap());
    }

    @Test
    public void shouldCreateTimestamp(){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("ets",1453202865000L);

        Event event = new Event(map);
        event.setTimestamp();

        Assert.assertEquals(true,event.getMap().containsKey("ts"));
    }

    @Test
    public void shouldNotCreateTimestampIfAlreadyPresent(){
        String existingTs = "2017-04-28'T'10:45:33.001Z";
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("ets",1453202865000L);
        map.put("ts", existingTs);
        Event event = new Event(map);
        event.setTimestamp();

        Assert.assertEquals(true,event.getMap().containsKey("ts"));
        Assert.assertEquals(existingTs,event.getMap().get("ts"));
    }

    @Test
    public void shouldNotCreateTimestampIfEtsAbsent(){
        Map<String,Object> map = new HashMap<String, Object>();
        Event event = new Event(map);
        event.setTimestamp();

        Assert.assertEquals(false,event.getMap().containsKey("ts"));
    }

    @Test
    public void shouldReturnTrueIfLocationIsPresent(){
        Event event = new Event(EventFixture.locationPresent());
        Assert.assertEquals(true, event.isLocationPresent());
        Assert.assertEquals(false, event.isLocationAbsent());
        Assert.assertEquals(false, event.isLocationEmpty());
    }

    @Test
    public void shouldReturnTrueIfLocationIsAbsent(){
        Event event = new Event(EventFixture.locationAbsent());
        Assert.assertEquals(true, event.isLocationAbsent());
        Assert.assertEquals(false, event.isLocationPresent());
        Assert.assertEquals(false, event.isLocationEmpty());
    }

    @Test
    public void shouldReturnTrueIfLocationIsEmpty(){
        Event event = new Event(EventFixture.locationEmpty());
        Assert.assertEquals(true, event.isLocationEmpty());
        Assert.assertEquals(false, event.isLocationPresent());
        Assert.assertEquals(false, event.isLocationAbsent());
    }

    private Location getLocation() {
        Location location = new Location();
        location.setCity("City1");
        location.setCountry("India");
        location.setDistrict("Dist1");
        location.setState("State1");
        return location;
    }

    private Map<String, Object> getMap(String location) {
        HashMap<String, Object> event = new HashMap<String, Object>();
        HashMap<String, Object> edata = new HashMap<String, Object>();
        HashMap<String, String> loc = new HashMap<String, String>();
        event.put("edata", edata);
        edata.put("eks", loc);
        loc.put("loc",location);
        return event;
    }


}

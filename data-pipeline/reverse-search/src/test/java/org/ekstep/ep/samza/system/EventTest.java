package org.ekstep.ep.samza.system;


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void shouldGetDeviceID(){
        Map<String, Object> map = mock(Map.class);
        when(map.get("did")).thenReturn("device_id");

        Event event = new Event(map);
        Assert.assertEquals("device_id", event.getDid());
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

    private Location getLocation() {
        Location location = new Location();
        location.setCity("City1");
        location.setCountry("India");
        location.setDistrict("Dist1");
        location.setState("State1");
        return location;
    }

    private Map<String, Object> getMap(Object loc) {
        Map<String, Object> eks = mock(Map.class);
        when(eks.get("loc")).thenReturn(loc);
        Map<String, Object> edata = mock(Map.class);
        when(edata.get("eks")).thenReturn(eks);
        Map<String, Object> event = mock(Map.class);
        when(event.get("edata")).thenReturn(edata);
        return event;
    }


}

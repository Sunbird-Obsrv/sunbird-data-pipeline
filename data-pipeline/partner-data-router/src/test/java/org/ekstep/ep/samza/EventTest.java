package org.ekstep.ep.samza;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventTest {
    @Test
    public void shouldBelongToPartnerIfPartnerIdIsPresent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> tags = new ArrayList<HashMap>();
        data.put("tags", tags);
        HashMap<String, String> firstTag = new HashMap<String,String>();
        tags.add(firstTag);
        firstTag.put("partnerid","org.test.partner.id");
        Event event = new Event(data);

        Assert.assertTrue(event.belongsToAPartner());
    }

    @Test
    public void shouldNotBelongToPartnerIfPartnerIdIsAbsent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> tags = new ArrayList<HashMap>();

        data.put("tags", tags);
        HashMap<String, String> firstTag = new HashMap<String,String>();
        tags.add(firstTag);
        Event event = new Event(data);

        Assert.assertFalse(event.belongsToAPartner());
    }

    @Test
    public void shouldNotBelongToPartnerIfTagsIsAbsent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        Event event = new Event(data);

        Assert.assertFalse(event.belongsToAPartner());
    }

    @Test
    public void shouldRouteToTheEventsTopicBasedOnPartnerId() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> tags = new ArrayList<HashMap>();

        data.put("tags", tags);
        HashMap<String, String> firstTag = new HashMap<String,String>();
        tags.add(firstTag);
        firstTag.put("partnerid","org.test.partner.id");
        Event event = new Event(data);

        Assert.assertEquals("org.test.partner.id.events", event.routeTo());
    }

    @Test
    public void shouldGetDataFromEvent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        HashMap<String, Object> edata = new HashMap<String,Object>();
        HashMap<String, Object> eks = new HashMap<String,Object>();
        data.put("edata", edata);
        edata.put("eks", eks);
        eks.put("partnerid","org.test.partner.id");
        Event event = new Event(data);

        Assert.assertEquals(data,event.getData());
    }

    @Test
    public void shouldUpdateTheTypeOfData(){
        HashMap<String, Object> data = new HashMap<String,Object>();
        data.put("type","otherType");
        Event event = new Event(data);

        event.updateType();

        Assert.assertEquals("partner.events",event.getData().get("type"));
    }

    @Test
    public void shouldHandleWhenThereIsNoType(){
        HashMap<String, Object> data = new HashMap<String,Object>();
        Event event = new Event(data);

        event.updateType();

        Assert.assertEquals("partner.events",event.getData().get("type"));
    }

}
package org.ekstep.ep.samza;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class EventTest {

    @Test
    public void shouldBelongToPartnerIfPartnerIdIsPresent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        HashMap<String, Object> context = new HashMap<String,Object>();

        ArrayList<HashMap> cdata = new ArrayList<HashMap>();
        context.put("cdata", cdata);

        HashMap<String, String> firstMap = new HashMap<String,String>();
        cdata.add(firstMap);
        firstMap.put("type", "partner");
        firstMap.put("id", "org.ekstep.partner.akshara");
        data.put("context",context);

        Event event = new Event(data);

        Assert.assertTrue(event.belongsToAPartner());
    }

    @Test
    public void shouldBelongToPartnerIfPartnerIdIsNumberString() {
        HashMap<String, Object> context = new HashMap<String,Object>();
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> cdata = new ArrayList<HashMap>();
        data.put("cdata", cdata);
        HashMap<String, String> firstMap = new HashMap<String,String>();
        cdata.add(firstMap);
        firstMap.put("type", "partner");
        firstMap.put("id", "9e94fb35");
        context.put("context",data);
        Event event = new Event(context);

        Assert.assertTrue(event.belongsToAPartner());
    }


    @Test
    public void shouldBelongToPartnerIfPartnerIdIsPresentWithMultipleMaps() {
        HashMap<String, Object> context = new HashMap<String,Object>();
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> cdata = new ArrayList<HashMap>();
        context.put("cdata", cdata);

        HashMap<String, String> firstMap = new HashMap<String,String>();
        cdata.add(firstMap);
        firstMap.put("type", "partner");
        firstMap.put("id", "9e94fb35");

        HashMap<String, String> secondMap = new HashMap<String,String>();
        cdata.add(secondMap);
        firstMap.put("type", "partner");
        firstMap.put("id", "6e94fb35");

        data.put("context",context);
        Event event = new Event(data);

        Assert.assertTrue(event.belongsToAPartner());
    }

    @Test
    public void shouldNotBelongToPartnerIfPartnerIdIsAbsent() {
        HashMap<String, Object> context = new HashMap<String,Object>();
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> cdata = new ArrayList<HashMap>();
        context.put("cdata", cdata);

        HashMap<String, String> firstMap = new HashMap<String,String>();
        cdata.add(firstMap);
        data.put("context",context);

        Event event = new Event(data);

        Assert.assertFalse(event.belongsToAPartner());
    }

    @Test
    public void shouldNotBelongToPartnerIfCDataIsEmpty() {
        HashMap<String, Object> context = new HashMap<String,Object>();
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> cdata = new ArrayList<HashMap>();
        context.put("cdata", cdata);
        data.put("context",context);

        Event event = new Event(data);

        Assert.assertFalse(event.belongsToAPartner());
    }

    @Test
    public void shouldNotBelongToPartnerIfContextIsAbsent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        Event event = new Event(data);

        Assert.assertFalse(event.belongsToAPartner());
    }

    @Test
    public void shouldGetDataFromEvent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        HashMap<String, Object> edata = new HashMap<String,Object>();
        HashMap<String, Object> eks = new HashMap<String,Object>();
        data.put("edata", edata);
        edata.put("eks", eks);
        eks.put("partnerid","org.ekstep.partner.akshara");
        Event event = new Event(data);

        Assert.assertEquals(data, event.getMap());
    }

    @Test
    public void shouldUpdateTheTypeOfData(){
        HashMap<String, Object> data = new HashMap<String,Object>();
        data.put("type","otherType");
        Event event = new Event(data);

        event.updateType();

        Assert.assertEquals("partner.events",event.getMap().get("type"));
    }

    @Test
    public void shouldUpdateMetadataWithShaOfPartnerId() throws Exception {
        HashMap<String, Object> data = new HashMap<String,Object>();
        HashMap<String, Object> context = new HashMap<String,Object>();

        ArrayList<HashMap> cdata = new ArrayList<HashMap>();
        context.put("cdata", cdata);

        HashMap<String, String> firstMap = new HashMap<String,String>();
        cdata.add(firstMap);
        firstMap.put("type", "partner");
        firstMap.put("id", "org.ekstep.partner.pratham");
        data.put("context",context);

        Event event = new Event(data);

        event.updateMetadata();

        Assert.assertEquals("fd001eaba2e5b79b814446307d709ee2097fbd51",(String) ( (Map<String,Object>) event.getMap().get("metadata")).get("partner_name"));
    }

    @Test
    public void shouldHandleWhenThereIsNoType(){
        HashMap<String, Object> data = new HashMap<String,Object>();
        Event event = new Event(data);

        event.updateType();

        Assert.assertEquals("partner.events",event.getMap().get("type"));
    }

}
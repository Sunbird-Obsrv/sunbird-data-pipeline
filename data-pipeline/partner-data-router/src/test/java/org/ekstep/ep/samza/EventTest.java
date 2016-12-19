package org.ekstep.ep.samza;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class EventTest {
    private static final List<String> validPartners =
            Arrays.asList("org.ekstep.partner.akshara", "org.ekstep.partner.pratham", "org.ekstep.partner.enlearn");

    @Test
    public void shouldBelongToPartnerIfPartnerIdIsPresent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> tags = new ArrayList<HashMap>();
        data.put("tags", tags);
        HashMap<String, String> firstTag = new HashMap<String,String>();
        tags.add(firstTag);
        firstTag.put("partnerid", "org.ekstep.partner.akshara");
        Event event = new Event(data, validPartners);

        Assert.assertTrue(event.belongsToAPartner());
    }

    @Test
    public void shouldBelongToPartnerIfPartnerIdIsNumberString() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> tags = new ArrayList<HashMap>();
        data.put("tags", tags);
        HashMap<String, String> firstTag = new HashMap<String,String>();
        tags.add(firstTag);
        firstTag.put("partnerid", "9e94fb35");
        Event event = new Event(data);

        Assert.assertTrue(event.belongsToAPartner());
    }

    @Test
    public void shouldHandleIfPartnerTagContainsListOfPartnerIds() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> tags = new ArrayList<HashMap>();
        data.put("tags", tags);
        HashMap<String, Object> firstTag = new HashMap<String,Object>();
        ArrayList<String> partnerSet = new ArrayList<String>(Arrays.asList("org.ekstep.partner.akshara"));
        firstTag.put("partnerid", partnerSet);
        tags.add(firstTag);
        Event event = new Event(data, validPartners);

        Assert.assertTrue(event.belongsToAPartner());
    }

    @Test
    public void shouldBelongToPartnerIfPartnerIdIsPresentWithMultipleTags() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> tags = new ArrayList<HashMap>();
        data.put("tags", tags);
        HashMap<String, String> firstTag = new HashMap<String,String>();
        HashMap<String, String> secondTag = new HashMap<String,String>();
        HashMap<String, String> thirdTag = new HashMap<String,String>();
        tags.add(firstTag);
        tags.add(secondTag);
        tags.add(thirdTag);
        firstTag.put("someKey","value");
        secondTag.put("partnerid","org.ekstep.partner.akshara");
        Event event = new Event(data, validPartners);

        Assert.assertTrue(event.belongsToAPartner());
    }

    @Test
    public void shouldNotBelongToPartnerIfPartnerIdIsAbsent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> tags = new ArrayList<HashMap>();

        data.put("tags", tags);
        HashMap<String, String> firstTag = new HashMap<String,String>();
        tags.add(firstTag);
        Event event = new Event(data, validPartners);

        Assert.assertFalse(event.belongsToAPartner());
    }

    @Test
    public void shouldNotBelongToPartnerIfTagsAreEmpty() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> tags = new ArrayList<HashMap>();

        data.put("tags", tags);
        Event event = new Event(data, validPartners);

        Assert.assertFalse(event.belongsToAPartner());
    }

    @Test
    public void shouldNotBelongToPartnerIfTagsIsAbsent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        Event event = new Event(data, validPartners);

        Assert.assertFalse(event.belongsToAPartner());
    }

    @Test
    public void shouldRouteToTheEventsTopicBasedOnPartnerId() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        ArrayList<HashMap> tags = new ArrayList<HashMap>();

        data.put("tags", tags);
        HashMap<String, String> firstTag = new HashMap<String,String>();
        tags.add(firstTag);
        firstTag.put("partnerid","org.ekstep.partner.akshara");
        Event event = new Event(data, validPartners);

        Assert.assertEquals("org.ekstep.partner.akshara.events", event.routeTo());
    }

    @Test
    public void shouldGetDataFromEvent() {
        HashMap<String, Object> data = new HashMap<String,Object>();
        HashMap<String, Object> edata = new HashMap<String,Object>();
        HashMap<String, Object> eks = new HashMap<String,Object>();
        data.put("edata", edata);
        edata.put("eks", eks);
        eks.put("partnerid","org.ekstep.partner.akshara");
        Event event = new Event(data, validPartners);

        Assert.assertEquals(data,event.getData());
    }

    @Test
    public void shouldUpdateTheTypeOfData(){
        HashMap<String, Object> data = new HashMap<String,Object>();
        data.put("type","otherType");
        Event event = new Event(data, validPartners);

        event.updateType();

        Assert.assertEquals("partner.events",event.getData().get("type"));
    }

    @Test
    public void shouldHandleWhenThereIsNoType(){
        HashMap<String, Object> data = new HashMap<String,Object>();
        Event event = new Event(data, validPartners);

        event.updateType();

        Assert.assertEquals("partner.events",event.getData().get("type"));
    }

}
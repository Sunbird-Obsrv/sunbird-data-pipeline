package org.ekstep.ep.samza.system;


import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class EventTest {

    @Test
    public void shouldReturnCidFromEvent(){

        Map<String, Object> json = (Map<String, Object>) createEvent();
        Map<String, Object> taxonomyMap = (Map<String, Object>) createTaxonomyMap();

        Event event = new Event(json);

        Assert.assertEquals("LT1", (String) event.getCid());
    }

    @Test
    public void shouldAddTaxonomyDataToEventAndReturnNewEventWithTaxonomyData(){

        Map<String, Object> json = (Map<String, Object>) createEvent();
        Map<String, Object> taxonomyMap = (Map<String, Object>) createTaxonomyMap();

        Event event = new Event(json);

        Assert.assertEquals("LT1", (String) event.getCid());

        event.addTaxonomyData(taxonomyMap);
        Assert.assertEquals(taxonomyMap, (Map<String,Object>) event.getMap().get("taxonomy"));
    }

    @Test
    public void shouldCreateChecksumIfNotPresentAsPartOfTheEvent(){
        Map<String, Object> json = (Map<String, Object>) createEvent();

        Event event = new Event(json);
        event.addCheksum();
        Assert.assertEquals(true,(Boolean) event.getMap().containsKey("metadata"));
    }

    @Test
    public void ShouldNotCallChecksumGeneratorIfMetadataChecksumAlreadyPresent(){
        Map<String, Object> json = (Map<String, Object>) createEvent();

        Event event = new Event(json);
    }

    private Map<String, Object> createEvent(){
        Map<String, Object> event = new Gson().fromJson("{\n" +
                "     \"eid\": \"ME_USER_GAME_LEVEL\",\n" +
                "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
                "     \"ver\": \"1.0\",\n" +
                "     \"uid\": \"3ee97c77-3587-4394-aff6-32b12576fcf6\",\n" +
                "     \"gdata\": { \n" +
                "          \"id\":\"org.eks.lit_screener\",\n" +
                "          \"ver\":\"1.0\"\n" +
                "     },\n" +
                "     \"cid\": \"LT1\",\n" +
                "     \"ctype\": \"LT\",\n" +
                "     \"pdata\": {\n" +
                "          \"id\":\"ASSESMENT_PIPELINE\",\n" +
                "          \"mod\":\"LiteraryScreenerAssessment\",\n" +
                "          \"ver\":\"1.0\"\n" +
                "     },\n" +
                "     \"edata\": {\n" +
                "          \"eks\": {\n" +
                "              \"current_level\":\"Primary 5\",\n" +
                "              \"score\":\"15\"\n" +
                "          }\n" +
                "     }\n" +
                "}",Map.class);

        return event;
    }

    private Map<String, Object> createTaxonomyMap(){
        Map<String, Object> taxonomyMap = new Gson().fromJson("{\n" +
                "    \"LT11\": {\n" +
                "        \"id\": \"LT11\",\n" +
                "        \"name\": \"Read and choose Picture\",\n" +
                "        \"parent\": \"LO9\"\n" +
                "    },\n" +
                "    \"LO9\": {\n" +
                "        \"id\": \"LO9\",\n" +
                "        \"name\": \"Sentence Comprehension\",\n" +
                "        \"parent\": \"LD5\"\n" +
                "    },\n" +
                "    \"LD5\": {\n" +
                "        \"id\": \"LD5\",\n" +
                "        \"name\": \"Reading Comprehension\",\n" +
                "        \"parent\": null\n" +
                "    }\n" +
                "}",Map.class);

        return taxonomyMap;
    }


}



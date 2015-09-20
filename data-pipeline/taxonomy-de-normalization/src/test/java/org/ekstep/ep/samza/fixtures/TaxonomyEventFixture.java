package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by shashankteotia on 9/19/15.
 */
public class TaxonomyEventFixture {
    public static final String JSON = "{\n" +
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
            "}";
    public static final String LT = "LT1";
    public static final String LO = "LO9";
    public static final String LD = "LD5";
    public static final String LTJSON = "{\"id\":\"LT1\", \"name\":\"Read and choose Picture\", \"parent\":\"LO9\",\"type\":\"LT\"}";
    public static final String LOJSON = "{\"id\":\"LO9\", \"name\":\"Sentence Comprehension\", \"parent\":\"LD5\",\"type\":\"LO\"}";
    public static final String LDJSON = "{\"id\":\"LD5\", \"name\":\"Reading Comprehension\", \"parent\":\"null\",\"type\":\"LD\"}";
    public static final String taxonomyJSON = "{\"LT\":"+LTJSON+", \"LO\": "+LOJSON+", \"LD\":"+LDJSON+"}";

    public static String getJSON(){
        return JSON;
    }
    public static String getCID(){
        return String.valueOf(getMessage().get("cid"));
    }
    public static Map<String, Object> getMessage(){
        Map<String, Object> jsonObject = new Gson().fromJson(JSON,Map.class);
        return jsonObject;
    }
    public static Map<String, Object> getDeNormalizedMessage(){
        Map<String, Object> taxonomy = new Gson().fromJson(taxonomyJSON,Map.class);
        Map<String, Object> jsonObject = getMessage();
        jsonObject.put("taxonomy", taxonomy);
        return jsonObject;
    }


    //Map<String, Object> taxonomyData = new Gson().fromJson(,Map.class);
}

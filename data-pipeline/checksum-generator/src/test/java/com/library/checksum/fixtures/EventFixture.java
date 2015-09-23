package com.library.checksum.fixtures;

import java.util.Map;
import com.google.gson.Gson;

public class EventFixture {

    public static final Map<String, Object> event(){
        Gson gson = new Gson();
        Map<String,Object> jsonObject = gson.fromJson("{\n" +
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

        return jsonObject;
    }
}

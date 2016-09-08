package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
    public static final String JSON = "{\n" +
            "     \"eid\": \"ME_USER_GAME_LEVEL\",\n" +
            "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
            "     \"ver\": \"1.0\",\n" +
            "     \"uid\": \"3ee97c77-3587-4394-aff6-32b12576fcf6\",\n" +
            "     \"did\": \"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\",\n" +
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

    public static final String JSON_WITH_MID = "{\n" +
            "     \"eid\": \"ME_USER_GAME_LEVEL\",\n" +
            "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
            "     \"ver\": \"1.0\",\n" +
            "     \"uid\": \"3ee97c77-3587-4394-aff6-32b12576fcf6\",\n" +
            "     \"did\": \"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\",\n" +
            "     \"mid\": \"4aa9291822a0f3df7f91f36a4cca8445ebb458a4\",\n" +
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


    public static Map<String, Object> sessionStartEvent() {

        String json = "{\n" +
                "    \"tags\": [\n" +
                "        {\n" +
                "            \"genie\": []\n" +
                "        }\n" +
                "    ],\n" +
                "    \"uid\": \"0e86f56e-f69a-4704-b4c4-69057a0be7bc\",\n" +
                "    \"sid\": \"3950c578-f7fe-402a-b4f6-d9917d7ae9f5\",\n" +
                "    \"ts\": \"2016-06-29T10:36:51.688+0000\",\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"loc\": \"\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"did\": \"bc811958-b4b7-4873-a43a-03718edba45b\",\n" +
                "    \"ver\": \"2.0\",\n" +
                "    \"type\": \"events\",\n" +
                "    \"eid\": \"GE_SESSION_START\",\n" +
                "    \"@version\": \"1\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"genieservice.android\",\n" +
                "        \"ver\": \"4.2.435production\"\n" +
                "    },\n" +
                "    \"@timestamp\": \"2016-07-04T02:33:50.599Z\",\n" +
                "    \"ets\": 1467196611688,\n" +
                "    \"uuid\": \"cf598e7e-7b34-4ec0-9fef-6292967f7a0955\",\n" +
                "    \"mid\": \"3d8bae31-8c74-40be-99e2-409af7ab80e0\",\n" +
                "    \"key\": \"0e86f56e-f69a-4704-b4c4-69057a0be7bc\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());

    }

    public static Map<String, Object> otherEvent() {

        String json = "{\n" +
                "    \"tags\": [\n" +
                "        {\n" +
                "            \"genie\": []\n" +
                "        }\n" +
                "    ],\n" +
                "    \"uid\": \"0e86f56e-f69a-4704-b4c4-69057a0be7bc\",\n" +
                "    \"sid\": \"3950c578-f7fe-402a-b4f6-d9917d7ae9f5\",\n" +
                "    \"ts\": \"2016-06-29T10:36:51.688+0000\",\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"loc\": \"\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"did\": \"bc811958-b4b7-4873-a43a-03718edba45b\",\n" +
                "    \"ver\": \"2.0\",\n" +
                "    \"type\": \"events\",\n" +
                "    \"eid\": \"GE_INTERACT\",\n" +
                "    \"@version\": \"1\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"genieservice.android\",\n" +
                "        \"ver\": \"4.2.435production\"\n" +
                "    },\n" +
                "    \"@timestamp\": \"2016-07-04T02:33:50.599Z\",\n" +
                "    \"ets\": 1467196611688,\n" +
                "    \"uuid\": \"cf598e7e-7b34-4ec0-9fef-6292967f7a0955\",\n" +
                "    \"mid\": \"3d8bae31-8c74-40be-99e2-409af7ab80e0\",\n" +
                "    \"key\": \"0e86f56e-f69a-4704-b4c4-69057a0be7bc\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());

    }
}

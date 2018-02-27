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
            "     \"context\": { \n" +
            "          \"did\":\"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\"\n" +
            "     },\n" +
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
            "              \"current_level\":\"Primary 5\",\n" +
            "              \"score\":\"15\"\n" +
            "          }\n" +
                    "}";

    public static final String JSON_WITH_MID = "{\n" +
            "     \"eid\": \"ME_USER_GAME_LEVEL\",\n" +
            "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
            "     \"ver\": \"1.0\",\n" +
            "     \"uid\": \"3ee97c77-3587-4394-aff6-32b12576fcf6\",\n" +
            "     \"did\": \"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\",\n" +
            "     \"mid\": \"4aa9291822a0f3df7f91f36a4cca8445ebb458a4\",\n" +
            "     \"context\": { \n" +
            "          \"did\":\"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\"\n" +
            "     },\n" +
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
            "              \"current_level\":\"Primary 5\",\n" +
            "              \"score\":\"15\"\n" +
            "          }\n" +
            "}";


    public static Map<String, Object> locationPresent() {

        String json = "{\n" +
                "  \"@timestamp\": \"2016-05-03T04:36:14.921Z\",\n" +
                "  \"@version\": \"1\",\n" +
                "     \"context\": { \n" +
                "          \"did\":\"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\"\n" +
                "     },\n" +
                "  \"edata\": {\n" +
                "      \"loc\": \"12.9312015,77.6238068\"\n" +
                "  },\n" +
                "  \"eid\": \"GE_SESSION_START\",\n" +
                "  \"ets\": 1462250323723,\n" +
                "  \"gdata\": {\n" +
                "    \"id\": \"genie.android\",\n" +
                "    \"ver\": \"4.2.282productionxWalk.6\"\n" +
                "  },\n" +
                "  \"key\": \"\",\n" +
                "  \"mid\": \"8c44a1c1-8919-4c6a-a256-69d14616c757\",\n" +
                "  \"sid\": \"\",\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"genie\": []\n" +
                "    }\n" +
                "  ],\n" +
                "  \"ts\": \"2016-05-03T04:38:43.723+0000\",\n" +
                "  \"type\": \"events\",\n" +
                "  \"uid\": \"\",\n" +
                "  \"uuid\": \"be4f0aef-3cd8-42bc-b92d-2f7b3901011418\",\n" +
                "  \"ver\": \"2.0\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());

    }

    public static Map<String, Object> locationEmpty() {

        String json = "{\n" +
                "  \"@timestamp\": \"2016-05-03T04:36:14.921Z\",\n" +
                "  \"@version\": \"1\",\n" +
                "     \"context\": { \n" +
                "          \"did\":\"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\"\n" +
                "     },\n" +
                "  \"edata\": {\n" +
                "      \"loc\": \"\"\n" +
                "  },\n" +
                "  \"eid\": \"GE_SESSION_START\",\n" +
                "  \"ets\": 1462250323723,\n" +
                "  \"gdata\": {\n" +
                "    \"id\": \"genie.android\",\n" +
                "    \"ver\": \"4.2.282productionxWalk.6\"\n" +
                "  },\n" +
                "  \"key\": \"\",\n" +
                "  \"mid\": \"8c44a1c1-8919-4c6a-a256-69d14616c757\",\n" +
                "  \"sid\": \"\",\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"genie\": []\n" +
                "    }\n" +
                "  ],\n" +
                "  \"ts\": \"2016-05-03T04:38:43.723+0000\",\n" +
                "  \"type\": \"events\",\n" +
                "  \"uid\": \"\",\n" +
                "  \"uuid\": \"be4f0aef-3cd8-42bc-b92d-2f7b3901011418\",\n" +
                "  \"ver\": \"2.0\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());

    }

    public static Map<String, Object> locationAbsent() {

        String json = "{\n" +
                "  \"@timestamp\": \"2016-05-03T04:36:14.921Z\",\n" +
                "  \"@version\": \"1\",\n" +
                "     \"context\": { \n" +
                "          \"did\":\"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\"\n" +
                "     },\n" +
                "  \"edata\": {\n" +
                "  },\n" +
                "  \"eid\": \"GE_SESSION_START\",\n" +
                "  \"ets\": 1462250323723,\n" +
                "  \"gdata\": {\n" +
                "    \"id\": \"genie.android\",\n" +
                "    \"ver\": \"4.2.282productionxWalk.6\"\n" +
                "  },\n" +
                "  \"key\": \"\",\n" +
                "  \"mid\": \"8c44a1c1-8919-4c6a-a256-69d14616c757\",\n" +
                "  \"sid\": \"\",\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"genie\": []\n" +
                "    }\n" +
                "  ],\n" +
                "  \"ts\": \"2016-05-03T04:38:43.723+0000\",\n" +
                "  \"type\": \"events\",\n" +
                "  \"uid\": \"\",\n" +
                "  \"uuid\": \"be4f0aef-3cd8-42bc-b92d-2f7b3901011418\",\n" +
                "  \"ver\": \"2.0\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());

    }

    public static Map<String, Object> emptyChannel() {

        String json = "{\n" +
                "  \"@timestamp\": \"2016-05-03T04:36:14.921Z\",\n" +
                "  \"@version\": \"1\",\n" +
                "     \"context\": { \n" +
                "          \"did\":\"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\"\n" +
                "     },\n" +
                "  \"edata\": {\n" +
                "  },\n" +
                "  \"eid\": \"GE_SESSION_START\",\n" +
                "  \"ets\": 1462250323723,\n" +
                "  \"gdata\": {\n" +
                "    \"id\": \"genie.android\",\n" +
                "    \"ver\": \"4.2.282productionxWalk.6\"\n" +
                "  },\n" +
                "  \"key\": \"\",\n" +
                "  \"mid\": \"8c44a1c1-8919-4c6a-a256-69d14616c757\",\n" +
                "  \"sid\": \"\",\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"genie\": []\n" +
                "    }\n" +
                "  ],\n" +
                "  \"ts\": \"2016-05-03T04:38:43.723+0000\",\n" +
                "  \"type\": \"events\",\n" +
                "  \"uid\": \"\",\n" +
                "  \"channel\": \"\",\n" +
                "  \"uuid\": \"be4f0aef-3cd8-42bc-b92d-2f7b3901011418\",\n" +
                "  \"ver\": \"2.0\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());

    }
}

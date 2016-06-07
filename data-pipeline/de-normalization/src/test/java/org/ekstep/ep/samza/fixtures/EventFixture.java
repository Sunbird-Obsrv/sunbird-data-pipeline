package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
    private static final String JSON = "{\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"loc\": \"\",\n" +
            "            \"uid\": \"6\",\n" +
            "            \"age\": 5,\n" +
            "            \"handle\": \"Jijesh\",\n" +
            "            \"standard\": -1,\n" +
            "            \"language\": \"ML\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"did\": \"cbeda6a2ef327eaee21008de6495f89476aba58d\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genieservice.android\",\n" +
            "        \"ver\": \"1.0.local-qa-debug\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"tags\": [],\n" +
            "    \"ts\": \"2015-09-27T13:03:43-04:00\",\n" +
            "    \"uid\": \"6\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"metadata\": {\n" +
            "        \"checksum\": \"22e1430f2e5f339230dbf9595b060008\"\n" +
            "    }\n" +
            "}";

    private static final String ANOTHER_JSON = "{\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"loc\": \"\",\n" +
            "            \"uid\": \"7\",\n" +
            "            \"age\": 6,\n" +
            "            \"handle\": \"Abhi\",\n" +
            "            \"standard\": -1,\n" +
            "            \"language\": \"ML\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"did\": \"cbeda6a2ef327eaee21008de6495f89476aba58d\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genieservice.android\",\n" +
            "        \"ver\": \"1.0.local-qa-debug\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"tags\": [],\n" +
            "    \"ts\": \"2015-09-27T13:03:43-04:00\",\n" +
            "    \"uid\": \"6\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"metadata\": {\n" +
            "        \"checksum\": \"22e1430f2e5f339230dbf9595b060078\"\n" +
            "    }\n" +
            "}";

    public static Map<String, Object> GeCreateProfile() {
        return new Gson().fromJson(JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> AnotherGeCreateProfile() {
        return new Gson().fromJson(ANOTHER_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}

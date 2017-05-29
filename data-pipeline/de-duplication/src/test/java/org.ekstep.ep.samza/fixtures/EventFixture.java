package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
        private static final String EVENT_WITH_CHECKSUM = "{\n" +
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

    private static final String EVENT_WITH_MID = "{\n" +
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
            "    \"mid\": \"22e1430f2e5f339230dbf9595b060008\"" +
            "}";

    private static final String EVENT_WITHOUT_CHECKSUM = "{\n" +
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
            "    \"ver\": \"1.0\"" +
            "}";

    public static Map<String, Object> EventWithChecksumMap() {
        return new Gson().fromJson(EVENT_WITH_CHECKSUM, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> EventWithMidMap() {
        return new Gson().fromJson(EVENT_WITH_MID, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> EventWithoutChecksumFieldMap() {
        return new Gson().fromJson(EVENT_WITHOUT_CHECKSUM, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static String EventWithChecksumJson() {
        return EVENT_WITH_CHECKSUM;
    }

}

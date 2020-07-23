package org.sunbird.dp.domain;

import com.google.gson.Gson;

import java.util.Map;

public class EventFixture {

    public static final String IMPRESSION_EVENT_MISSING_FIELDS = "{\n" +
            "  \"eid\": \"IMPRESSION\",\n" +
            "  \"ets\": 1574945199426,\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"IMPRESSION:0093c96434557b2ead169c7156e95770\",\n" +
            "  \"actor\": {\n" +
            "    \"id\": \"f1a99b5cf111be23bd0e8d48a50458c6\",\n" +
            "    \"type\": \"User\"\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"prod.diksha.portal\",\n" +
            "      \"ver\": \"2.0.0\",\n" +
            "      \"pid\": \"sunbird-portal\"\n" +
            "    },\n" +
            "    \"env\": \"public\",\n" +
            "    \"sid\": \"1f077b10-11dd-11ea-bbdc-fbe7180a137e\",\n" +
            "    \"did\": \"f1a99b5cf111be23bd0e8d48a50458c6\",\n" +
            "    \"cdata\": [],\n" +
            "    \"rollup\": {\n" +
            "      \"l1\": \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
            "    },\n" +
            "    \"uid\": \"anonymous\"\n" +
            "  },\n" +
            "  \"object\": {},\n" +
            "  \"tags\": [\n" +
            "    \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
            "  ],\n" +
            "  \"edata\": {\n" +
            "    \"type\": \"view\",\n" +
            "    \"pageid\": \"landing\",\n" +
            "    \"subtype\": \"init\",\n" +
            "    \"uri\": \"https://diksha.gov.in/\",\n" +
            "    \"visits\": []\n" +
            "  }\n" +
            "}";

    public static final String IMPRESSION_EVENT = "{\n" +
            "  \"eid\": \"IMPRESSION\",\n" +
            "  \"ets\": 1574945199426,\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"IMPRESSION:0093c96434557b2ead169c7156e95770\",\n" +
            "  \"actor\": {\n" +
            "    \"id\": \"f1a99b5cf111be23bd0e8d48a50458c6\",\n" +
            "    \"type\": \"User\"\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"prod.diksha.portal\",\n" +
            "      \"ver\": \"2.0.0\",\n" +
            "      \"pid\": \"sunbird-portal\"\n" +
            "    },\n" +
            "    \"env\": \"public\",\n" +
            "    \"sid\": \"1f077b10-11dd-11ea-bbdc-fbe7180a137e\",\n" +
            "    \"did\": \"f1a99b5cf111be23bd0e8d48a50458c6\",\n" +
            "    \"cdata\": [],\n" +
            "    \"rollup\": {\n" +
            "      \"l1\": \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
            "    },\n" +
            "    \"uid\": \"anonymous\"\n" +
            "  },\n" +
            "  \"object\": {\n" +
            "    \"type\": \"content\",\n" +
            "    \"id\": \"658374_49785\"\n" +
            "  },\n" +
            "  \"tags\": [\n" +
            "    \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
            "  ],\n" +
            "  \"edata\": {\n" +
            "    \"type\": \"view\",\n" +
            "    \"pageid\": \"landing\",\n" +
            "    \"subtype\": \"init\",\n" +
            "    \"uri\": \"https://diksha.gov.in/\",\n" +
            "    \"visits\": []\n" +
            "  },\n" +
            "  \"metadata\": {\n" +
            "    \"checksum\": \"55437853\"\n" +
            "  },\n" +
            "  \"@timestamp\": 1575265622000,\n" +
            "  \"syncts\": 56543875684\n" +
            "}";






    public static Map<String, Object> getMap(String message) {
        return (Map<String, Object>) new Gson().fromJson(message, Map.class);
    }

}

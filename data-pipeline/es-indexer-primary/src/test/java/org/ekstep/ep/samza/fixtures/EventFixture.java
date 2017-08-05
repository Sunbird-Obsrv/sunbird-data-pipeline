package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
    public static final String EVENT_WITH_INDEX_DETAILS = "{\n" +
            "    \"tags\": [],\n" +
            "    \"uid\": \"ea6f7688-a5fb-4ec9-a851-28fc761e2c8d\",\n" +
            "    \"cdata\": [\n" +
            "        {\n" +
            "            \"id\": \"06f3a120f3b2c8380d365063fd955728ad834719\",\n" +
            "            \"type\": \"api-org.ekstep.genie.content.explore\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"sid\": \"75b437c1-c1e6-4676-b573-5a296df02c26\",\n" +
            "    \"ts\": \"2017-02-21T09:07:25.579+0000\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"err\": \"\",\n" +
            "            \"gid\": \"do_30076072\",\n" +
            "            \"tmschm\": \"INTENT\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"did\": \"ef37fc07aee31d87b386a408e0e4651e00486618\",\n" +
            "    \"ver\": \"2.0\",\n" +
            "    \"type\": \"events\",\n" +
            "    \"@version\": \"1\",\n" +
            "    \"eid\": \"GE_LAUNCH_GAME\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"5.6.localdev-debug.20\"\n" +
            "    },\n" +
            "    \"@timestamp\": \"2017-02-21T09:04:49.138Z\",\n" +
            "    \"ets\": 1487668045579,\n" +
            "    \"uuid\": \"b5fb3e21-4c62-4654-bde9-70b57edfbd8b126\",\n" +
            "    \"mid\": \"d35199df-25ac-4407-8493-246dc2b6c477\",\n" +
            "    \"key\": \"ea6f7688-a5fb-4ec9-a851-28fc761e2c8d\",\n" +
            "    \"metadata\": {\n" +
            "        \"checksum\": \"ea6f7688-a5fb-4ec4-a851-28fc761e2c8d\",\n" +
            "        \"index_name\": \"ecosystem\",\n" +
            "        \"index_type\": \"events_v1\"\n" +
            "    }\n" +
            "}";

    public static final String EVENT_WITHOUT_INDEX_DETAILS = "{\n" +
            "    \"tags\": [],\n" +
            "    \"uid\": \"ea6f7688-a5fb-4ec9-a851-28fc761e2c8d\",\n" +
            "    \"cdata\": [\n" +
            "        {\n" +
            "            \"id\": \"06f3a120f3b2c8380d365063fd955728ad834719\",\n" +
            "            \"type\": \"api-org.ekstep.genie.content.explore\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"sid\": \"75b437c1-c1e6-4676-b573-5a296df02c26\",\n" +
            "    \"ts\": \"2017-02-21T09:07:25.579+0000\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"err\": \"\",\n" +
            "            \"gid\": \"do_30076072\",\n" +
            "            \"tmschm\": \"INTENT\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"did\": \"ef37fc07aee31d87b386a408e0e4651e00486618\",\n" +
            "    \"ver\": \"2.0\",\n" +
            "    \"type\": \"events\",\n" +
            "    \"@version\": \"1\",\n" +
            "    \"eid\": \"GE_LAUNCH_GAME\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"5.6.localdev-debug.20\"\n" +
            "    },\n" +
            "    \"@timestamp\": \"2017-02-21T09:04:49.138Z\",\n" +
            "    \"ets\": 1487668045579,\n" +
            "    \"uuid\": \"b5fb3e21-4c62-4654-bde9-70b57edfbd8b126\",\n" +
            "    \"mid\": \"d35199df-25ac-4407-8493-246dc2b6c477\",\n" +
            "    \"key\": \"ea6f7688-a5fb-4ec9-a851-28fc761e2c8d\",\n" +
            "    \"metadata\": {\n" +
            "        \"checksum\": \"ea6f7688-a5fb-4ec4-a851-28fc761e2c8d\"\n" +
            "    }\n" +
            "}";

    public static Map<String, Object> EventWithIndexDetails() {
        return new Gson().fromJson(EVENT_WITH_INDEX_DETAILS, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> EventWithoutIndexDetails() {
        return new Gson().fromJson(EVENT_WITHOUT_INDEX_DETAILS, new TypeToken<Map<String, Object>>() {
        }.getType());
    }


}

package org.ekstep.ep.samza.fixture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
    public static final String OE_ASSESS_JSON_STRING = "{\n" +
            "    \"tags\": [\n" +
            "        {\n" +
            "            \"genie\": []\n" +
            "        }\n" +
            "    ],\n" +
            "    \"uid\": \"a2833275-980a-42a3-a47d-b2af0015e5d7\",\n" +
            "    \"sid\": \"c2a504ee-2817-45a8-a59c-eb63d2545f55\",\n" +
            "    \"ts\": \"2017-02-21T09:22:24.315+0530\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"exlength\": 0,\n" +
            "            \"length\": 17,\n" +
            "            \"params\": [],\n" +
            "            \"pass\": \"No\",\n" +
            "            \"qdesc\": \"\",\n" +
            "            \"qid\": \"eks.tipu.q3\",\n" +
            "            \"qindex\": 1,\n" +
            "            \"resvalues\": [],\n" +
            "            \"score\": 0,\n" +
            "            \"uri\": \"\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"did\": \"b5cf527fa3fab82d75c11dcda3bde3628b2efb12\",\n" +
            "    \"ver\": \"2.0\",\n" +
            "    \"type\": \"events\",\n" +
            "    \"eid\": \"OE_ASSESS\",\n" +
            "    \"@version\": \"1\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"do_30076072\",\n" +
            "        \"ver\": \"1\"\n" +
            "    },\n" +
            "    \"@timestamp\": \"2017-02-21T03:53:33.944Z\",\n" +
            "    \"ets\": 1487649144315,\n" +
            "    \"uuid\": \"9627695d-dc9c-4556-b928-dede2625b935142\",\n" +
            "    \"mid\": \"b676bda4-2a75-4482-8643-bf395e9dcf9d\",\n" +
            "    \"key\": \"a2833275-980a-42a3-a47d-b2af0015e5d7\"\n" +
            "}";


    public static final String JSON_WITHOUT_CONTENT_ID = "{\n" +
            "    \"eid\": \"GE_INTERACT\",\n" +
            "    \"@version\": \"1\",\n" +
            "    \"gdata\": {\n" +
            "        \"ver\": \"1\"\n" +
            "    },\n" +
            "    \"@timestamp\": \"2017-02-21T03:53:33.944Z\",\n" +
            "    \"ets\": 1487649144315\n" +
            "}";

    public static final String ME_EVENT_JSON = "{\n" +
            "    \"eid\": \"ME_GENIE_USAGE_SUMMARY\",\n" +
            "    \"ets\": 1487660892491,\n" +
            "    \"syncts\": 1487622011190,\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"mid\": \"1661C8EA1D5209D6ACF708FA0602BB49\",\n" +
            "    \"uid\": \"\",\n" +
            "    \"context\": {\n" +
            "        \"pdata\": {\n" +
            "            \"id\": \"AnalyticsDataPipeline\",\n" +
            "            \"model\": \"GenieUsageSummaryModel\",\n" +
            "            \"ver\": \"1.0\"\n" +
            "        },\n" +
            "        \"granularity\": \"DAY\",\n" +
            "        \"date_range\": {\n" +
            "            \"from\": 1487621998810,\n" +
            "            \"to\": 1487622007043\n" +
            "        }\n" +
            "    },\n" +
            "    \"dimensions\": {\n" +
            "        \"tag\": \"all\",\n" +
            "        \"period\": 20170221\n" +
            "    },\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"device_ids\": [\n" +
            "                \"80f09a519421035b0dfc3e264dc520b27b7fb596\"\n" +
            "            ],\n" +
            "            \"avg_ts_session\": 1.9,\n" +
            "            \"total_sessions\": 3,\n" +
            "            \"contents\": [],\n" +
            "            \"total_ts\": 5.71\n" +
            "        }\n" +
            "    },\n" +
            "    \"@version\": \"1\",\n" +
            "    \"@timestamp\": \"2017-02-21T07:08:20.148Z\",\n" +
            "    \"type\": \"events\",\n" +
            "    \"learning\": \"true\",\n" +
            "    \"ts\": \"2017-02-21T12:38:12.000+0530\"\n" +
            "}";

    public static final String OTHER_GE_EVENT = "{\n" +
            "    \"tags\": [\n" +
            "        {\n" +
            "            \"genie\": []\n" +
            "        }\n" +
            "    ],\n" +
            "    \"uid\": \"ea6f7688-a5fb-4ec9-a851-28fc761e2c8d\",\n" +
            "    \"cdata\": [],\n" +
            "    \"sid\": \"75b437c1-c1e6-4676-b573-5a296df02c26\",\n" +
            "    \"ts\": \"2017-02-21T09:07:35.146+0000\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"extype\": \"\",\n" +
            "            \"id\": \"\",\n" +
            "            \"pos\": [],\n" +
            "            \"stageid\": \"Settings-Home\",\n" +
            "            \"subtype\": \"\",\n" +
            "            \"tid\": \"\",\n" +
            "            \"type\": \"SHOW\",\n" +
            "            \"uri\": \"\",\n" +
            "            \"values\": []\n" +
            "        }\n" +
            "    },\n" +
            "    \"did\": \"ef37fc07aee31d87b386a408e0e4651e00486618\",\n" +
            "    \"ver\": \"2.0\",\n" +
            "    \"type\": \"events\",\n" +
            "    \"@version\": \"1\",\n" +
            "    \"eid\": \"GE_INTERACT\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"5.6.localdev-debug.20\"\n" +
            "    },\n" +
            "    \"@timestamp\": \"2017-02-21T09:04:49.159Z\",\n" +
            "    \"ets\": 1487668055146,\n" +
            "    \"uuid\": \"b5fb3e21-4c62-4654-bde9-70b57edfbd8b157\",\n" +
            "    \"mid\": \"a65b028d-4fd1-459a-84b3-77ca293484b3\",\n" +
            "    \"key\": \"ea6f7688-a5fb-4ec9-a851-28fc761e2c8d\"\n" +
            "}";


    public static final String GE_LAUNCH_EVENT = "{\n" +
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
            "            \"gid\": \"do_30074541\",\n" +
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
            "    \"key\": \"ea6f7688-a5fb-4ec9-a851-28fc761e2c8d\"\n" +
            "}";

    public static Map<String, Object> OeEvent() {
        return new Gson().fromJson(OE_ASSESS_JSON_STRING, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> GeLaunchEvent() {
        return new Gson().fromJson(GE_LAUNCH_EVENT, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> MeEvent() {
        return new Gson().fromJson(ME_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> OtherGeEvent() {
        return new Gson().fromJson(OTHER_GE_EVENT, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> EventWithoutContentId() {
        return new Gson().fromJson(JSON_WITHOUT_CONTENT_ID, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}

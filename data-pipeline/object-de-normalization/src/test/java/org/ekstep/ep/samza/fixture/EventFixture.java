package org.ekstep.ep.samza.fixture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
    public static final String OE_ASSESS_JSON_STRING = "{\n" +
            "\n" +
            "  \"eid\": \"ASSESS\",\n" +
            "  \"ets\": \"1442816723\",\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"\",\n" +
            "  \"actor\": { \n" +
            "    \"id\": \"\",\n" +
            "    \"type\": \"user\"\n" +
            "  },\n" +
            "  \"context\": { \n" +
            "    \"channel\": \"ekstep\",\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"\",\n" +
            "      \"pid\": \"\",\n" +
            "      \"ver\": \"\"\n" +
            "    },\n" +
            "    \"env\": \"\",\n" +
            "    \"sid\": \"\",\n" +
            "    \"did\": \"\",\n" +
            "    \"cdata\": [{ \n" +
            "      \"type\":\"\", \n" +
            "      \"id\": \"\"\n" +
            "    }],\n" +
            "    \"rollup\": { \n" +
            "      \"l1\": \"\",\n" +
            "      \"l2\": \"\",\n" +
            "      \"l3\": \"\",\n" +
            "      \"l4\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"object\": { \n" +
            "    \"id\": \"do_30076072\", \n" +
            "    \"type\": \"Content\", \n" +
            "    \"ver\": \"\" , \n" +
            "    \"rollup\": { \n" +
            "    \t\"l1\": \"\",\n" +
            "      \"l2\": \"\",\n" +
            "      \"l3\": \"\",\n" +
            "      \"l4\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"edata\": {},\n" +
            "  \"tags\": []\n" +
            "}";

    public static final String ME_EVENT_JSON = "{\n" +
            "    \"eid\": \"ME_ITEM_USAGE_SUMMARY\",\n" +
            "    \"ets\": 1489111212816,\n" +
            "    \"syncts\": 1489069555431,\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"mid\": \"07FF6BB2531844F506CBDE67E95E8357\",\n" +
            "    \"uid\": \"\",\n" +
            "    \"context\": {\n" +
            "        \"pdata\": {\n" +
            "            \"id\": \"AnalyticsDataPipeline\",\n" +
            "            \"model\": \"ItemSummaryModel\",\n" +
            "            \"ver\": \"1.0\"\n" +
            "        },\n" +
            "        \"granularity\": \"DAY\",\n" +
            "        \"date_range\": {\n" +
            "            \"from\": 1489069599278,\n" +
            "            \"to\": 1489069621675\n" +
            "        }\n" +
            "    },\n" +
            "    \"dimensions\": {\n" +
            "        \"tag\": \"all\",\n" +
            "        \"period\": 20170309,\n" +
            "        \"content_id\": \"do_30076072\",\n" +
            "        \"item_id\": \"ek.n.ib.en.ad.I.10\"\n" +
            "    },\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"avg_ts\": 0,\n" +
            "            \"inc_res_count\": 2,\n" +
            "            \"incorrect_res\": [],\n" +
            "            \"total_ts\": 0,\n" +
            "            \"total_count\": 2,\n" +
            "            \"correct_res_count\": 0,\n" +
            "            \"correct_res\": []\n" +
            "        }\n" +
            "    },\n" +
            "    \"@version\": \"1\",\n" +
            "    \"@timestamp\": \"2017-03-10T01:57:59.814Z\",\n" +
            "    \"type\": \"events\",\n" +
            "    \"learning\": \"true\",\n" +
            "    \"ts\": \"2017-03-10T02:00:12.000+0000\"\n" +
            "}";

    public static final String EVENT_WITHOUT_OBJECT_ID = "{\n" +
            "\n" +
            "  \"eid\": \"ASSESS\",\n" +
            "  \"ets\": \"1442816723\",\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"\",\n" +
            "  \"actor\": { \n" +
            "    \"id\": \"\",\n" +
            "    \"type\": \"user\"\n" +
            "  },\n" +
            "  \"edata\": {},\n" +
            "  \"tags\": []\n" +
            "}";

    public static final String EVENT_WITH_EMPTY_OBJECT_ID = "{\n" +
            "  \"object\": { \n" +
            "    \"id\": \"\", \n" +
            "    \"type\": \"Content\", \n" +
            "    \"ver\": \"\" , \n" +
            "    \"rollup\": { \n" +
            "    \t\"l1\": \"\",\n" +
            "      \"l2\": \"\",\n" +
            "      \"l3\": \"\",\n" +
            "      \"l4\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"edata\": {},\n" +
            "  \"eid\": \"START\", \n" +
            "  \"tags\": []\n" +
            "}";


    public static Map<String, Object> OeEvent() {
        return new Gson().fromJson(OE_ASSESS_JSON_STRING, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> MeEvent() {
        return new Gson().fromJson(ME_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> EventWithoutObjectID() {
        return new Gson().fromJson(EVENT_WITHOUT_OBJECT_ID, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> EventWithEmptyObjectID() {
        return new Gson().fromJson(EVENT_WITH_EMPTY_OBJECT_ID, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

}

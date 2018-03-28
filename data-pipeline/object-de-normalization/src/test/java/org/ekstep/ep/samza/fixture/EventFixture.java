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
            "  \"eid\": \"LOG\",\n" +
            "  \"ets\": 1519026217617,\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"LP.1519026217617.14f44fcf-4bb0-4b0b-9dd3-ceea958373b4\",\n" +
            "  \"actor\": {\n" +
            "    \"id\": \"org.ekstep.learning.platform\",\n" +
            "    \"type\": \"System\"\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"channel\": \"in.ekstep\",\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"org.ekstep.learning.platform\",\n" +
            "      \"pid\": \"search-service\",\n" +
            "      \"ver\": \"1.0\"\n" +
            "    },\n" +
            "    \"env\": \"search\"\n" +
            "  },\n" +
            "  \"edata\": {\n" +
            "    \"level\": \"INFO\",\n" +
            "    \"type\": \"api_access\",\n" +
            "    \"message\": \"\",\n" +
            "    \"params\": [\n" +
            "      {\n" +
            "        \"duration\": 2.0\n" +
            "      },\n" +
            "      {\n" +
            "        \"protocol\": \"HTTP\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"size\": 289.0\n" +
            "      },\n" +
            "      {\n" +
            "        \"method\": \"GET\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"rid\": \"search-service.health\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"uip\": \"10.32.6.7\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"url\": \"/health\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"status\": 200.0\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true,\n" +
            "    \"dd_processed\": true,\n" +
            "    \"ldata_obtained\": false,\n" +
            "    \"ldata_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\",\n" +
            "  \"ts\": \"2018-02-19T07:43:37.617+0000\",\n" +
            "  \"metadata\": {\n" +
            "    \"checksum\": \"LP.1519026217617.14f44fcf-4bb0-4b0b-9dd3-ceea958373b4\"\n" +
            "  }\n" +
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
    public static final String EVENT_WITH_WORD_OBJECT = "{\n" +
            "  \"eid\": \"END\",\n" +
            "  \"ets\": 1522215084870,\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"END:ca93706b305734ba8ddff58755681f15\",\n" +
            "  \"actor\": {\n" +
            "    \"id\": \"446\",\n" +
            "    \"type\": \"User\"\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"channel\": \"in.ekstep\",\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"dev.ekstep.portal\",\n" +
            "      \"pid\": \"ekstep_portal\",\n" +
            "      \"ver\": \"382\"\n" +
            "    },\n" +
            "    \"env\": \"word\",\n" +
            "    \"sid\": \"bdvd04os32j4bfneii5g4q4dl0\",\n" +
            "    \"did\": \"dd38152e81d552ad3e9e1dbace364322\",\n" +
            "    \"cdata\": [],\n" +
            "    \"rollup\": {}\n" +
            "  },\n" +
            "  \"object\": {\n" +
            "    \"id\": \"18605\",\n" +
            "    \"type\": \"word\",\n" +
            "    \"ver\": \"382\"\n" +
            "  },\n" +
            "  \"tags\": [],\n" +
            "  \"edata\": {\n" +
            "    \"type\": \"word\",\n" +
            "    \"mode\": \"edit\",\n" +
            "    \"pageid\": \"word-detail\",\n" +
            "    \"summary\": []\n" +
            "  }\n" +
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

    public static Map<String, Object> EventWithOtherObjectType() {
        return new Gson().fromJson(EVENT_WITH_WORD_OBJECT, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}

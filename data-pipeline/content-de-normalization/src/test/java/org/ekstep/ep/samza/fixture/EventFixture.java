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

    public static final String BE_EVENT_JSON = "{\n" +
            "    \"eid\": \"BE_CONTENT_LIFECYCLE\",\n" +
            "    \"ets\": 1491911140141,\n" +
            "    \"ver\": \"2.0\",\n" +
            "    \"pdata\": {\n" +
            "        \"ver\": \"1.0\",\n" +
            "        \"pid\": \"\",\n" +
            "        \"id\": \"org.ekstep.content.platform\"\n" +
            "    },\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"prevState\": \"Processing\",\n" +
            "            \"size\": 11652,\n" +
            "            \"concepts\": null,\n" +
            "            \"downloadUrl\": \"https://ekstep-public-qa.s3-ap-south-1.amazonaws.com\",\n" +
            "            \"flags\": null,\n" +
            "            \"mediaType\": \"content\",\n" +
            "            \"state\": \"Live\",\n" +
            "            \"contentType\": \"Plugin\",\n" +
            "            \"pkgVersion\": 8,\n" +
            "            \"cid\": \"do_30076072\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"@version\": \"1\",\n" +
            "    \"@timestamp\": \"2017-04-11T11:42:32.917Z\",\n" +
            "    \"type\": \"backend.events\",\n" +
            "    \"ts\": \"2017-04-11T11:45:40.000+0000\"\n" +
            "}";

    public static final String CP_EVENT_JSON = "{\n" +
            "    \"eid\": \"CP_INTERACT\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"type\": \"event\",\n" +
            "            \"title\": null,\n" +
            "            \"category\": \"Content.Publish\",\n" +
            "            \"size\": null,\n" +
            "            \"duration\": null,\n" +
            "            \"status\": \"\",\n" +
            "            \"protocol\": \"\",\n" +
            "            \"method\": \"\",\n" +
            "            \"action\": \"do_30076072\",\n" +
            "            \"value\": \"\",\n" +
            "            \"params\": {\n" +
            "                \"piwik_idlink_va\": \"594565\"\n" +
            "            },\n" +
            "            \"uip\": \"182.73.0.0\",\n" +
            "            \"rid\": \"qa.ekstep.in/content/searchContent\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"ets\": 1491891374000,\n" +
            "    \"ver\": \"2.0\",\n" +
            "    \"pdata\": {\n" +
            "        \"id\": \"https://qa.ekstep.in\",\n" +
            "        \"pid\": \"com.ekstep.piwik\",\n" +
            "        \"ver\": \"\"\n" +
            "    },\n" +
            "    \"uid\": \"ee16ee0fa7f21a4101793582e1cf4b24fef9f948\",\n" +
            "    \"rid\": \"\",\n" +
            "    \"@version\": \"1\",\n" +
            "    \"@timestamp\": \"2017-04-11T11:50:02.731Z\",\n" +
            "    \"path\": \"/var/log/piwik/piwik.raw.idsite_2.log\",\n" +
            "    \"host\": \"ip-10-42-6-49\",\n" +
            "    \"tags\": [\n" +
            "        \"piwik_json\"\n" +
            "    ],\n" +
            "    \"type\": \"backend.events\",\n" +
            "    \"ts\": \"2017-04-11T06:16:14.000+0000\"\n" +
            "}";

    public static final String CE_EVENT_JSON = "{\n" +
            "    \"eid\": \"CE_INTERACT\",\n" +
            "    \"ets\": 1491932923636,\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"pdata\": {\n" +
            "        \"id\": \"ATTool\",\n" +
            "        \"pid\": \"ContentEditor\",\n" +
            "        \"ver\": \"2.0\"\n" +
            "    },\n" +
            "    \"cdata\": [],\n" +
            "    \"uid\": \"266\",\n" +
            "    \"context\": {\n" +
            "        \"sid\": \"e64f3u1bcbb2q3j376m012or16\",\n" +
            "        \"content_id\": \"do_30076072\"\n" +
            "    },\n" +
            "    \"rid\": \"\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"type\": \"click\",\n" +
            "            \"subtype\": \"menu\",\n" +
            "            \"target\": \"previewButton\",\n" +
            "            \"pluginid\": \"\",\n" +
            "            \"pluginver\": \"\",\n" +
            "            \"objectid\": \"preview\",\n" +
            "            \"stage\": \"190e950a-93c6-4c15-87b9-e907463ab31b\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"tags\": [\n" +
            "        \"piwik_json\"\n" +
            "    ],\n" +
            "    \"@version\": \"1\",\n" +
            "    \"@timestamp\": \"2017-04-11T17:50:02.285Z\",\n" +
            "    \"path\": \"/var/log/piwik/piwik.raw.idsite_2.log\",\n" +
            "    \"host\": \"ip-10-42-6-49\",\n" +
            "    \"type\": \"backend.events\",\n" +
            "    \"ts\": \"2017-04-11T17:48:43.000+0000\"\n" +
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

    public static Map<String, Object> BeEvent() {
        return new Gson().fromJson(BE_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> CpEvent() {
        return new Gson().fromJson(CP_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> CeEvent() {
        return new Gson().fromJson(CE_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

}

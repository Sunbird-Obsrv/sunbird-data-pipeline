package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
    public static Map<String, Object> Interact() {
        String json = "{\n" +
                "  \"actor\": {\n" +
                "    \"id\": \"9a4cd587-751d-4e22-9ee6-9bb18edc07f6\",\n" +
                "    \"type\": \"User\"\n" +
                "  },\n" +
                "  \"eid\": \"INTERACT\",\n" +
                "  \"edata\": {\n" +
                "    \"plugin\": {\n" +
                "      \"id\": \"\",\n" +
                "      \"ver\": \"\"\n" +
                "    },\n" +
                "    \"subtype\": \"AutoSync-Initiated\",\n" +
                "    \"extra\": {\n" +
                "      \"pos\": [],\n" +
                "      \"values\": [],\n" +
                "      \"uri\": \"\"\n" +
                "    },\n" +
                "    \"id\": \"\",\n" +
                "    \"pageid\": \"Genie-TelemetrySync\",\n" +
                "    \"type\": \"TOUCH\",\n" +
                "    \"target\": {\n" +
                "      \"id\": \"\",\n" +
                "      \"type\": \"TOUCH\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"ver\": \"3.0\",\n" +
                "  \"metadata\": {\n" +
                "    \"checksum\": \"c4040c6c-350f-4759-8cec-64654d32631d\",\n" +
                "    \"source_eid\": \"GE_INTERACT\",\n" +
                "    \"source_mid\": \"c4040c6c-350f-4759-8cec-64654d32631d\"\n" +
                "  },\n" +
                "  \"@timestamp\": \"2017-12-06T10:29:36.931Z\",\n" +
                "  \"ets\": 1512556259037,\n" +
                "  \"context\": {\n" +
                "    \"channel\": \"in.ekstep\",\n" +
                "    \"env\": \"Genie\",\n" +
                "    \"sid\": \"a612c30f-1031-41c0-8bd0-9a11503c9086\",\n" +
                "    \"did\": \"7d2f87dc71f3a72901740e2296c5c82807c754aa\",\n" +
                "    \"pdata\": {},\n" +
                "    \"cdata\": []\n" +
                "  },\n" +
                "  \"flags\": {\n" +
                "    \"tv_processed\": true,\n" +
                "    \"v2_converted\": true,\n" +
                "    \"dd_processed\": true\n" +
                "  },\n" +
                "  \"mid\": \"INTERACT:c4040c6c-350f-4759-8cec-64654d32631d\",\n" +
                "  \"object\": {\n" +
                "    \"id\": \"genieservice.android\",\n" +
                "    \"type\": \"TOUCH\",\n" +
                "    \"ver\": \"6.1.195\",\n" +
                "    \"subtype\": \"AutoSync-Initiated\",\n" +
                "    \"parent\": {\n" +
                "      \"id\": \"\",\n" +
                "      \"type\": \"\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"tags\": [],\n" +
                "  \"type\": \"events\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> PartnerData() {
        String json = "{\n" +
                "    \"tags\": [\n" +
                "        {\n" +
                "            \"partnerid\": [\n" +
                "                \"org.ekstep.partner.pratham\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"genie\": []\n" +
                "        }\n" +
                "    ],\n" +
                "    \"uid\": \"\",\n" +
                "    \"sid\": \"\",\n" +
                "   \"context\": {\n" +
                "       \"channel\": \"in.ekstep\"\n" +
                "    },\n" +
                "    \"ts\": \"2016-05-27T15:21:59.995+0530\",\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"data\": \"L0ouPhQSzupyU5O/w87KZqZp5UQ+ay2fiDwJ5d9mkN8=\\n\",\n" +
                "            \"iv\": \"O3ZBNFXvA6HXE54g1H7TbA==\\n\",\n" +
                "            \"key\": \"LLNrwwlPZCtUYtDgF3UKph/OFISao5RvC3JQmzJDCItH17ZHoGsAioUzMry5u9VM50vTSBjW0W2M\\nkR6Nf9oJ35sO0PPZLs7kyWYTXKXACWi4QkaTAyTvY0j1Vrd1YXL5Fp9hjbOCyPyyVrvyioBKfFrl\\nGvWX8D6WNQsLGRcMIujkCK3oD7d3d2PLb3CKNFdaPPbxxofojtWZFI71uR9tYe7QDJk+x4WkJnNs\\nNdWjJLc3qIEaea9CNca/7+hdGLeeyleqpEqh/KmxTyefUWyIZyfkiho31vaN44jLGtXrwhJYc3cc\\n7PjNr8AncYZ1o9WzGkbnObQqSD76Lqsmt+YjPA==\\n\",\n" +
                "            \"partnerid\": \"org.ekstep.partner.pratham\",\n" +
                "            \"publickeyid\": \"[B@44cec40\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"did\": \"89feecbf900d1730c59b71c35f3dca344d2bd5e3\",\n" +
                "    \"ver\": \"2.0\",\n" +
                "    \"type\": \"events\",\n" +
                "    \"eid\": \"EXDATA\",\n" +
                "    \"@version\": \"1\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"genieservice.android\",\n" +
                "        \"ver\": \"4.2.localqa-debug\"\n" +
                "    },\n" +
                "    \"@timestamp\": \"2016-05-27T09:55:18.100Z\",\n" +
                "    \"ets\": 1464342719995,\n" +
                "    \"uuid\": \"558a1acf-4995-4af8-bdca-3ac95f9064b225\",\n" +
                "    \"mid\": \"249a9496-165e-44f4-bf55-d06855ab71b1\",\n" +
                "    \"key\": \"\",\n" +
                "    \"metadata\": {\n" +
                "        \"checksum\": \"30b91597f09d477c29349a9f1de1d2671c2bdbca\",\n" +
                "        \"last_processed_at\": \"2016-05-27T15:24:38.049+05:30\",\n" +
                "        \"processed_count\": 1,\n" +
                "        \"ts_year\": \"2016\",\n" +
                "        \"ts_month\": \"05\",\n" +
                "        \"year\": \"2016\",\n" +
                "        \"month\": \"05\",\n" +
                "        \"monthday\": \"27\",\n" +
                "        \"hour\": \"09\",\n" +
                "        \"sync_timestamp\": \"2016-05-27T09:54:39+00:00\"\n" +
                "    },\n" +
                "    \"flags\": {\n" +
                "        \"ldata_processed\": true,\n" +
                "        \"ldata_obtained\": false\n" +
                "    },\n" +
                "    \"ready_to_index\": \"true\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> LearningEvent1() {
        String json = "{\n" +
                "  \"eid\": \"ME_SESSION_SUMMARY\",\n" +
                "  \"ets\": 1462881701027,\n" +
                "  \"syncts\": 1441324800000,\n" +
                "  \"ver\": \"2.0\",\n" +
                "  \"mid\": \"327A6D861952BC1B29C136E8806D3ECA\",\n" +
                "  \"uid\": \"98e3e4f3e47f839e28618812a90e2ac79a2adc8a\",\n" +
                "  \"context\": {\n" +
                "    \"pdata\": {\n" +
                "      \"id\": \"AnalyticsDataPipeline\",\n" +
                "      \"model\": \"LearnerSessionSummary\",\n" +
                "      \"ver\": \"1.0\"\n" +
                "    },\n" +
                "    \"granularity\": \"SESSION\",\n" +
                "    \"date_range\": {\n" +
                "      \"from\": 1441262850000,\n" +
                "      \"to\": 1441262943000\n" +
                "    },\n" +
                "    \"channel\": \"in.ekstep\"\n" +
                "  },\n" +
                "  \"dimensions\": {\n" +
                "    \"did\": \"d5df4a8a801b5b6d28fe24bbcaeee680ea8a75cf\",\n" +
                "    \"gdata\": {\n" +
                "      \"id\": \"org.ekstep.aser\",\n" +
                "      \"ver\": \"5.1\"\n" +
                "    },\n" +
                "    \"loc\": \"13.315301666666668,77.14502333333333\"\n" +
                "  },\n" +
                "  \"edata\": {\n" +
                "    \"eks\": {\n" +
                "      \"start_time\": 1441262850000,\n" +
                "      \"noOfLevelTransitions\": -1,\n" +
                "      \"levels\": [],\n" +
                "      \"activitySummary\": [\n" +
                "        {\n" +
                "          \"actType\": \"TOUCH\",\n" +
                "          \"count\": 8,\n" +
                "          \"timeSpent\": 93.0\n" +
                "        }\n" +
                "      ],\n" +
                "      \"noOfAttempts\": 1,\n" +
                "      \"screenSummary\": [],\n" +
                "      \"end_time\": 1441262943000,\n" +
                "      \"partnerId\": \"\",\n" +
                "      \"timeSpent\": 93.0,\n" +
                "      \"interactEventsPerMin\": 5.16,\n" +
                "      \"mimeType\": \"application/vnd.android.package-archive\",\n" +
                "      \"syncDate\": 1441324800000,\n" +
                "      \"anonymousUser\": false,\n" +
                "      \"contentType\": \"Game\",\n" +
                "      \"timeDiff\": 93.0,\n" +
                "      \"groupUser\": false,\n" +
                "      \"eventsSummary\": [\n" +
                "        {\n" +
                "          \"id\": \"OE_INTERACT\",\n" +
                "          \"count\": 8\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"OE_START\",\n" +
                "          \"count\": 1\n" +
                "        }\n" +
                "      ],\n" +
                "      \"currentLevel\": {},\n" +
                "      \"noOfInteractEvents\": 8,\n" +
                "      \"interruptTime\": 0.0,\n" +
                "      \"itemResponses\": [],\n" +
                "      \"telemetryVersion\": \"1.0\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> LearningEvent2() {
        String json = "{\n" +
                "  \"eid\": \"ME_GENIE_LAUNCH_SUMMARY\",\n" +
                "  \"ets\": 1465522333811,\n" +
                "  \"syncts\": 1465466359997,\n" +
                "  \"ver\": \"2.0\",\n" +
                "  \"mid\": \"0F5CE4150EA162407AD07F756CF40B88\",\n" +
                "  \"uid\": \"\",\n" +
                "  \"context\": {\n" +
                "    \"channel\": \"in.ekstep\",\n" +
                "    \"pdata\": {\n" +
                "      \"id\": \"AnalyticsDataPipeline\",\n" +
                "      \"model\": \"GenieUsageSummarizer\",\n" +
                "      \"ver\": \"1.0\"\n" +
                "    },\n" +
                "    \"granularity\": \"DAY\",\n" +
                "    \"date_range\": {\n" +
                "      \"from\": 1465368900608,\n" +
                "      \"to\": 1465368900608\n" +
                "    }\n" +
                "  },\n" +
                "  \"dimensions\": {\n" +
                "    \"did\": \"f0994e5336511a802bb948ee108ce9a4d6089301\"\n" +
                "  },\n" +
                "  \"edata\": {\n" +
                "    \"eks\": {\n" +
                "      \"timeSpent\": 0.0,\n" +
                "      \"time_stamp\": 1465368900608,\n" +
                "      \"content\": [],\n" +
                "      \"contentCount\": 0\n" +
                "    }\n" +
                "  },\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"genie\": []\n" +
                "    }\n" +
                "  ],\n" +
                "  \"@version\": \"1\",\n" +
                "  \"@timestamp\": \"2016-06-10T01:28:43.935Z\",\n" +
                "  \"type\": \"events\",\n" +
                "  \"learning\": \"true\",\n" +
                "  \"ts\": \"2016-06-10T07:02:13.000+0530\",\n" +
                "  \"metadata\": {\n" +
                "    \"ts_year\": \"2016\",\n" +
                "    \"ts_month\": \"06\",\n" +
                "    \"learning_index\": \"2016.06\",\n" +
                "    \"year\": \"2016\",\n" +
                "    \"month\": \"06\",\n" +
                "    \"monthday\": \"10\",\n" +
                "    \"hour\": \"01\",\n" +
                "    \"sync_timestamp\": \"2016-06-10T01:28:43+00:00\"\n" +
                "  }\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> VersionOneEvent() {
        String json = "{\n" +
                "  \"eid\": \"ME_GENIE_LAUNCH_SUMMARY\",\n" +
                "  \"ets\": 1465522333811,\n" +
                "  \"syncts\": 1465466359997,\n" +
                "  \"ver\": \"1.0\",\n" +
                "  \"mid\": \"0F5CE4150EA162407AD07F756CF40B88\",\n" +
                "  \"uid\": \"\",\n" +
                "  \"context\": {\n" +
                "    \"channel\": \"in.ekstep\",\n" +
                "    \"pdata\": {\n" +
                "      \"id\": \"AnalyticsDataPipeline\",\n" +
                "      \"model\": \"GenieUsageSummarizer\",\n" +
                "      \"ver\": \"1.0\"\n" +
                "    },\n" +
                "    \"granularity\": \"DAY\",\n" +
                "    \"date_range\": {\n" +
                "      \"from\": 1465368900608,\n" +
                "      \"to\": 1465368900608\n" +
                "    }\n" +
                "  },\n" +
                "  \"dimensions\": {\n" +
                "    \"did\": \"f0994e5336511a802bb948ee108ce9a4d6089301\"\n" +
                "  },\n" +
                "  \"edata\": {\n" +
                "    \"eks\": {\n" +
                "      \"timeSpent\": 0.0,\n" +
                "      \"time_stamp\": 1465368900608,\n" +
                "      \"content\": [],\n" +
                "      \"contentCount\": 0\n" +
                "    }\n" +
                "  },\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"genie\": []\n" +
                "    }\n" +
                "  ],\n" +
                "  \"@version\": \"1\",\n" +
                "  \"@timestamp\": \"2016-06-10T01:28:43.935Z\",\n" +
                "  \"type\": \"events\",\n" +
                "  \"learning\": \"true\",\n" +
                "  \"ts\": \"2016-06-10T07:02:13.000+0530\",\n" +
                "  \"metadata\": {\n" +
                "    \"ts_year\": \"2016\",\n" +
                "    \"ts_month\": \"06\",\n" +
                "    \"learning_index\": \"2016.06\",\n" +
                "    \"year\": \"2016\",\n" +
                "    \"month\": \"06\",\n" +
                "    \"monthday\": \"10\",\n" +
                "    \"hour\": \"01\",\n" +
                "    \"sync_timestamp\": \"2016-06-10T01:28:43+00:00\"\n" +
                "  }\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> AssessmentEvent() {
        String json = "{\n" +
                "    \"tags\": [\n" +
                "        {\n" +
                "            \"genie\": []\n" +
                "        }\n" +
                "    ],\n" +
                "    \"uid\": \"919280cb-3f51-46d7-ad7e-0ce4becb97a6\",\n" +
                "    \"sid\": \"1e891213-5ec6-475c-b706-d65928f47f77\",\n" +
                "    \"context\": {\n" +
                "       \"channel\": \"in.ekstep\"\n" +
                "    },\n" +
                "    \"ts\": \"2016-09-07T11:36:40.784+0530\",\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"exlength\": 0,\n" +
                "            \"length\": 19,\n" +
                "            \"params\": [],\n" +
                "            \"pass\": \"Yes\",\n" +
                "            \"qid\": \"esl.l2q24\",\n" +
                "            \"qindex\": 1,\n" +
                "            \"resvalues\": [\n" +
                "                {\n" +
                "                    \"1\": \"ball\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"score\": 1,\n" +
                "            \"uri\": \"\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"did\": \"b0ead84e4db348a5315a05e67137e3c518d7f5f5\",\n" +
                "    \"ver\": \"2.0\",\n" +
                "    \"type\": \"events\",\n" +
                "    \"eid\": \"ASSESS\",\n" +
                "    \"@version\": \"1\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"do_30076072\",\n" +
                "        \"ver\": \"1\"\n" +
                "    },\n" +
                "    \"@timestamp\": \"2016-09-07T06:08:00.037Z\",\n" +
                "    \"ets\": 1473228400784,\n" +
                "    \"uuid\": \"1173e3cd-b844-4b5c-8028-17fa0da3fab455\",\n" +
                "    \"mid\": \"9cb093ff-1e3d-4e2f-9b0a-2122a85ad4ca\",\n" +
                "    \"key\": \"919280cb-3f51-46d7-ad7e-0ce4becb97a6\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> OtherChannel() {
        String json = "{\n" +
                "    \"tags\": [\n" +
                "        {\n" +
                "            \"partnerid\": [\n" +
                "                \"org.ekstep.partner.pratham\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"genie\": []\n" +
                "        }\n" +
                "    ],\n" +
                "    \"uid\": \"\",\n" +
                "    \"sid\": \"\",\n" +
                "   \"context\": {\n" +
                "       \"channel\": \"ntp.in.ekstep\"\n" +
                "    },\n" +
                "    \"ts\": \"2016-05-27T15:21:59.995+0530\",\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"data\": \"L0ouPhQSzupyU5O/w87KZqZp5UQ+ay2fiDwJ5d9mkN8=\\n\",\n" +
                "            \"iv\": \"O3ZBNFXvA6HXE54g1H7TbA==\\n\",\n" +
                "            \"key\": \"LLNrwwlPZCtUYtDgF3UKph/OFISao5RvC3JQmzJDCItH17ZHoGsAioUzMry5u9VM50vTSBjW0W2M\\nkR6Nf9oJ35sO0PPZLs7kyWYTXKXACWi4QkaTAyTvY0j1Vrd1YXL5Fp9hjbOCyPyyVrvyioBKfFrl\\nGvWX8D6WNQsLGRcMIujkCK3oD7d3d2PLb3CKNFdaPPbxxofojtWZFI71uR9tYe7QDJk+x4WkJnNs\\nNdWjJLc3qIEaea9CNca/7+hdGLeeyleqpEqh/KmxTyefUWyIZyfkiho31vaN44jLGtXrwhJYc3cc\\n7PjNr8AncYZ1o9WzGkbnObQqSD76Lqsmt+YjPA==\\n\",\n" +
                "            \"partnerid\": \"org.ekstep.partner.pratham\",\n" +
                "            \"publickeyid\": \"[B@44cec40\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"did\": \"89feecbf900d1730c59b71c35f3dca344d2bd5e3\",\n" +
                "    \"ver\": \"2.0\",\n" +
                "    \"type\": \"events\",\n" +
                "    \"eid\": \"EXDATA\",\n" +
                "    \"@version\": \"1\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"genieservice.android\",\n" +
                "        \"ver\": \"4.2.localqa-debug\"\n" +
                "    },\n" +
                "    \"@timestamp\": \"2016-05-27T09:55:18.100Z\",\n" +
                "    \"ets\": 1464342719995,\n" +
                "    \"uuid\": \"558a1acf-4995-4af8-bdca-3ac95f9064b225\",\n" +
                "    \"mid\": \"249a9496-165e-44f4-bf55-d06855ab71b1\",\n" +
                "    \"key\": \"\",\n" +
                "    \"metadata\": {\n" +
                "        \"checksum\": \"30b91597f09d477c29349a9f1de1d2671c2bdbca\",\n" +
                "        \"last_processed_at\": \"2016-05-27T15:24:38.049+05:30\",\n" +
                "        \"processed_count\": 1,\n" +
                "        \"ts_year\": \"2016\",\n" +
                "        \"ts_month\": \"05\",\n" +
                "        \"year\": \"2016\",\n" +
                "        \"month\": \"05\",\n" +
                "        \"monthday\": \"27\",\n" +
                "        \"hour\": \"09\",\n" +
                "        \"sync_timestamp\": \"2016-05-27T09:54:39+00:00\"\n" +
                "    },\n" +
                "    \"flags\": {\n" +
                "        \"ldata_processed\": true,\n" +
                "        \"ldata_obtained\": false\n" +
                "    },\n" +
                "    \"ready_to_index\": \"true\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> DefaultChannel() {
        String json = "{\n" +
                "    \"tags\": [\n" +
                "        {\n" +
                "            \"partnerid\": [\n" +
                "                \"org.ekstep.partner.pratham\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"genie\": []\n" +
                "        }\n" +
                "    ],\n" +
                "    \"uid\": \"\",\n" +
                "    \"sid\": \"\",\n" +
                "   \"context\": {\n" +
                "       \"channel\": \"ekstep\"\n" +
                "    },\n" +
                "    \"ts\": \"2016-05-27T15:21:59.995+0530\",\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"data\": \"L0ouPhQSzupyU5O/w87KZqZp5UQ+ay2fiDwJ5d9mkN8=\\n\",\n" +
                "            \"iv\": \"O3ZBNFXvA6HXE54g1H7TbA==\\n\",\n" +
                "            \"key\": \"LLNrwwlPZCtUYtDgF3UKph/OFISao5RvC3JQmzJDCItH17ZHoGsAioUzMry5u9VM50vTSBjW0W2M\\nkR6Nf9oJ35sO0PPZLs7kyWYTXKXACWi4QkaTAyTvY0j1Vrd1YXL5Fp9hjbOCyPyyVrvyioBKfFrl\\nGvWX8D6WNQsLGRcMIujkCK3oD7d3d2PLb3CKNFdaPPbxxofojtWZFI71uR9tYe7QDJk+x4WkJnNs\\nNdWjJLc3qIEaea9CNca/7+hdGLeeyleqpEqh/KmxTyefUWyIZyfkiho31vaN44jLGtXrwhJYc3cc\\n7PjNr8AncYZ1o9WzGkbnObQqSD76Lqsmt+YjPA==\\n\",\n" +
                "            \"partnerid\": \"org.ekstep.partner.pratham\",\n" +
                "            \"publickeyid\": \"[B@44cec40\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"did\": \"89feecbf900d1730c59b71c35f3dca344d2bd5e3\",\n" +
                "    \"ver\": \"2.0\",\n" +
                "    \"type\": \"events\",\n" +
                "    \"eid\": \"ACCESS\",\n" +
                "    \"@version\": \"1\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"genieservice.android\",\n" +
                "        \"ver\": \"4.2.localqa-debug\"\n" +
                "    },\n" +
                "    \"@timestamp\": \"2016-05-27T09:55:18.100Z\",\n" +
                "    \"ets\": 1464342719995,\n" +
                "    \"uuid\": \"558a1acf-4995-4af8-bdca-3ac95f9064b225\",\n" +
                "    \"mid\": \"249a9496-165e-44f4-bf55-d06855ab71b1\",\n" +
                "    \"key\": \"\",\n" +
                "    \"metadata\": {\n" +
                "        \"checksum\": \"30b91597f09d477c29349a9f1de1d2671c2bdbca\",\n" +
                "        \"last_processed_at\": \"2016-05-27T15:24:38.049+05:30\",\n" +
                "        \"processed_count\": 1,\n" +
                "        \"ts_year\": \"2016\",\n" +
                "        \"ts_month\": \"05\",\n" +
                "        \"year\": \"2016\",\n" +
                "        \"month\": \"05\",\n" +
                "        \"monthday\": \"27\",\n" +
                "        \"hour\": \"09\",\n" +
                "        \"sync_timestamp\": \"2016-05-27T09:54:39+00:00\"\n" +
                "    },\n" +
                "    \"flags\": {\n" +
                "        \"ldata_processed\": true,\n" +
                "        \"ldata_obtained\": false\n" +
                "    },\n" +
                "    \"ready_to_index\": \"true\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}

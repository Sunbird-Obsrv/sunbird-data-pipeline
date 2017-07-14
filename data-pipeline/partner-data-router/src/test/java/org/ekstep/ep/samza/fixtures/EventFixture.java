package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
    public static Map<String, Object> CreateProfile() {
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
                "  \"uid\": \"68f24339-caae-4ebb-b4ff-6b1ae0478c02\",\n" +
                "  \"sid\": \"\",\n" +
                "  \"ts\": \"2016-05-25T15:03:54.857+0530\",\n" +
                "  \"edata\": {\n" +
                "    \"eks\": {\n" +
                "      \"age\": 5,\n" +
                "      \"day\": 25,\n" +
                "      \"gender\": \"female\",\n" +
                "      \"handle\": \"Geetha\",\n" +
                "      \"is_group_user\": false,\n" +
                "      \"language\": \"en\",\n" +
                "      \"loc\": \"\",\n" +
                "      \"month\": 5,\n" +
                "      \"standard\": -1,\n" +
                "      \"uid\": \"68f24339-caae-4ebb-b4ff-6b1ae0478c02\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"did\": \"79e957c03a7297d4e2aaab3ca218ab9af9e88403\",\n" +
                "  \"ver\": \"2.0\",\n" +
                "  \"type\": \"events\",\n" +
                "  \"eid\": \"GE_CREATE_PROFILE\",\n" +
                "  \"@version\": \"1\",\n" +
                "  \"gdata\": {\n" +
                "    \"id\": \"genieservice.android\",\n" +
                "    \"ver\": \"4.2.374sandboxxWalk\"\n" +
                "  },\n" +
                "  \"@timestamp\": \"2016-05-25T09:42:00.877Z\",\n" +
                "  \"ets\": 1464168834857,\n" +
                "  \"uuid\": \"d1c45492-3aae-4429-a0e3-453cb6f27b3a13\",\n" +
                "  \"mid\": \"987fe351-d77d-4f17-9609-ee6ec3f0ac5d\",\n" +
                "  \"key\": \"68f24339-caae-4ebb-b4ff-6b1ae0478c02\",\n" +
                "  \"metadata\": {\n" +
                "    \"checksum\": \"fb95ae1a8c4943c50a2dbf84cd9026ad4312f260\",\n" +
                "    \"last_processed_at\": \"2016-05-25T15:12:18.054+05:30\",\n" +
                "    \"processed_count\": 1,\n" +
                "    \"ts_year\": \"2016\",\n" +
                "    \"ts_month\": \"05\",\n" +
                "    \"year\": \"2016\",\n" +
                "    \"month\": \"05\",\n" +
                "    \"monthday\": \"25\",\n" +
                "    \"hour\": \"10\",\n" +
                "    \"sync_timestamp\": \"2016-05-25T10:17:59+00:00\"\n" +
                "  },\n" +
                "  \"flags\": {\n" +
                "    \"ldata_processed\": true,\n" +
                "    \"ldata_obtained\": false,\n" +
                "    \"child_data_processed\": true\n" +
                "  },\n" +
                "  \"udata\": {\n" +
                "    \"is_group_user\": false,\n" +
                "    \"handle\": \"Geetha\",\n" +
                "    \"standard\": 0,\n" +
                "    \"age_completed_years\": 5,\n" +
                "    \"gender\": \"female\"\n" +
                "  },\n" +
                "  \"ready_to_index\": \"true\"\n" +
                "}";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> EventWithoutPartnerId() {
        String json = "{\n" +
                "    \"tags\": [\n" +
                "        {\n" +
                "            \"genie\": []\n" +
                "        }\n" +
                "    ],\n" +
                "  \"uid\": \"68f24339-caae-4ebb-b4ff-6b1ae0478c02\",\n" +
                "  \"sid\": \"\",\n" +
                "  \"ts\": \"2016-05-25T15:03:54.857+0530\",\n" +
                "  \"edata\": {\n" +
                "    \"eks\": {\n" +
                "      \"age\": 5,\n" +
                "      \"day\": 25,\n" +
                "      \"gender\": \"female\",\n" +
                "      \"handle\": \"Geetha\",\n" +
                "      \"is_group_user\": false,\n" +
                "      \"language\": \"en\",\n" +
                "      \"loc\": \"\",\n" +
                "      \"month\": 5,\n" +
                "      \"standard\": -1,\n" +
                "      \"uid\": \"68f24339-caae-4ebb-b4ff-6b1ae0478c02\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"did\": \"79e957c03a7297d4e2aaab3ca218ab9af9e88403\",\n" +
                "  \"ver\": \"2.0\",\n" +
                "  \"type\": \"events\",\n" +
                "  \"eid\": \"GE_CREATE_PROFILE\",\n" +
                "  \"@version\": \"1\",\n" +
                "  \"gdata\": {\n" +
                "    \"id\": \"genieservice.android\",\n" +
                "    \"ver\": \"4.2.374sandboxxWalk\"\n" +
                "  },\n" +
                "  \"@timestamp\": \"2016-05-25T09:42:00.877Z\",\n" +
                "  \"ets\": 1464168834857,\n" +
                "  \"uuid\": \"d1c45492-3aae-4429-a0e3-453cb6f27b3a13\",\n" +
                "  \"mid\": \"987fe351-d77d-4f17-9609-ee6ec3f0ac5d\",\n" +
                "  \"key\": \"68f24339-caae-4ebb-b4ff-6b1ae0478c02\",\n" +
                "  \"metadata\": {\n" +
                "    \"checksum\": \"fb95ae1a8c4943c50a2dbf84cd9026ad4312f260\",\n" +
                "    \"last_processed_at\": \"2016-05-25T15:12:18.054+05:30\",\n" +
                "    \"processed_count\": 1,\n" +
                "    \"ts_year\": \"2016\",\n" +
                "    \"ts_month\": \"05\",\n" +
                "    \"year\": \"2016\",\n" +
                "    \"month\": \"05\",\n" +
                "    \"monthday\": \"25\",\n" +
                "    \"hour\": \"10\",\n" +
                "    \"sync_timestamp\": \"2016-05-25T10:17:59+00:00\"\n" +
                "  },\n" +
                "  \"flags\": {\n" +
                "    \"ldata_processed\": true,\n" +
                "    \"ldata_obtained\": false,\n" +
                "    \"child_data_processed\": true\n" +
                "  },\n" +
                "  \"udata\": {\n" +
                "    \"is_group_user\": false,\n" +
                "    \"handle\": \"Geetha\",\n" +
                "    \"standard\": 0,\n" +
                "    \"age_completed_years\": 5,\n" +
                "    \"gender\": \"female\"\n" +
                "  },\n" +
                "  \"ready_to_index\": \"true\"\n" +
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
                "    \"sid\": \"e2e50098-ed9e-41cf-bfb1-e61baaff8911\",\n" +
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
                "    \"eid\": \"GE_PARTNER_DATA\",\n" +
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

    public static Map<String, Object> PartnerDataV2() {
        String json = "{\n" +
                "    \"etags\": {\n" +
                "            \"partner\": [\n" +
                "                \"org.ekstep.partner.pratham\"\n" +
                "            ]\n" +
                "    },\n" +
                "    \"uid\": \"\",\n" +
                "    \"sid\": \"e2e50098-ed9e-41cf-bfb1-e61baaff8911\",\n" +
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
                "    \"eid\": \"GE_PARTNER_DATA\",\n" +
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

    public static Map<String, Object> LearningEvent() {
        String json = "{\n" +
                "  \"eid\": \"ME_GENIE_LAUNCH_SUMMARY\",\n" +
                "  \"ets\": 1465522333811,\n" +
                "  \"syncts\": 1465466359997,\n" +
                "  \"ver\": \"2.0\",\n" +
                "  \"mid\": \"0F5CE4150EA162407AD07F756CF40B88\",\n" +
                "  \"uid\": \"\",\n" +
                "  \"context\": {\n" +
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

    public static Map<String, Object> CPEvent() {
        String json = "{\n" +
                "    \"eid\": \"CP_INTERACT\",\n" +
                "    \"ets\": 1500011156661,\n" +
                "    \"ver\": \"2.0\",\n" +
                "    \"pdata\": {\n" +
                "      \"id\": \"in.ekstep.qa\",\n" +
                "      \"pid\": \"\",\n" +
                "      \"ver\": \"124\"\n" +
                "    },\n" +
                "    \"cdata\": [\n" +
                "      {\n" +
                "        \"type\": \"\",\n" +
                "        \"id\": \"\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"uid\": \"718\",\n" +
                "    \"rid\": \"\",\n" +
                "    \"context\": {\n" +
                "      \"sid\": \"1vbnii58kjr7i17abisplvpgj0\",\n" +
                "      \"content_id\": \"do_2122880790441738241317\"\n" +
                "    },\n" +
                "    \"tags\": [\n" +
                "      \"piwik_json\"\n" +
                "    ],\n" +
                "    \"edata\": {\n" +
                "      \"eks\": {\n" +
                "        \"env\": \"content\",\n" +
                "        \"context\": \"do_2122880790441738241317\",\n" +
                "        \"type\": \"click\",\n" +
                "        \"target\": \"\",\n" +
                "        \"targetid\": \"t3-content\",\n" +
                "        \"subtype\": \"\",\n" +
                "        \"values\": []\n" +
                "      }\n" +
                "    },\n" +
                "    \"mid\": \"CP:dd65263e6959b3e5319c031023d73dc1\",\n" +
                "    \"@version\": \"1\",\n" +
                "    \"@timestamp\": \"2017-07-14T05:50:03.115Z\",\n" +
                "    \"path\": \"/var/log/piwik/piwik.raw.idsite_2.log\",\n" +
                "    \"host\": \"ip-10-42-6-49\",\n" +
                "    \"piwik_pipeline\": \"qa\",\n" +
                "    \"channel\": \"in.ekstep\",\n" +
                "    \"flags\": {\n" +
                "      \"dd_processed\": true,\n" +
                "      \"ppm_skipped\": true,\n" +
                "      \"ldata_obtained\": false,\n" +
                "      \"ldata_processed\": true,\n" +
                "      \"od_processed\": true\n" +
                "    },\n" +
                "    \"type\": \"events\",\n" +
                "    \"ts\": \"2017-07-14T05:45:56.661+0000\",\n" +
                "    \"metadata\": {\n" +
                "      \"checksum\": \"CP:dd65263e6959b3e5319c031023d73dc1\",\n" +
                "      \"od_last_processed_at\": \"2017-07-14T05:50:34.194Z\",\n" +
                "      \"od_processed_count\": 1,\n" +
                "      \"ts_year\": \"2017\",\n" +
                "      \"ts_month\": \"07\",\n" +
                "      \"year\": \"2017\",\n" +
                "      \"month\": \"07\",\n" +
                "      \"monthday\": \"14\",\n" +
                "      \"hour\": \"05\",\n" +
                "      \"sync_timestamp\": \"2017-07-14T05:43:16+00:00\"\n" +
                "    },\n" +
                "    \"portaluserdata\": {\n" +
                "      \"code\": \"\",\n" +
                "      \"access\": [\n" +
                "        {\n" +
                "          \"id\": \"2\",\n" +
                "          \"value\": \"Registered\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"8\",\n" +
                "          \"value\": \"Super Users\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"13\",\n" +
                "          \"value\": \"Quiz-items-creator\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"14\",\n" +
                "          \"value\": \"Content-creator\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"22\",\n" +
                "          \"value\": \"Partner-admin\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"subtype\": \"\",\n" +
                "      \"partners\": [\n" +
                "        {\n" +
                "          \"id\": \"org.ekstep.test.telemetry\",\n" +
                "          \"value\": \"Telemetry Partner\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"profile\": [],\n" +
                "      \"name\": \"Aprajita Kumari\",\n" +
                "      \"id\": \"718\",\n" +
                "      \"type\": \"User\",\n" +
                "      \"parenttype\": \"\",\n" +
                "      \"parentid\": \"\",\n" +
                "      \"email\": \"aprajita.kumari@tarento.com\"\n" +
                "    },\n" +
                "    \"backend\": \"true\",\n" +
                "    \"ready_to_index\": \"true\"\n" +
                "  }";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> CEEvent() {
        String json = "{\n" +
                "    \"eid\": \"CE_API_CALL\",\n" +
                "    \"mid\": \"CE:e9070b719cfae3f374d6ed3eb0dccea3\",\n" +
                "    \"ets\": 1500009889567,\n" +
                "    \"channel\": \"\",\n" +
                "    \"ver\": \"1.0\",\n" +
                "    \"pdata\": {},\n" +
                "    \"cdata\": [],\n" +
                "    \"uid\": \"216\",\n" +
                "    \"context\": {\n" +
                "      \"sid\": \"r2gh5892n2n20t5dsc0itgvfc3\",\n" +
                "      \"content_id\": \"do_2122564752717742081127\"\n" +
                "    },\n" +
                "    \"rid\": \"\",\n" +
                "    \"edata\": {\n" +
                "      \"eks\": {\n" +
                "        \"path\": \"/action/composite/v3/search\",\n" +
                "        \"method\": \"POST\",\n" +
                "        \"request\": \"{\\\"request\\\":{\\\"filters\\\":{\\\"objectType\\\":[\\\"Dimension\\\",\\\"Domain\\\"]}}}\",\n" +
                "        \"response\": \"\",\n" +
                "        \"responseTime\": 257,\n" +
                "        \"status\": \"OK\",\n" +
                "        \"uip\": \"\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"etags\": {\n" +
                "      \"app\": [],\n" +
                "      \"partner\": [],\n" +
                "      \"dims\": []\n" +
                "    },\n" +
                "    \"@version\": \"1\",\n" +
                "    \"@timestamp\": \"2017-07-14T05:30:03.790Z\",\n" +
                "    \"path\": \"/var/log/piwik/piwik.raw.idsite_2.log\",\n" +
                "    \"host\": \"ip-10-42-6-49\",\n" +
                "    \"piwik_pipeline\": \"qa\",\n" +
                "    \"tags\": [\n" +
                "      \"piwik_json\"\n" +
                "    ],\n" +
                "    \"flags\": {\n" +
                "      \"dd_processed\": true,\n" +
                "      \"ppm_skipped\": true,\n" +
                "      \"ldata_obtained\": false,\n" +
                "      \"ldata_processed\": true,\n" +
                "      \"od_retry\": true\n" +
                "    },\n" +
                "    \"type\": \"events\",\n" +
                "    \"ts\": \"2017-07-14T05:24:49.567+0000\",\n" +
                "    \"metadata\": {\n" +
                "      \"checksum\": \"CE:e9070b719cfae3f374d6ed3eb0dccea3\",\n" +
                "      \"cachehit\": true,\n" +
                "      \"od_last_skipped_at\": \"2017-07-14T05:49:10.136Z\",\n" +
                "      \"od_err\": \"BAD_REQUEST\",\n" +
                "      \"od_errmsg\": \"CHANNEL IS MANDATORY\",\n" +
                "      \"od_last_processed_at\": \"2017-07-14T05:50:10.272Z\",\n" +
                "      \"od_processed_count\": 1,\n" +
                "      \"ts_year\": \"2017\",\n" +
                "      \"ts_month\": \"07\",\n" +
                "      \"year\": \"2017\",\n" +
                "      \"month\": \"07\",\n" +
                "      \"monthday\": \"14\",\n" +
                "      \"hour\": \"05\",\n" +
                "      \"sync_timestamp\": \"2017-07-14T05:42:52+00:00\"\n" +
                "    },\n" +
                "    \"contentdata\": {\n" +
                "      \"code\": \"org.ekstep.literacy.story.6542\",\n" +
                "      \"keywords\": null,\n" +
                "      \"subject\": \"\",\n" +
                "      \"methods\": null,\n" +
                "      \"rating\": null,\n" +
                "      \"description\": \"Write a short description of your lesson\",\n" +
                "      \"language\": [\n" +
                "        \"English\"\n" +
                "      ],\n" +
                "      \"medium\": \"\",\n" +
                "      \"source\": \"\",\n" +
                "      \"duration\": null,\n" +
                "      \"gradeLevel\": null,\n" +
                "      \"downloads\": null,\n" +
                "      \"lastUpdatedOn\": \"2017-05-30T13:44:42.015+0000\",\n" +
                "      \"contentType\": \"Story\",\n" +
                "      \"identifier\": \"do_2122564752717742081127\",\n" +
                "      \"audience\": [\n" +
                "        \"Learner\"\n" +
                "      ],\n" +
                "      \"createdFor\": null,\n" +
                "      \"author\": null,\n" +
                "      \"mediaType\": \"content\",\n" +
                "      \"curriculum\": null,\n" +
                "      \"ageGroup\": null,\n" +
                "      \"pkgVersion\": null,\n" +
                "      \"size\": null,\n" +
                "      \"concepts\": [],\n" +
                "      \"createdBy\": \"216\",\n" +
                "      \"domain\": [\n" +
                "        \"literacy\"\n" +
                "      ],\n" +
                "      \"name\": \"Untitled lesson\",\n" +
                "      \"status\": \"Draft\"\n" +
                "    },\n" +
                "    \"backend\": \"true\",\n" +
                "    \"ready_to_index\": \"true\"\n" +
                "  }";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> BEEvent() {
        String json = "{\n" +
                "    \"eid\": \"BE_OBJECT_LIFECYCLE\",\n" +
                "    \"ets\": 1499859855829,\n" +
                "    \"ver\": \"2.0\",\n" +
                "    \"pdata\": {\n" +
                "      \"id\": \"in.ekstep.qa\",\n" +
                "      \"pid\": \"\",\n" +
                "      \"ver\": \"124\"\n" +
                "    },\n" +
                "    \"cdata\": [\n" +
                "      {\n" +
                "        \"type\": \"\",\n" +
                "        \"id\": \"\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"uid\": \"424\",\n" +
                "    \"rid\": \"\",\n" +
                "    \"context\": {\n" +
                "      \"sid\": \"b0h1r7ru099mvfsn8aqrrgls01\",\n" +
                "      \"content_id\": \"\"\n" +
                "    },\n" +
                "    \"edata\": {\n" +
                "      \"eks\": {\n" +
                "        \"type\": \"User\",\n" +
                "        \"subtype\": \"\",\n" +
                "        \"id\": \"424\",\n" +
                "        \"parentid\": \"\",\n" +
                "        \"code\": \"\",\n" +
                "        \"name\": \"Siddharth Kabra\",\n" +
                "        \"state\": \"Update\",\n" +
                "        \"prevstate\": \"\",\n" +
                "        \"parenttype\": \"\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"mid\": \"CP:9e61fd61a251b6e8724e5f1f2042ecba\",\n" +
                "    \"@version\": \"1\",\n" +
                "    \"@timestamp\": \"2017-07-12T11:45:08.332Z\",\n" +
                "    \"path\": \"/var/log/piwik/piwik.raw.idsite_2.log\",\n" +
                "    \"host\": \"ip-10-42-6-49\",\n" +
                "    \"piwik_pipeline\": \"qa\",\n" +
                "    \"tags\": [\n" +
                "      \"piwik_json\"\n" +
                "    ],\n" +
                "    \"channelid\": \"in.ekstep\",\n" +
                "    \"flags\": {\n" +
                "      \"dd_processed\": true,\n" +
                "      \"olm_processed\": true,\n" +
                "      \"ppm_skipped\": true,\n" +
                "      \"ldata_obtained\": false,\n" +
                "      \"ldata_processed\": true,\n" +
                "      \"od_skipped\": true\n" +
                "    },\n" +
                "    \"type\": \"events\",\n" +
                "    \"ts\": \"2017-07-12T11:44:15.829+0000\",\n" +
                "    \"metadata\": {\n" +
                "      \"checksum\": \"CP:9e61fd61a251b6e8724e5f1f2042ecba\",\n" +
                "      \"ts_year\": \"2017\",\n" +
                "      \"ts_month\": \"07\",\n" +
                "      \"year\": \"2017\",\n" +
                "      \"month\": \"07\",\n" +
                "      \"monthday\": \"12\",\n" +
                "      \"hour\": \"11\",\n" +
                "      \"sync_timestamp\": \"2017-07-12T11:38:24+00:00\"\n" +
                "    },\n" +
                "    \"backend\": \"true\",\n" +
                "    \"ready_to_index\": \"true\"\n" +
                "  }";

        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> VersionOneEvent() {
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
                "    \"sid\": \"e2e50098-ed9e-41cf-bfb1-e61baaff8911\",\n" +
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
                "    \"ver\": \"1.0\",\n" +
                "    \"type\": \"events\",\n" +
                "    \"eid\": \"GE_PARTNER_DATA\",\n" +
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

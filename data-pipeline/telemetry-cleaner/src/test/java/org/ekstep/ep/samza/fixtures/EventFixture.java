package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
    public static Map<String, Object> CreateProfile() {
        String json = "{\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"genie\": []\n" +
                "    }\n" +
                "  ],\n" +
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

    public static Map<String, Object> GenieStart() {
        String json = "{\n" +
                "    \"actor\": {\n" +
                "      \"id\": \"874ed8a5-782e-4f6c-8f36-e0288455901e\",\n" +
                "      \"type\": \"User\"\n" +
                "    },\n" +
                "    \"eid\": \"START\",\n" +
                "    \"edata\": {\n" +
                "      \"mode\": \"\",\n" +
                "      \"duration\": 0,\n" +
                "      \"loc\": \"12.936936936936936,77.6280860297418\",\n" +
                "      \"dspec\": {\n" +
                "        \"camera\": \"12.0,6.0\",\n" +
                "        \"cap\": [],\n" +
                "        \"cpu\": \"abi: arm64-v8a  AArch64 Processor rev 4 (aarch64) \",\n" +
                "        \"dlocname\": \"\",\n" +
                "        \"dname\": \"\",\n" +
                "        \"edisk\": 50,\n" +
                "        \"id\": \"d592ee283e322f6b\",\n" +
                "        \"idisk\": 50,\n" +
                "        \"make\": \"Xiaomi Mi A1\",\n" +
                "        \"mem\": -1,\n" +
                "        \"os\": \"Android 7.1.2\",\n" +
                "        \"scrn\": 5,\n" +
                "        \"sims\": -1\n" +
                "      },\n" +
                "      \"pageid\": \"\"\n" +
                "    },\n" +
                "    \"ver\": \"3.0\",\n" +
                "    \"metadata\": {\n" +
                "      \"checksum\": \"68462c44-33ce-45f6-8b0f-516746fcf011\",\n" +
                "      \"source_eid\": \"GE_START\",\n" +
                "      \"source_mid\": \"68462c44-33ce-45f6-8b0f-516746fcf011\",\n" +
                "      \"index_name\": \"telemetry-2017.12\",\n" +
                "      \"index_type\": \"events_v1\"\n" +
                "    },\n" +
                "    \"@timestamp\": \"2017-12-06T10:57:56.792Z\",\n" +
                "    \"ets\": 1512557871074,\n" +
                "    \"context\": {\n" +
                "      \"channel\": \"in.ekstep\",\n" +
                "      \"env\": \"Genie\",\n" +
                "      \"sid\": \"5bbdf35e-c907-4f98-990e-0f15d92b3e1d\",\n" +
                "      \"did\": \"bac4ff75910c96e33d93396b73c4585cdfa4fda5\",\n" +
                "      \"pdata\": {\n" +
                "        \"id\": \"sunbird\",\n" +
                "        \"ver\": \"1.0.localdev-debug\"\n" +
                "      },\n" +
                "      \"cdata\": []\n" +
                "    },\n" +
                "    \"flags\": {\n" +
                "      \"tv_processed\": true,\n" +
                "      \"v2_converted\": true,\n" +
                "      \"dd_processed\": true,\n" +
                "      \"ldata_obtained\": false,\n" +
                "      \"ldata_processed\": true\n" +
                "    },\n" +
                "    \"mid\": \"START:68462c44-33ce-45f6-8b0f-516746fcf011\",\n" +
                "    \"object\": {\n" +
                "      \"id\": \"org.sunbird.app\",\n" +
                "      \"type\": \"Content\",\n" +
                "      \"ver\": \"1.0.localdev-debug\",\n" +
                "      \"subtype\": \"\",\n" +
                "      \"parent\": {\n" +
                "        \"id\": \"\",\n" +
                "        \"type\": \"\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"tags\": [],\n" +
                "    \"type\": \"events\",\n" +
                "    \"ts\": \"2017-12-06T10:57:51.074+0000\",\n" +
                "    \"ldata\": {\n" +
                "      \"country\": \"India\",\n" +
                "      \"district\": \"Chennai\",\n" +
                "      \"locality\": \"Chennai\",\n" +
                "      \"state\": \"Tamil Nadu\"\n" +
                "    }\n" +
                "  }";

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
                "  \"ver\": \"1.0\",\n" +
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
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"genie\": []\n" +
                "    }\n" +
                "  ],\n" +
                "  \"key\": \"68f24339-caae-4ebb-b4ff-6b1ae0478c02\",\n" +
                "  \"@version\": \"1\",\n" +
                "  \"@timestamp\": \"2016-06-10T01:28:43.935Z\",\n" +
                "  \"type\": \"events\",\n" +
                "  \"learning\": \"true\",\n" +
                "  \"ts\": \"2016-06-10T07:02:13.000+0530\",\n" +
                "  \"flags\": {\n" +
                "    \"tv_processed\": \"true\",\n" +
                "    \"tc_processed\": \"06\"\n" +
                "  },\n" +
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
}

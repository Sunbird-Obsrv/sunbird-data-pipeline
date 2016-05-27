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
                "    \"tags\": [\n" +
                "      {\n" +
                "        \"genie\": []\n" +
                "      }\n" +
                "    ],\n" +
                "    \"uid\": \"\",\n" +
                "    \"sid\": \"\",\n" +
                "    \"ts\": \"2016-05-25T19:03:33.856+0530\",\n" +
                "    \"edata\": {\n" +
                "      \"eks\": {\n" +
                "        \"dspec\": {\n" +
                "          \"camera\": \"6.0,2.0\",\n" +
                "          \"cap\": [],\n" +
                "          \"cpu\": \"abi: armeabi-v7a  ARMv7 Processor rev 3 (v7l) \",\n" +
                "          \"dlocname\": \"\",\n" +
                "          \"dname\": \"\",\n" +
                "          \"edisk\": 5.52,\n" +
                "          \"id\": \"49f257354ef26714\",\n" +
                "          \"idisk\": 0,\n" +
                "          \"make\": \"Motorola XT1033\",\n" +
                "          \"mem\": -1,\n" +
                "          \"os\": \"Android 5.1\",\n" +
                "          \"scrn\": 4.49,\n" +
                "          \"sims\": -1\n" +
                "        },\n" +
                "        \"loc\": \"12.9311397,77.6236127\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"did\": \"cf979a7ba0162ddb0ac0702e397d8b689b61712b\",\n" +
                "    \"ver\": \"2.0\",\n" +
                "    \"type\": \"events\",\n" +
                "    \"eid\": \"GE_GENIE_START\",\n" +
                "    \"@version\": \"1\",\n" +
                "    \"gdata\": {\n" +
                "      \"id\": \"genie.android\",\n" +
                "      \"ver\": \"4.2.localsandbox-debug.6\"\n" +
                "    },\n" +
                "    \"@timestamp\": \"2016-05-25T13:31:05.869Z\",\n" +
                "    \"ets\": 1464183213856,\n" +
                "    \"uuid\": \"2ab01daa-9134-471c-8abb-e676765020b425\",\n" +
                "    \"mid\": \"97d882b6-4a65-4f50-b288-c46d700ee615\",\n" +
                "    \"key\": \"\",\n" +
                "    \"metadata\": {\n" +
                "      \"checksum\": \"f079e3f4448a20d9afcb0fa93449daba584ac65b\",\n" +
                "      \"last_processed_at\": \"2016-05-26T04:18:26.601+05:30\",\n" +
                "      \"processed_count\": 1,\n" +
                "      \"ts_year\": \"2016\",\n" +
                "      \"ts_month\": \"05\",\n" +
                "      \"year\": \"2016\",\n" +
                "      \"month\": \"05\",\n" +
                "      \"monthday\": \"25\",\n" +
                "      \"hour\": \"22\",\n" +
                "      \"sync_timestamp\": \"2016-05-25T22:44:55+00:00\"\n" +
                "    },\n" +
                "    \"ldata\": {\n" +
                "      \"state\": \"Karnataka\",\n" +
                "      \"locality\": \"Bengaluru\",\n" +
                "      \"district\": \"Bangalore Urban\",\n" +
                "      \"country\": \"India\"\n" +
                "    },\n" +
                "    \"flags\": {\n" +
                "      \"ldata_processed\": true,\n" +
                "      \"ldata_obtained\": true\n" +
                "    },\n" +
                "    \"ready_to_index\": \"true\"\n" +
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
}

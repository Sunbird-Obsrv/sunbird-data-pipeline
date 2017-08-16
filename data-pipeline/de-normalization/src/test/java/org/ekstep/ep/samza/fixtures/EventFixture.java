package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
    private static final String JSON = "{\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"loc\": \"\",\n" +
            "            \"uid\": \"6\",\n" +
            "            \"age\": 5,\n" +
            "            \"handle\": \"Jijesh\",\n" +
            "            \"standard\": -1,\n" +
            "            \"language\": \"ML\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"did\": \"cbeda6a2ef327eaee21008de6495f89476aba58d\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genieservice.android\",\n" +
            "        \"ver\": \"1.0.local-qa-debug\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"tags\": [],\n" +
            "    \"ts\": \"2015-09-27T13:03:43-04:00\",\n" +
            "    \"uid\": \"6\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"metadata\": {\n" +
            "        \"checksum\": \"22e1430f2e5f339230dbf9595b060008\"\n" +
            "    }\n" +
            "}";

    private static final String ANOTHER_JSON = "{\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"loc\": \"\",\n" +
            "            \"uid\": \"7\",\n" +
            "            \"age\": 6,\n" +
            "            \"handle\": \"Abhi\",\n" +
            "            \"standard\": -1,\n" +
            "            \"language\": \"ML\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"did\": \"cbeda6a2ef327eaee21008de6495f89476aba58d\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genieservice.android\",\n" +
            "        \"ver\": \"1.0.local-qa-debug\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"tags\": [],\n" +
            "    \"ts\": \"2015-09-27T13:03:43-04:00\",\n" +
            "    \"uid\": \"6\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"metadata\": {\n" +
            "        \"checksum\": \"22e1430f2e5f339230dbf9595b060078\"\n" +
            "    }\n" +
            "}";

    private static final String BE_EVENT_JSON = "{\n" +
            "  \"@timestamp\": \"2017-04-11T11:42:32.917Z\",\n" +
            "  \"@version\": \"1\",\n" +
            "  \"edata\": {\n" +
            "    \"eks\": {\n" +
            "      \"cid\": \"do_10096841\",\n" +
            "      \"concepts\": null,\n" +
            "      \"contentType\": \"Plugin\",\n" +
            "      \"downloadUrl\": \"https://ekstep-public-qa.s3-ap-south-1.amazonaws.com/\",\n" +
            "      \"flags\": null,\n" +
            "      \"mediaType\": \"content\",\n" +
            "      \"pkgVersion\": 8,\n" +
            "      \"prevState\": \"Processing\",\n" +
            "      \"size\": 11652,\n" +
            "      \"state\": \"Live\"\n" +
            "    }\n" +
            "  },\n" +
            "    \"uid\": \"6\",\n" +
            "  \"eid\": \"BE_CONTENT_LIFECYCLE\",\n" +
            "  \"ets\": 1491911140141,\n" +
            "  \"pdata\": {\n" +
            "    \"id\": \"org.ekstep.content.platform\",\n" +
            "    \"pid\": \"\",\n" +
            "    \"ver\": \"1.0\"\n" +
            "  },\n" +
            "  \"ts\": \"2017-04-11T11:45:40.000+0000\",\n" +
            "  \"type\": \"backend.events\",\n" +
            "  \"ver\": \"2.0\"\n" +
            "}";

    private static final String ME_EVENT_JSON =
            "{\n" +
                    "  \"@timestamp\": \"2017-06-12T04:31:58.237Z\",\n" +
                    "  \"@version\": \"1\",\n" +
                    "  \"edata\": {\n" +
                    "    \"eks\": {\n" +
                    "      \"api_calls_count\": 3,\n" +
                    "      \"end_time\": 1497188030389,\n" +
                    "      \"events_summary\": [],\n" +
                    "      \"interact_events_count\": 4,\n" +
                    "      \"interact_events_per_min\": 3.86,\n" +
                    "      \"load_time\": 7,\n" +
                    "      \"menu_events_count\": 2,\n" +
                    "      \"plugin_summary\": {},\n" +
                    "      \"save_summary\": {\n" +
                    "        \"failed_count\": 0,\n" +
                    "        \"success_count\": 0,\n" +
                    "        \"total_count\": 0\n" +
                    "      },\n" +
                    "      \"sidebar_events_count\": 0,\n" +
                    "      \"stage_summary\": {\n" +
                    "        \"added_count\": 0,\n" +
                    "        \"deleted_count\": 0,\n" +
                    "        \"modified_count\": 0\n" +
                    "      },\n" +
                    "      \"start_time\": 1497187968172,\n" +
                    "      \"time_diff\": 62.22,\n" +
                    "      \"time_spent\": 62.22\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"eid\": \"ME_CE_SESSION_SUMMARY\",\n" +
                    "  \"ets\": 1497229931026,\n" +
                    "  \"learning\": \"true\",\n" +
                    "  \"mid\": \"88802F3D414C65F857DD6B3A5C580FF6\",\n" +
                    "  \"syncts\": 1497188030389,\n" +
                    "  \"ts\": \"2017-06-12T01:12:11.000+0000\",\n" +
                    "  \"type\": \"events\",\n" +
                    "  \"uid\": \"213\",\n" +
                    "  \"ver\": \"1.0\"\n" +
                    "}";

    private static final String OTHER_CHANNEL_EVENT_JSON = "{\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"loc\": \"\",\n" +
            "            \"uid\": \"6\",\n" +
            "            \"age\": 5,\n" +
            "            \"handle\": \"Jijesh\",\n" +
            "            \"standard\": -1,\n" +
            "            \"language\": \"ML\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"channel\": \"other.channel\",\n" +
            "    \"did\": \"cbeda6a2ef327eaee21008de6495f89476aba58d\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genieservice.android\",\n" +
            "        \"ver\": \"1.0.local-qa-debug\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"tags\": [],\n" +
            "    \"ts\": \"2015-09-27T13:03:43-04:00\",\n" +
            "    \"uid\": \"6\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"metadata\": {\n" +
            "        \"checksum\": \"22e1430f2e5f339230dbf9595b060008\"\n" +
            "    }\n" +
            "}";

    public static Map<String, Object> GeCreateProfile() {
        return new Gson().fromJson(JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> AnotherGeCreateProfile() {
        return new Gson().fromJson(ANOTHER_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> BeEvent() {
        return new Gson().fromJson(BE_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> MeEvent() {
        return new Gson().fromJson(ME_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> OtherChannelEvent() {
        return new Gson().fromJson(OTHER_CHANNEL_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}

package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {
    private static final String EVENT_WITH_CHECKSUM = "{\n" +
            "  \"actor\": {\n" +
            "    \"type\": \"User\",\n" +
            "    \"id\": \"9a78dacddfcdfae7a9679b143e348158\"\n" +
            "  },\n" +
            "  \"eid\": \"INTERACT\",\n" +
            "  \"edata\": {\n" +
            "    \"id\": \"do_312764599773110272147\",\n" +
            "    \"type\": \"TOUCH\",\n" +
            "    \"pageid\": \"ContentApp-EndScreen\",\n" +
            "    \"subtype\": \"ContentID\"\n" +
            "  },\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"ets\": 1561620619274,\n" +
            "  \"context\": {\n" +
            "    \"pdata\": {\n" +
            "      \"ver\": \"1.14.5\",\n" +
            "      \"pid\": \"sunbird-portal.contentplayer\",\n" +
            "      \"id\": \"dev.diksha.portal\"\n" +
            "    },\n" +
            "    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "    \"env\": \"contentplayer\",\n" +
            "    \"did\": \"9a78dacddfcdfae7a9679b143e348158\",\n" +
            "    \"sid\": \"8c416b30-4e47-a695-b1d2-9cd030eadeff\",\n" +
            "    \"cdata\": [\n" +
            "      {\n" +
            "        \"type\": \"DialCode\",\n" +
            "        \"id\": \"DNILBR\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"ContentSession\",\n" +
            "        \"id\": \"88de9e1dfa6f237c076278a02cc4b305\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"rollup\": {\n" +
            "      \"l1\": \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"mid\": \"INTERACT:fd63ff6bd0eedf05cf57c0058588ae69\",\n" +
            "  \"object\": {\n" +
            "    \"ver\": \"1\",\n" +
            "    \"id\": \"do_312764599773110272147\",\n" +
            "    \"type\": \"Content\",\n" +
            "    \"rollup\": {\n" +
            "      \"l1\": \"do_31276594374681395211909\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"tags\": [],\n" +
            "  \"syncts\": 1561620619795,\n" +
            "  \"@timestamp\": \"2019-06-27T07:30:19.795Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";

    private static final String EVENT_WITH_MID = "{\n" +
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
            "    \"mid\": \"22e1430f2e5f339230dbf9595b060008\"" +
            "}";

    private static final String EVENT_WITHOUT_CHECKSUM = "{\n" +
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
            "    \"ver\": \"1.0\"" +
            "}";

    private static final String EVENT_WITH_EMTPY_CHANNEL = "{\n" +
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
            "    \"context\": {\n" +
            "    \"channel\": \"\"\n" +
            "    },\n" +
            "    \"tags\": [],\n" +
            "    \"ts\": \"2015-09-27T13:03:43-04:00\",\n" +
            "    \"uid\": \"6\",\n" +
            "    \"ver\": \"1.0\"" +
            "}";

    public static Map<String, Object> EventWithChecksumMap() {
        return new Gson().fromJson(EVENT_WITH_CHECKSUM, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> EventWithMidMap() {
        return new Gson().fromJson(EVENT_WITH_MID, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> EventWithoutChecksumFieldMap() {
        return new Gson().fromJson(EVENT_WITHOUT_CHECKSUM, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> EventWithEmptyChannel() {
        return new Gson().fromJson(EVENT_WITH_EMTPY_CHANNEL, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static String EventWithChecksumJson() {
        return EVENT_WITH_CHECKSUM;
    }

}


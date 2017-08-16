package org.ekstep.ep.samza.fixture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {

    public static final String OTHER_EVENT =
            "{\n" +
                    "    \"eid\": \"ME_ITEM_USAGE_SUMMARY\",\n" +
                    "    \"channel\": \"in.ekstep.test\",\n" +
                    "    \"dimensions\": {\n" +
                    "      \"tag\": \"6c3791818e80b9d05fb975da1e972431d9f8c2a6\",\n" +
                    "      \"period\": 20170424,\n" +
                    "      \"content_id\": \"domain_4501\"\n" +
                    "    }\n" +
                    "  }";

    public static final String OTHER_CHANNEL_EVENT =
            "{\n" +
                    "    \"eid\": \"ME_ITEM_USAGE_SUMMARY\",\n" +
                    "    \"channel\": \"in.other.channel\",\n" +
                    "    \"dimensions\": {\n" +
                    "      \"tag\": \"6c3791818e80b9d05fb975da1e972431d9f8c2a6\",\n" +
                    "      \"period\": 20170424,\n" +
                    "      \"content_id\": \"domain_4501\"\n" +
                    "    }\n" +
                    "  }";

    public static final String LIFECYCLE_EVENT_JSON =
            "{\n" +
                    "    \"tags\": [],\n" +
                    "    \"uid\": \"725\",\n" +
                    "    \"cdata\": [],\n" +
                    "    \"host\": \"ip-10-42-6-49\",\n" +
                    "    \"channel\": \"in.ekstep.test\",\n" +
                    "    \"edata\": {\n" +
                    "        \"eks\": {\n" +
                    "            \"type\": \"User\",\n" +
                    "            \"subtype\": \"\",\n" +
                    "            \"id\": \"725\",\n" +
                    "            \"parentid\": \"\",\n" +
                    "            \"code\": \"\",\n" +
                    "            \"name\": \"Amit\",\n" +
                    "            \"state\": \"Create\",\n" +
                    "            \"prevstate\": \"\",\n" +
                    "            \"parenttype\": \"\"\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"ver\": \"2.0\",\n" +
                    "    \"type\": \"events\",\n" +
                    "    \"eid\": \"BE_OBJECT_LIFECYCLE\"\n" +
                    "}";

    public static Map<String, Object> OtherEvent() {
        return new Gson().fromJson(OTHER_EVENT, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> LifecycleEvent() {
        return new Gson().fromJson(LIFECYCLE_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> OtherChannelEvent() {
        return new Gson().fromJson(OTHER_CHANNEL_EVENT, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}

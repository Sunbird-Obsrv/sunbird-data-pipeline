package org.ekstep.ep.samza.fixture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {

    public static final String SKIP_EVENT_JSON =
            "{\n" +
                    "    \"eid\": \"ME_ITEM_USAGE_SUMMARY\",\n" +
                    "    \"dimensions\": {\n" +
                    "      \"tag\": \"6c3791818e80b9d05fb975da1e972431d9f8c2a6\",\n" +
                    "      \"period\": 20170424,\n" +
                    "      \"content_id\": \"domain_4501\"\n" +
                    "    }\n" +
                    "  }";

    public static final String EVENT_JSON =
            "{\n" +
                    "    \"eid\": \"ME_ITEM_USAGE_SUMMARY\",\n" +
                    "    \"dimensions\": {\n" +
                    "      \"tag\": \"6c3791818e80b9d05fb975da1e972431d9f8c2a6\",\n" +
                    "      \"period\": 20170424,\n" +
                    "      \"content_id\": \"domain_4501\"\n" +
                    "    }\n" +
                    "  }";


    public static Map<String, Object> Event() {
        return new Gson().fromJson(EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> skipEvent() {
        return new Gson().fromJson(SKIP_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}

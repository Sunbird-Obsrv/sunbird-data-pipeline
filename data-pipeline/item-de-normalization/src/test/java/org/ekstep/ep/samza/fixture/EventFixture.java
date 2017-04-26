package org.ekstep.ep.samza.fixture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {

    public static final String JSON_WITHOUT_ITEM_ID =
            "{\n" +
                    "    \"eid\": \"ME_ITEM_USAGE_SUMMARY\",\n" +
                    "    \"dimensions\": {\n" +
                    "      \"tag\": \"6c3791818e80b9d05fb975da1e972431d9f8c2a6\",\n" +
                    "      \"period\": 20170424,\n" +
                    "      \"content_id\": \"domain_4501\"\n" +
                    "    }\n" +
                    "  }";

    private static final String ME_ITEM_USAGE_SUMMARY_JSON_STRING =
            "{\n" +
                    "    \"eid\": \"ME_ITEM_USAGE_SUMMARY\",\n" +
                    "    \"dimensions\": {\n" +
                    "      \"tag\": \"6c3791818e80b9d05fb975da1e972431d9f8c2a6\",\n" +
                    "      \"period\": 20170424,\n" +
                    "      \"content_id\": \"domain_3915\",\n" +
                    "      \"item_id\": \"domain_4502\"\n" +
                    "    }\n" +
                    "  }";

    private static final String ME_ITEM_SUMMARY_JSON_STRING =
            "{\n" +
                    "    \"eid\": \"ME_ITEM_SUMMARY\",\n" +
                    "    \"dimensions\": {\n" +
                    "      \"did\": \"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\",\n" +
                    "      \"gdata\": {\n" +
                    "        \"id\": \"domain_3915\",\n" +
                    "        \"ver\": \"5\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"edata\": {\n" +
                    "      \"eks\": {\n" +
                    "        \"itemId\": \"domain_4502\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }";

    private static final String OE_ASSESS_JSON_STRING =
            "{\n" +
                    "  \"@timestamp\": \"2017-04-25T10:31:08.837Z\",\n" +
                    "  \"@version\": \"1\",\n" +
                    "  \"did\": \"0b010eb9655e941b9637869e95b4036b9abbd9ed\",\n" +
                    "  \"edata\": {\n" +
                    "    \"eks\": {\n" +
                    "      \"exlength\": 0,\n" +
                    "      \"length\": 3,\n" +
                    "      \"params\": [],\n" +
                    "      \"pass\": \"Yes\",\n" +
                    "      \"qdesc\": \"\",\n" +
                    "      \"qid\": \"domain_4502\",\n" +
                    "      \"qindex\": 1,\n" +
                    "      \"resvalues\": [],\n" +
                    "      \"score\": 2,\n" +
                    "      \"uri\": \"\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"eid\": \"OE_ASSESS\",\n" +
                    "  \"ets\": 1493116423555\n" +
                    "}";

    public static Map<String, Object> EventWithoutItemId() {
        return new Gson().fromJson(JSON_WITHOUT_ITEM_ID, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> MeItemUsageSummaryEvent() {
        return new Gson().fromJson(ME_ITEM_USAGE_SUMMARY_JSON_STRING, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> MeItemSummaryEvent() {
        return new Gson().fromJson(ME_ITEM_SUMMARY_JSON_STRING, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> OeAssessEvent() {
        return new Gson().fromJson(OE_ASSESS_JSON_STRING, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}

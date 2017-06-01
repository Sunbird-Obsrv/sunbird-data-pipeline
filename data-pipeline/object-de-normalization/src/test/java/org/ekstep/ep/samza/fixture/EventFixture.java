package org.ekstep.ep.samza.fixture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {

    private static final String SKIP_EVENT_JSON =
            "{\n" +
                    "    \"eid\": \"ME_ITEM_USAGE_SUMMARY\",\n" +
                    "    \"dimensions\": {\n" +
                    "      \"tag\": \"6c3791818e80b9d05fb975da1e972431d9f8c2a6\",\n" +
                    "      \"period\": 20170424,\n" +
                    "      \"content_id\": \"domain_4501\"\n" +
                    "    }\n" +
                    "  }";

    private static final String CP_INTERACT_EVENT_JSON =
            "{\n" +
                    "    \"eid\": \"CP_INTERACT\",\n" +
                    "    \"uid\": \"111\"\n" +
                    "  }";

    private static final String DENORMALIZED_CP_INTERACT_EVENT_JSON =
            "{\n" +
                    "  \"eid\": \"CP_INTERACT\",\n" +
                    "  \"uid\": \"111\",\n" +
                    "  \"portaluserdata\": {\n" +
                    "    \"id\": \"111\",\n" +
                    "    \"type\": \"User\",\n" +
                    "    \"subtype\": \"Reviewer\",\n" +
                    "    \"parentid\": \"222\",\n" +
                    "    \"parenttype\": \"Admin\",\n" +
                    "    \"code\": \"XYZ\",\n" +
                    "    \"name\": \"User 111\",\n" +
                    "    \"email\": \"user@ekstep.in\",\n" +
                    "    \"access\": [\n" +
                    "    {\n" +
                    "      \"id\": \"2\",\n" +
                    "      \"value\": \"Registered\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"partners\": [],\n" +
                    "  \"profile\": []\n" +
                    "  }\n" +
                    "}";

    private static final String DENORMALIZED_CP_INTERACT_EVENT_JSON_WITHOUT_DETAILS =
            "{\n" +
                    "  \"eid\": \"CP_INTERACT\",\n" +
                    "  \"uid\": \"111\",\n" +
                    "  \"portaluserdata\": {\n" +
                    "    \"id\": \"111\",\n" +
                    "    \"type\": \"User\",\n" +
                    "    \"subtype\": \"Reviewer\",\n" +
                    "    \"parentid\": \"222\",\n" +
                    "    \"parenttype\": \"Admin\",\n" +
                    "    \"code\": \"XYZ\",\n" +
                    "    \"name\": \"User 111\"\n" +
                    "  }\n" +
                    "}";


    public static Map<String, Object> event() {
        return new Gson().fromJson(SKIP_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> cpInteractEvent() {
        return new Gson().fromJson(CP_INTERACT_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> denormalizedCpInteractEvent() {
        return new Gson().fromJson(DENORMALIZED_CP_INTERACT_EVENT_JSON, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> denormalizedCpInteractEventWithoutDetails() {
        return new Gson().fromJson(DENORMALIZED_CP_INTERACT_EVENT_JSON_WITHOUT_DETAILS, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}

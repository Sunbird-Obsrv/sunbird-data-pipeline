package org.ekstep.ep.samza.fixture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {

    public static Map<String, Object> cpUpdateProfileEvent() {
        return new Gson().fromJson("{\n" +
                "  \"eid\": \"CP_UPDATE_PROFILE\",\n" +
                "  \"uid\": \"111\",\n" +
                "  \"edata\": {\n" +
                "     \"eks\": {\n" +
                "        \"name\": \"Portal-User-10\",\n" +
                "        \"email\": \"portal.user.10@ekstep.in\", \n" +
                "        \"access\": [{\"id\": 11, \"value\": \"Content-Creator\"}],\n" +
                "        \"partners\": [{\"id\": \"org.ekstep.partners.pratham\", \"value\": \"Pratham\"}],\n" +
                "        \"profile\": [{\"id\": \"field_gender\", \"value\": \"Female\"}]\n" +
                "     }\n" +
                "  }\n" +
                "}", new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> skipEvent() {
        return new Gson().fromJson("{\n" +
                "    \"eid\": \"ME_ITEM_USAGE_SUMMARY\",\n" +
                "    \"dimensions\": {\n" +
                "      \"tag\": \"6c3791818e80b9d05fb975da1e972431d9f8c2a6\",\n" +
                "      \"period\": 20170424,\n" +
                "      \"content_id\": \"domain_4501\"\n" +
                "    }\n" +
                "  }", new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> cpUpdateProfileEventWithoutUid() {
        return new Gson().fromJson("{\n" +
                "  \"eid\": \"CP_UPDATE_PROFILE\",\n" +
                "  \"edata\": {\n" +
                "     \"eks\": {\n" +
                "        \"name\": \"Portal-User-10\",\n" +
                "        \"email\": \"portal.user.10@ekstep.in\", \n" +
                "        \"access\": [{\"id\": 11, \"value\": \"Content-Creator\"}],\n" +
                "        \"partners\": [{\"id\": \"org.ekstep.partners.pratham\", \"value\": \"Pratham\"}],\n" +
                "        \"profile\": [{\"id\": \"field_gender\", \"value\": \"Female\"}]\n" +
                "     }\n" +
                "  }\n" +
                "}", new TypeToken<Map<String, Object>>() {
        }.getType());
    }

}

package org.ekstep.ep.samza.fixture;


import com.google.gson.Gson;
import java.util.Map;

public class ContentCacheFilter {
    public static final String CONTENT_JSON = "{\n" +
            "    \"identifier\": \"LP_FT_2921131\",\n" +
            "    \"description\": \"Test_QA\",\n" +
            "    \"language\": [\n" +
            "        \"English\"\n" +
            "    ],\n" +
            "    \"ageGroup\": [\n" +
            "        \"5-6\"\n" +
            "    ],\n" +
            "    \"mimeType\": \"application/vnd.ekstep.ecml-archive\",\n" +
            "    \"contentType\": \"Story\",\n" +
            "    \"lastUpdatedOn\": \"2017-02-08T13:46:55.372+0000\",\n" +
            "    \"idealScreenSize\": \"normal\",\n" +
            "    \"createdOn\": \"2017-02-08T13:46:44.741+0000\",\n" +
            "    \"objectType\": \"Content\"" +
            "}";


    public static Map<String,Object> getContentMap() {
        return new Gson().fromJson(CONTENT_JSON, Map.class);
    }

    public static String getContentID() {
        return "LP_FT_2921131";
    }

}

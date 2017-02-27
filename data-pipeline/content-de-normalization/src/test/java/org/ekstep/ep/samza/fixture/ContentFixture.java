package org.ekstep.ep.samza.fixture;

import com.google.gson.Gson;
import org.ekstep.ep.samza.Content;
import java.util.Map;

public class ContentFixture {
    public static final String CONTENT_JSON = "{\n" +
            "    \"code\": \"Test_QA\",\n" +
            "    \"description\": \"Test_QA\",\n" +
            "    \"language\": [\n" +
            "        \"English\"\n" +
            "    ],\n" +
            "    \"mimeType\": \"application/vnd.ekstep.ecml-archive\",\n" +
            "    \"idealScreenSize\": \"normal\",\n" +
            "    \"createdOn\": \"2017-02-08T13:46:44.741+0000\",\n" +
            "    \"objectType\": \"Content\",\n" +
            "    \"gradeLevel\": [\n" +
            "        \"Grade 1\"\n" +
            "    ],\n" +
            "    \"lastUpdatedOn\": \"2017-02-08T13:46:55.372+0000\",\n" +
            "    \"contentType\": \"Story\",\n" +
            "    \"owner\": \"EkStep\",\n" +
            "    \"identifier\": \"do_30076072\",\n" +
            "    \"os\": [\n" +
            "        \"All\"\n" +
            "    ],\n" +
            "    \"visibility\": \"Default\",\n" +
            "    \"mediaType\": \"content\",\n" +
            "    \"ageGroup\": [\n" +
            "        \"5-6\"\n" +
            "    ],\n" +
            "    \"osId\": \"org.ekstep.quiz.app\",\n" +
            "    \"graph_id\": \"domain\",\n" +
            "    \"nodeType\": \"DATA_NODE\",\n" +
            "    \"pkgVersion\": 4,\n" +
            "    \"versionKey\": \"1486561615372\",\n" +
            "    \"idealScreenDensity\": \"hdpi\",\n" +
            "    \"name\": \".TestContent!23.\",\n" +
            "    \"status\": \"Live\",\n" +
            "    \"node_id\": 94910,\n" +
            "    \"tags\": [\n" +
            "        \"LP_functionalTest\"\n" +
            "    ],\n" +
            "    \"artifactUrl\": \"https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/content/lp_ft_2921131/artifact/verbs_test_1486561607291.zip\",\n" +
            "    \"size\": 14995748,\n" +
            "    \"lastPublishedOn\": \"2017-02-08T13:46:55.119+0000\",\n" +
            "    \"downloadUrl\": \"https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/LP_FT_2921131/.testcontent23._1486561612577_lp_ft_2921131_4.0.ecar\",\n" +
            "    \"es_metadata_id\": \"LP_FT_2921131\"\n" +
            "}";

    public static Content getContent() {
        return new Gson().fromJson(CONTENT_JSON, Content.class);
    }

    public static Map<String,Object> getContentMap() {
        return new Gson().fromJson(CONTENT_JSON, Map.class);
    }

    public static String getContentID() {
        return "do_30076072";
    }

}

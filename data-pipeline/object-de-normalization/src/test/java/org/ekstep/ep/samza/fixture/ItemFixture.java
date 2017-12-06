package org.ekstep.ep.samza.fixture;

import com.google.gson.Gson;
import org.ekstep.ep.samza.search.domain.Item;

import java.util.Map;

public class ItemFixture {

    public static final String ITEM_JSON = "{\n" +
            "        \"template\": \"org.ekstep.mcq.t.ta\",\n" +
            "        \"code\": \"domain_4502\",\n" +
            "        \"keywords\": [\n" +
            "          \"addition\",\n" +
            "          \"add\",\n" +
            "          \"sum\"\n" +
            "        ],\n" +
            "        \"qlevel\": \"DIFFICULT\",\n" +
            "        \"language\": [\n" +
            "          \"Hindi\"\n" +
            "        ],\n" +
            "        \"partial_scoring\": false,\n" +
            "        \"type\": \"mcq\",\n" +
            "        \"title\": \"जोड़\",\n" +
            "        \"createdOn\": \"2016-06-10T05:30:17.982+0000\",\n" +
            "        \"objectType\": \"AssessmentItem\",\n" +
            "        \"feedback\": \"दोबारा कोशिश करें\",\n" +
            "        \"gradeLevel\": [\n" +
            "          \"Grade 2\"\n" +
            "        ],\n" +
            "        \"options\": \"[{\\\"value\\\":{\\\"type\\\":\\\"mixed\\\",\\\"text\\\":612},\\\"answer\\\":false},{\\\"value\\\":{\\\"type\\\":\\\"mixed\\\",\\\"text\\\":75},\\\"answer\\\":false},{\\\"value\\\":{\\\"type\\\":\\\"mixed\\\",\\\"text\\\":72},\\\"answer\\\":true},{\\\"value\\\":{\\\"type\\\":\\\"mixed\\\",\\\"text\\\":62},\\\"answer\\\":false}]\",\n" +
            "        \"lastUpdatedOn\": \"2016-06-10T10:39:25.754+0000\",\n" +
            "        \"used_for\": \"worksheet\",\n" +
            "        \"owner\": \"feroz\",\n" +
            "        \"identifier\": \"ek.n.ib.a.ddc.bp3.3\",\n" +
            "        \"question\": \"33 + 39 = ?\",\n" +
            "        \"graph_id\": \"domain\",\n" +
            "        \"nodeType\": \"DATA_NODE\",\n" +
            "        \"max_score\": 1,\n" +
            "        \"name\": \"जोड़\",\n" +
            "        \"num_answers\": 1,\n" +
            "        \"template_id\": \"domain_49164\",\n" +
            "        \"node_id\": 66262,\n" +
            "        \"es_metadata_id\": \"ek.n.ib.a.ddc.bp3.3\"\n" +
            "      }";

    public static Item getItem() {
        return new Gson().fromJson(ITEM_JSON, Item.class);
    }

    public static Map<String,Object> getItemMap() {
        return new Gson().fromJson(ITEM_JSON, Map.class);
    }

    public static String getItemID() {
        return "domain_4502";
    }

}

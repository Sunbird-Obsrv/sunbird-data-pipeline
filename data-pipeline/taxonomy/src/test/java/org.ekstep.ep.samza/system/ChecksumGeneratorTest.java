package org.ekstep.ep.samza.system;

import com.google.gson.Gson;
import org.junit.Assert;
import org.ekstep.ep.samza.system.ChecksumGenerator;
import org.junit.Test;

import java.util.Map;

public class ChecksumGeneratorTest {

    @Test
    public void ShouldCreateChecksum(){

        String json = (String) createEvent();

        ChecksumGenerator checksumGenerator = new ChecksumGenerator(json);
        String checksum = checksumGenerator.generateCheksum();
        Assert.assertNotNull(checksum);
    }

    private String createEvent(){
       String jsonObject = "{\n" +
                "    \"eid\": \"ME_USER_GAME_LEVEL\",\n" +
                "    \"ts\": \"2015-09-18T08:23:11+00:00\",\n" +
                "    \"ver\": \"1.0\",\n" +
                "    \"uid\": \"40887ec1e1e82887a308a3ffe94dccbde9e067ee\",\n" +
                "    \"cid\": \"LD4\",\n" +
                "    \"ctype\": \"LD\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"org.ekstep.lit.scrnr.kan.lite_static\",\n" +
                "        \"ver\": \"1.30\"\n" +
                "    },\n" +
                "    \"pdata\": {\n" +
                "        \"id\": \"AssessmentPipeline\",\n" +
                "        \"mod\": \"LitScreenerLevelComputation\",\n" +
                "        \"ver\": \"1.0\"\n" +
                "    },\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"score\": 17,\n" +
                "            \"maxscore\": 35,\n" +
                "            \"current_level\": \"NL\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"taxonomy\": {\n" +
                "        \"LD\": {\n" +
                "            \"id\": \"LD4\",\n" +
                "            \"name\": \"Decoding & Fluency\",\n" +
                "            \"parent\": null,\n" +
                "            \"type\": \"LD\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"flags\": {\n" +
                "        \"ldata_processed\": true,\n" +
                "        \"ldata_obtained\": false,\n" +
                "        \"child_data_processed\": true\n" +
                "    },\n" +
                "    \"udata\": {\n" +
                "        \"uname\": \"childone\",\n" +
                "        \"age\": 158295191,\n" +
                "        \"dob\": \"2010-09-12 05:30:00\",\n" +
                "        \"age_completed_years\": 5,\n" +
                "        \"gender\": \"male\",\n" +
                "        \"uekstep_id\": \"child1\"\n" +
                "    },\n" +
                "    \"@version\": \"1\",\n" +
                "    \"@timestamp\": \"2015-09-18T08:39:57.359Z\",\n" +
                "    \"type\": \"events\"\n" +
                "}";

        return jsonObject;
    }
}

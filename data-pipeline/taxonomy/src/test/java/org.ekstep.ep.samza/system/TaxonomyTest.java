package org.ekstep.ep.samza.system;

import com.google.gson.Gson;
import org.apache.samza.storage.kv.KeyValueStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.mockito.Mockito.stub;


public class TaxonomyTest {
    KeyValueStore taxonomyStore;
    Taxonomy taxonomy;

    @Test
    public void shouldReturnCid(){

        taxonomyStore = Mockito.mock(KeyValueStore.class);
        taxonomy =  new Taxonomy("LT1",taxonomyStore);

        Assert.assertEquals("LT1", (String) taxonomy.getCid());
    }

    @Test
    public void shouldCreateTaxonomyMapFromTaxonomyStoreAndReturnIt(){

        taxonomyStore = Mockito.mock(KeyValueStore.class);
        Taxonomy taxonomy = new Taxonomy("LT11",taxonomyStore);

        Map<String,Object> taxonomyData = (Map<String,Object>) getTaxonomyData();

        Map<String,Object> taxonomyStoreData = (Map<String,Object>) getTaxonomyStoreData();
        stub(taxonomyStore.get("LT11")).toReturn(taxonomyStoreData.get("LT11"));
        stub(taxonomyStore.get("LO9")).toReturn(taxonomyStoreData.get("LO9"));
        stub(taxonomyStore.get("LD5")).toReturn(taxonomyStoreData.get("LD5"));

        Assert.assertEquals(taxonomyData, (Map<String,Object>) taxonomy.getTaxonomyData("LT11"));
    }

    private Map<String,Object> getTaxonomyData(){
        Map<String, Object> taxonomyData = new Gson().fromJson("{\n" +
                "    \"LT11\": {\n" +
                "        \"id\": \"LT11\",\n" +
                "        \"name\": \"Read and choose Picture\",\n" +
                "        \"parent\": \"LO9\"\n" +
                "    },\n" +
                "    \"LO9\": {\n" +
                "        \"id\": \"LO9\",\n" +
                "        \"name\": \"Sentence Comprehension\",\n" +
                "        \"parent\": \"LD5\"\n" +
                "    },\n" +
                "    \"LD5\": {\n" +
                "        \"id\": \"LD5\",\n" +
                "        \"name\": \"Reading Comprehension\",\n" +
                "        \"parent\": null\n" +
                "    }\n" +
                "}",Map.class);
        return taxonomyData;
    }

    private Map<String,Object> getTaxonomyStoreData(){
        Map<String,Object> taxonomyStoreData = new Gson().fromJson("{\n" +
                "    \"LT6\": {\n" +
                "        \"id\": \"LT6\",\n" +
                "        \"name\": \"Teacher Teacher!\",\n" +
                "        \"parent\": \"LO6\"\n" +
                "    },\n" +
                "    \"LT7\": {\n" +
                "        \"id\": \"LT7\",\n" +
                "        \"name\": \"Word picture matching\",\n" +
                "        \"parent\": \"LO7\"\n" +
                "    },\n" +
                "    \"LT4\": {\n" +
                "        \"id\": \"LT4\",\n" +
                "        \"name\": \"Is this right?\",\n" +
                "        \"parent\": \"LO4\"\n" +
                "    },\n" +
                "    \"LT5\": {\n" +
                "        \"id\": \"LT5\",\n" +
                "        \"name\": \"Akshara Sound\",\n" +
                "        \"parent\": \"LO5\"\n" +
                "    },\n" +
                "    \"LT2\": {\n" +
                "        \"id\": \"LT2\",\n" +
                "        \"name\": \"Pick the correct Picture\",\n" +
                "        \"parent\": \"LO2\"\n" +
                "    },\n" +
                "    \"LT3\": {\n" +
                "        \"id\": \"LT3\",\n" +
                "        \"name\": \"Listen and choose picture\",\n" +
                "        \"parent\": \"LO3\"\n" +
                "    },\n" +
                "    \"LT1\": {\n" +
                "        \"id\": \"LT1\",\n" +
                "        \"name\": \"Chili Pili\",\n" +
                "        \"parent\": \"LO1\"\n" +
                "    },\n" +
                "    \"LT9\": {\n" +
                "        \"id\": \"LT9\",\n" +
                "        \"name\": \"Word completion\",\n" +
                "        \"parent\": \"LO8\"\n" +
                "    },\n" +
                "    \"LO2\": {\n" +
                "        \"id\": \"LO2\",\n" +
                "        \"name\": \"Lexical Judgement\",\n" +
                "        \"parent\": \"LD1\"\n" +
                "    },\n" +
                "    \"LO1\": {\n" +
                "        \"id\": \"LO1\",\n" +
                "        \"name\": \"Receptive Vocabulary\",\n" +
                "        \"parent\": \"LD1\"\n" +
                "    },\n" +
                "    \"LO10\": {\n" +
                "        \"id\": \"LO10\",\n" +
                "        \"name\": \"Passage Comprehension\",\n" +
                "        \"parent\": \"LD5\"\n" +
                "    },\n" +
                "    \"LT11\": {\n" +
                "        \"id\": \"LT11\",\n" +
                "        \"name\": \"Read and choose Picture\",\n" +
                "        \"parent\": \"LO9\"\n" +
                "    },\n" +
                "    \"LT13\": {\n" +
                "        \"id\": \"LT13\",\n" +
                "        \"name\": \"Passage Reading (match the words)\",\n" +
                "        \"parent\": \"LO10\"\n" +
                "    },\n" +
                "    \"LD3\": {\n" +
                "        \"id\": \"LD3\",\n" +
                "        \"name\": \"Akshara Knowledge\"\n" +
                "    },\n" +
                "    \"LO7\": {\n" +
                "        \"id\": \"LO7\",\n" +
                "        \"name\": \"Decoding for Reading\",\n" +
                "        \"parent\": \"LD4\"\n" +
                "    },\n" +
                "    \"LD2\": {\n" +
                "        \"id\": \"LD2\",\n" +
                "        \"name\": \"Listening Comprehension\"\n" +
                "    },\n" +
                "    \"LO8\": {\n" +
                "        \"id\": \"LO8\",\n" +
                "        \"name\": \"Decoding for Spelling\",\n" +
                "        \"parent\": \"LD4\"\n" +
                "    },\n" +
                "    \"LD1\": {\n" +
                "        \"id\": \"LD1\",\n" +
                "        \"name\": \"Vocabulary\",\n" +
                "        \"parent\": null\n" +
                "    },\n" +
                "    \"LO9\": {\n" +
                "        \"id\": \"LO9\",\n" +
                "        \"name\": \"Sentence Comprehension\",\n" +
                "        \"parent\": \"LD5\"\n" +
                "    },\n" +
                "    \"LO3\": {\n" +
                "        \"id\": \"LO3\",\n" +
                "        \"name\": \"Sentence Comprehension\",\n" +
                "        \"parent\": \"LD2\"\n" +
                "    },\n" +
                "    \"LO4\": {\n" +
                "        \"id\": \"LO4\",\n" +
                "        \"name\": \"Grammaticality Judgement/Syntax\",\n" +
                "        \"parent\": \"LD2\"\n" +
                "    },\n" +
                "    \"LO5\": {\n" +
                "        \"id\": \"LO5\",\n" +
                "        \"name\": \"Sound-to-symbol Mapping\",\n" +
                "        \"parent\": \"LD3\"\n" +
                "    },\n" +
                "    \"LD5\": {\n" +
                "        \"id\": \"LD5\",\n" +
                "        \"name\": \"Reading Comprehension\",\n" +
                "        \"parent\": null\n" +
                "    },\n" +
                "    \"LO6\": {\n" +
                "        \"id\": \"LO6\",\n" +
                "        \"name\": \"Decoding for Spelling\",\n" +
                "        \"parent\": \"LD4\"\n" +
                "    },\n" +
                "    \"LD4\": {\n" +
                "        \"id\": \"LD4\",\n" +
                "        \"name\": \"Decoding & Fluency\",\n" +
                "        \"parent\": null\n" +
                "    }\n" +
                "}",Map.class);
        return taxonomyStoreData;
    }
}

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
    public void shouldReturnCType(){

        taxonomyStore = Mockito.mock(KeyValueStore.class);
        taxonomy =  new Taxonomy("LT11",taxonomyStore);

        Map<String,Object> taxonomyStoreData = (Map<String,Object>) getTaxonomyStoreData();
        stub(taxonomyStore.get("LT11")).toReturn(taxonomyStoreData.get("LT11"));

        Assert.assertEquals("LT", (String) taxonomy.getCType());
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
                "    \"LT\": {\n" +
                "        \"id\": \"LT11\",\n" +
                "        \"name\": \"ReadandchoosePicture\",\n" +
                "        \"parent\": \"LO9\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LO\": {\n" +
                "        \"id\": \"LO9\",\n" +
                "        \"name\": \"SentenceComprehension\",\n" +
                "        \"parent\": \"LD5\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LD\": {\n" +
                "        \"id\": \"LD5\",\n" +
                "        \"name\": \"ReadingComprehension\",\n" +
                "        \"type\": \"LD\",\n" +
                "        \"parent\": null\n" +
                "    }\n" +
                "}",Map.class);
        return taxonomyData;
    }

    private Map<String,Object> getTaxonomyStoreData(){
        Map<String,Object> taxonomyStoreData = new Gson().fromJson("{\n" +
                "    \"LT6\": {\n" +
                "        \"id\": \"LT6\",\n" +
                "        \"name\": \"TeacherTeacher!\",\n" +
                "        \"parent\": \"LO6\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LT7\": {\n" +
                "        \"id\": \"LT7\",\n" +
                "        \"name\": \"Wordpicturematching\",\n" +
                "        \"parent\": \"LO7\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LT4\": {\n" +
                "        \"id\": \"LT4\",\n" +
                "        \"name\": \"Isthisright?\",\n" +
                "        \"parent\": \"LO4\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LT5\": {\n" +
                "        \"id\": \"LT5\",\n" +
                "        \"name\": \"AksharaSound\",\n" +
                "        \"parent\": \"LO5\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LT2\": {\n" +
                "        \"id\": \"LT2\",\n" +
                "        \"name\": \"PickthecorrectPicture\",\n" +
                "        \"parent\": \"LO2\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LT3\": {\n" +
                "        \"id\": \"LT3\",\n" +
                "        \"name\": \"Listenandchoosepicture\",\n" +
                "        \"parent\": \"LO3\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LT1\": {\n" +
                "        \"id\": \"LT1\",\n" +
                "        \"name\": \"ChiliPili\",\n" +
                "        \"parent\": \"LO1\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LT9\": {\n" +
                "        \"id\": \"LT9\",\n" +
                "        \"name\": \"Wordcompletion\",\n" +
                "        \"parent\": \"LO8\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LO2\": {\n" +
                "        \"id\": \"LO2\",\n" +
                "        \"name\": \"LexicalJudgement\",\n" +
                "        \"parent\": \"LD1\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LO1\": {\n" +
                "        \"id\": \"LO1\",\n" +
                "        \"name\": \"ReceptiveVocabulary\",\n" +
                "        \"parent\": \"LD1\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LO10\": {\n" +
                "        \"id\": \"LO10\",\n" +
                "        \"name\": \"PassageComprehension\",\n" +
                "        \"parent\": \"LD5\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LT11\": {\n" +
                "        \"id\": \"LT11\",\n" +
                "        \"name\": \"ReadandchoosePicture\",\n" +
                "        \"parent\": \"LO9\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LT13\": {\n" +
                "        \"id\": \"LT13\",\n" +
                "        \"name\": \"PassageReading(matchthewords)\",\n" +
                "        \"parent\": \"LO10\",\n" +
                "        \"type\": \"LT\"\n" +
                "    },\n" +
                "    \"LD3\": {\n" +
                "        \"id\": \"LD3\",\n" +
                "        \"name\": \"AksharaKnowledge\",\n" +
                "        \"type\": \"LD\",\n" +
                "        \"parent\" : null\n" +
                "    },\n" +
                "    \"LO7\": {\n" +
                "        \"id\": \"LO7\",\n" +
                "        \"name\": \"DecodingforReading\",\n" +
                "        \"parent\": \"LD4\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LD2\": {\n" +
                "        \"id\": \"LD2\",\n" +
                "        \"name\": \"ListeningComprehension\",\n" +
                "        \"type\": \"LD\"\n" +
                "    },\n" +
                "    \"LO8\": {\n" +
                "        \"id\": \"LO8\",\n" +
                "        \"name\": \"DecodingforSpelling\",\n" +
                "        \"parent\": \"LD4\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LD1\": {\n" +
                "        \"id\": \"LD1\",\n" +
                "        \"name\": \"Vocabulary\",\n" +
                "        \"type\": \"LD\",\n" +
                "        \"parent\" : null\n" +
                "    },\n" +
                "    \"LO9\": {\n" +
                "        \"id\": \"LO9\",\n" +
                "        \"name\": \"SentenceComprehension\",\n" +
                "        \"parent\": \"LD5\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LO3\": {\n" +
                "        \"id\": \"LO3\",\n" +
                "        \"name\": \"SentenceComprehension\",\n" +
                "        \"parent\": \"LD2\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LO4\": {\n" +
                "        \"id\": \"LO4\",\n" +
                "        \"name\": \"GrammaticalityJudgement/Syntax\",\n" +
                "        \"parent\": \"LD2\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LO5\": {\n" +
                "        \"id\": \"LO5\",\n" +
                "        \"name\": \"Sound-to-symbolMapping\",\n" +
                "        \"parent\": \"LD3\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LD5\": {\n" +
                "        \"id\": \"LD5\",\n" +
                "        \"name\": \"ReadingComprehension\",\n" +
                "        \"type\": \"LD\",\n" +
                "        \"parent\" : null\n" +
                "    },\n" +
                "    \"LO6\": {\n" +
                "        \"id\": \"LO6\",\n" +
                "        \"name\": \"DecodingforSpelling\",\n" +
                "        \"parent\": \"LD4\",\n" +
                "        \"type\": \"LO\"\n" +
                "    },\n" +
                "    \"LD4\": {\n" +
                "        \"id\": \"LD4\",\n" +
                "        \"name\": \"Decoding&Fluency\",\n" +
                "        \"type\": \"LD\",\n" +
                "        \"parent\" : null\n" +
                "    }\n" +
                "}",Map.class);
        return taxonomyStoreData;
    }
}

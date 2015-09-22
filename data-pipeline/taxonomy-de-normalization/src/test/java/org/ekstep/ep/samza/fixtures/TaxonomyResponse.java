package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by shashankteotia on 9/19/15.
 */
public class TaxonomyResponse {
    private static String jsonResponse = "{\"id\":\"ekstep.lp.taxonomy.hierarchy\",\"ver\":\"1.0\",\"ts\":\"2015-09-22T03:25:37ZZ\",\"params\":{\"resmsgid\":\"be3b5dae-8e93-41a4-bb31-973a83516629\",\"msgid\":null,\"err\":null,\"status\":\"successful\",\"errmsg\":null},\"responseCode\":\"OK\",\"result\":{\"taxonomy_hierarchy\":{\"identifier\":\"literacy_v2\",\"objectType\":\"Taxonomy\",\"type\":null,\"metadata\":{\"description\":\"Literacy\",\"name\":\"Literacy V2\"},\"children\":[{\"identifier\":\"LD5\",\"objectType\":\"Concept\",\"type\":\"LD\",\"metadata\":{\"description\":\"Reading Comprehension\",\"name\":\"Reading Comprehension\"},\"children\":[{\"identifier\":\"LO10\",\"objectType\":\"Concept\",\"type\":\"LO\",\"metadata\":{\"description\":\"Passage Comprehension\",\"name\":\"Passage Comprehension\"},\"children\":[{\"identifier\":\"LT13\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Passage Reading (match the words to the blanks in passage)\",\"name\":\"Passage Reading (match the words)\"},\"children\":null}]},{\"identifier\":\"LO9\",\"objectType\":\"Concept\",\"type\":\"LO\",\"metadata\":{\"description\":\"Sentence Comprehension\",\"name\":\"Sentence Comprehension\"},\"children\":[{\"identifier\":\"LT11\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Read and choose Picture (read the sentence and related questions and choose a picture answer)\",\"name\":\"Read and choose Picture\"},\"children\":null},{\"identifier\":\"LT12\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Passage Reading (fill in the blanks in sentences)\",\"name\":\"Passage Reading (fill in the blanks)\"},\"children\":null},{\"identifier\":\"LT10\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Read and choose word (read the sentence and related questions and choose a word answer)\",\"name\":\"Read and choose word\"},\"children\":null}]}]},{\"identifier\":\"LD4\",\"objectType\":\"Concept\",\"type\":\"LD\",\"metadata\":{\"description\":\" Decoding & Fluency\",\"name\":\"Decoding & Fluency\"},\"children\":[{\"identifier\":\"LO8\",\"objectType\":\"Concept\",\"type\":\"LO\",\"metadata\":{\"description\":\"Decoding for Spelling\",\"name\":\"Decoding for Spelling\"},\"children\":[{\"identifier\":\"LT9\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Word completion (fill in missing akshara from four options to make a meaningful word)\",\"name\":\"Word completion\"},\"children\":null}]},{\"identifier\":\"LO7\",\"objectType\":\"Concept\",\"type\":\"LO\",\"metadata\":{\"description\":\"Decoding for Reading\",\"name\":\"Decoding for Reading\"},\"children\":[{\"identifier\":\"LT7\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Word picture matching (Choose the right word to match the picture shown)\",\"name\":\"Word picture matching\"},\"children\":null},{\"identifier\":\"LT8\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Pick the correct word (find answer to a question about a just-heard sentence from four word options)\",\"name\":\"Pick the correct word\"},\"children\":null}]},{\"identifier\":\"LO6\",\"objectType\":\"Concept\",\"type\":\"LO\",\"metadata\":{\"description\":\"Decoding for Spelling\",\"name\":\"Decoding for Spelling\"},\"children\":[{\"identifier\":\"LT6\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Teacher Teacher! (decide if spelling is right or wrong after listening to word)\",\"name\":\"Teacher Teacher!\"},\"children\":null}]}]},{\"identifier\":\"LD3\",\"objectType\":\"Concept\",\"type\":\"LD\",\"metadata\":{\"description\":\"Akshara Knowledge\",\"name\":\"Akshara Knowledge\"},\"children\":[{\"identifier\":\"LO5\",\"objectType\":\"Concept\",\"type\":\"LO\",\"metadata\":{\"description\":\"Sound-to-symbol Mapping\",\"name\":\"Sound-to-symbol Mapping\"},\"children\":[{\"identifier\":\"LT5\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Akshara Sound (find akshara from four options to match just heard akshara)\",\"name\":\"Akshara Sound\"},\"children\":null}]}]},{\"identifier\":\"LD2\",\"objectType\":\"Concept\",\"type\":\"LD\",\"metadata\":{\"description\":\"Listening Comprehension\",\"name\":\"Listening Comprehension\"},\"children\":[{\"identifier\":\"LO4\",\"objectType\":\"Concept\",\"type\":\"LO\",\"metadata\":{\"description\":\"Grammaticality Judgement/Syntax\",\"name\":\"Grammaticality Judgement/Syntax\"},\"children\":[{\"identifier\":\"LT4\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Is this right? (judge whether just-heard sentence is grammatically correct)\",\"name\":\"Is this right?\"},\"children\":null}]},{\"identifier\":\"LO3\",\"objectType\":\"Concept\",\"type\":\"LO\",\"metadata\":{\"description\":\"Sentence Comprehension\",\"name\":\"Sentence Comprehension\"},\"children\":[{\"identifier\":\"LT3\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Listen and choose picture (find picture from four options to match just heard sentence)\",\"name\":\"Listen and choose picture\"},\"children\":null}]}]},{\"identifier\":\"LD1\",\"objectType\":\"Concept\",\"type\":\"LD\",\"metadata\":{\"description\":\"Vocabulary\",\"name\":\"Vocabulary\"},\"children\":[{\"identifier\":\"LO2\",\"objectType\":\"Concept\",\"type\":\"LO\",\"metadata\":{\"description\":\"Lexical Judgement\",\"name\":\"Lexical Judgement\"},\"children\":[{\"identifier\":\"LT2\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Pick the correct Picture (choose spoken word to match picture with a spoken nonword as disractor)\",\"name\":\"Pick the correct Picture\"},\"children\":null}]},{\"identifier\":\"LO1\",\"objectType\":\"Concept\",\"type\":\"LO\",\"metadata\":{\"description\":\"Receptive Vocabulary\",\"name\":\"Receptive Vocabulary\"},\"children\":[{\"identifier\":\"LT1\",\"objectType\":\"Concept\",\"type\":\"LT\",\"metadata\":{\"description\":\"Chili Pili (find picture from four options to match just heard word)\",\"name\":\"Chili Pili\"},\"children\":null}]}]}]}}}";
    public static String getJsonResponse(){
        return jsonResponse;
    }
    public static Map<String,Object> fetchMap(){
        Map<String,Object> result = (Map<String,Object>)new Gson().fromJson(jsonResponse,Map.class).get("result");
        Map<String,Object> taxonomy_hierarchy = (Map<String,Object>)result.get("taxonomy_hierarchy");
//        ArrayList<Map<String,Object>> children = (ArrayList<Map<String,Object>>)taxonomy_hierarchy.get("children");
        return taxonomy_hierarchy;
    }
    public static Map<String,Object> getMessage(){
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
                "        \"type\": \"LD\"\n" +
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
                "        \"type\": \"LD\"\n" +
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
                "        \"type\": \"LD\"\n" +
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
                "        \"type\": \"LD\"\n" +
                "    }\n" +
                "}",Map.class);
        return taxonomyStoreData;
    }
}

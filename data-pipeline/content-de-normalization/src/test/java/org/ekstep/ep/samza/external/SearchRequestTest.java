package org.ekstep.ep.samza.external;

import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.Gson;

import static org.junit.Assert.*;

public class SearchRequestTest {

    @Test
    public void testToMap() throws Exception {
        SearchRequest req = new SearchRequest("testId");
        HashMap<String, Object> request = (HashMap<String, Object>) req.toMap().get("request");
        HashMap<String, Object> filters = (HashMap<String, Object>) request.get("filters");
        assertEquals(((ArrayList<String>)filters.get("identifier")).get(0),"testId" );
        System.out.println(new Gson().toJson(req.toMap()));
    }
}
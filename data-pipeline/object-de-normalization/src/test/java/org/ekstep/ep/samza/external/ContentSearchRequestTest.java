package org.ekstep.ep.samza.external;

import org.ekstep.ep.samza.search.dto.ContentSearchRequest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class ContentSearchRequestTest {

    @Test
    public void testToMap() throws Exception {
        ContentSearchRequest req = new ContentSearchRequest("testId");
        HashMap<String, Object> request = (HashMap<String, Object>) req.toMap().get("request");
        HashMap<String, Object> filters = (HashMap<String, Object>) request.get("filters");
        assertEquals(((ArrayList<String>)filters.get("identifier")).get(0),"testId" );
    }
}
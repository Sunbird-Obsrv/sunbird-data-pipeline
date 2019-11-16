package org.ekstep.ep.samza.domain;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TestCdata {

    @Test
    public void shouldIntializeTheCdataValues() {
        CData cData = new CData("contentSession", "c803439b26d433fa920200067ebe4408");
        assertTrue(cData.getType() == "contentSession");
        assertTrue(cData.getId() == "c803439b26d433fa920200067ebe4408");

    }

    @Test
    public void shouldGetTheCdataValues() {
        Map<String, Object> cdata = new HashMap<>();
        cdata.put("type", "contentSession");
        cdata.put("id", "c803439b26d433fa920200067ebe4408");

        CData cData = new CData(cdata);
        assertTrue(cData.getType() == "contentSession");
        assertTrue(cData.getId() == "c803439b26d433fa920200067ebe4408");

    }

}

package org.ekstep.ep.samza.system;

import com.google.gson.Gson;
import org.ekstep.ep.samza.service.TaxonomyService;
import org.ekstep.ep.samza.fixtures.TaxonomyEventFixture;
import org.junit.Before;
import org.junit.Test;
import org.ekstep.ep.samza.fixtures.TaxonomyEventFixture;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by shashankteotia on 9/19/15.
 */
public class TaxonomyEventTest {
    private TaxonomyCache taxonomyCache;
    private TaxonomyEvent taxonomyEvent;
    private TaxonomyService taxonomyService;
    @Before
    public void setupMock(){
        taxonomyCache = Mockito.mock(TaxonomyCache.class);
        taxonomyService = Mockito.mock(TaxonomyService.class);
        taxonomyCache.setService(taxonomyService);
        taxonomyEvent = new TaxonomyEvent(TaxonomyEventFixture.getJSON());
        // TODO Replace taxonomyCache with an Interface to swap other cache in
        taxonomyEvent.setCache(taxonomyCache);
    }
    @Test
    public void DeNormalizeLevelSet(){
        Mockito.when(taxonomyCache.get(TaxonomyEventFixture.LT)).thenReturn(TaxonomyEventFixture.LTJSON);
        Mockito.when(taxonomyCache.get(TaxonomyEventFixture.LO)).thenReturn(TaxonomyEventFixture.LOJSON);
        Mockito.when(taxonomyCache.get(TaxonomyEventFixture.LD)).thenReturn(TaxonomyEventFixture.LDJSON);
        try{
            taxonomyEvent.denormalize();
        } catch(java.io.IOException ex){

        }
        Map<String, Object> taxonomy = getMap(taxonomyEvent.getMap().get("taxonomy"));
        Map<String, Object> taxonomy_LT = getMap(taxonomy.get("LT"));
        Map<String, Object> taxonomy_LO = getMap(taxonomy.get("LO"));
        Map<String, Object> taxonomy_LD = getMap(taxonomy.get("LD"));
        Map<String, Object> expected_taxonomy = (Map<String, Object>)TaxonomyEventFixture.getDeNormalizedMessage().get("taxonomy");
        Map<String, Object> expected_taxonomy_LD = (Map<String,Object>)expected_taxonomy.get("LD");
        Map<String, Object> expected_taxonomy_LO = (Map<String,Object>)expected_taxonomy.get("LO");
        Map<String, Object> expected_taxonomy_LT = (Map<String,Object>)expected_taxonomy.get("LT");
        assertEquals(expected_taxonomy_LD.get("id"),taxonomy_LD.get("id"));
        assertEquals(expected_taxonomy_LD.get("name"),taxonomy_LD.get("name"));
        assertEquals(expected_taxonomy_LO.get("id"),taxonomy_LO.get("id"));
        assertEquals(expected_taxonomy_LO.get("name"),taxonomy_LO.get("name"));
        assertEquals(expected_taxonomy_LT.get("id"),taxonomy_LT.get("id"));
        assertEquals(expected_taxonomy_LT.get("name"),taxonomy_LT.get("name"));
    }

    private Map<String,Object> getMap(Object json){
        // TODO unchecked
        return new Gson().fromJson(String.valueOf(json),Map.class);
    }
}
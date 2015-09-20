package org.ekstep.ep.samza.service;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class TaxonomyServiceTest {

    private HttpService mockHttpService;
    private TaxonomyService taxonomyService;

    @Before
    public void setUp() throws Exception {
        mockHttpService = Mockito.mock(HttpService.class);
//        taxonomyService = new TaxonomyService();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void FetchTaxonomy(){

    }
}
package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ekstep.ep.samza.fixtures.TaxonomyResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;

public class TaxonomyServiceTest {

    private HttpService mockHttpService;
    private HttpClient mockHttpClient;
    private TaxonomyService taxonomyService;
    private HttpGet mockHttpGet;
    private HttpResponse mockHttpResponse;
    private StatusLine mockHttpStatus;
    private HttpEntity mockEntity;
    private static final String HOST = "HOST";
    private static final String URL = "URL";

    @Before
    public void setUp() throws Exception {
        mockHttpService = Mockito.mock(HttpService.class);
        mockHttpClient = Mockito.mock(HttpClient.class);
        mockHttpGet = Mockito.mock(HttpGet.class);
        mockHttpResponse = Mockito.mock(HttpResponse.class);
        taxonomyService = new TaxonomyService(HOST,URL,mockHttpClient);
        mockHttpStatus = Mockito.mock(StatusLine.class);
        mockEntity = Mockito.mock(HttpEntity.class);

        Mockito.when(mockHttpResponse.getStatusLine()).thenReturn(mockHttpStatus);
        Mockito.when(mockHttpStatus.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        Mockito.when(mockHttpResponse.getEntity()).thenReturn(mockEntity);
    }

    @Test
    public void FetchTaxonomy() throws IOException {
        String jsonResponse = TaxonomyResponse.getJsonResponse();
        InputStream stream = new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8));
        Mockito.when(mockEntity.getContent()).thenReturn(stream);
        Mockito.when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockHttpResponse);
        Map<String,Object> map =  new Gson().fromJson(jsonResponse, Map.class);
        assertEquals(parseJSON(parseJSON(map.get("result")).get("taxonomy_hierarchy")), taxonomyService.fetch());
    }

    private Map<String,Object> parseJSON(Object map){
        return (Map<String, Object>)map;
    }
}
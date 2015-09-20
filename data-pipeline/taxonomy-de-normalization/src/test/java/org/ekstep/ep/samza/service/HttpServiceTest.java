package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
public class HttpServiceTest {

    private HttpService httpService;
    private HttpClient mockHttpClient;
    private HttpResponse mockHttpResponse;
    private Map<String,String> httpHeaderMap;
    final String HOST = "http://somehost";
    final String URL = "some/relative/url";

    @Before
    public void setup(){
        mockHttpClient = Mockito.mock(HttpClient.class);
        httpService = new HttpService(mockHttpClient,HOST);
        httpHeaderMap = new HashMap<String, String>();
        mockHttpResponse = Mockito.mock(HttpResponse.class);
    }

    @Test
    public void ShouldGet() throws Exception{
        Mockito.when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockHttpResponse);
        assertEquals(mockHttpResponse,httpService.get(URL, httpHeaderMap));

//        String json = EntityUtils.toString(response.getEntity(), "UTF-8");
//        Map<String, Object> jsonObject = new Gson().fromJson(json, Map.class);
    }
}
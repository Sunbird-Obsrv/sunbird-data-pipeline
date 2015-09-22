package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import org.apache.http.Header;
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
import static org.mockito.Mockito.*;

public class HttpServiceTest {

    private HttpService httpService;
    private HttpClient mockHttpClient;
    private HttpGet httpGet;
    private HttpResponse mockHttpResponse;
    private Map<String,String> httpHeaderMap;
    final String HOST = "http://somehost";
    final String URL = "some/relative/url";
    final String HEADER1 = "HEADER1";
    final String VALUE1 = "VALUE1";
    final String HEADER2 = "HEADER2";
    final String VALUE2 = "VALUE2";

    @Before
    public void setup(){
        mockHttpClient = Mockito.mock(HttpClient.class);
        httpGet = new HttpGet();
        httpService = new HttpService(mockHttpClient,HOST);
        httpHeaderMap = new HashMap<String, String>();
        httpHeaderMap.put(HEADER1,VALUE1);
        httpHeaderMap.put(HEADER2,VALUE2);
        mockHttpResponse = Mockito.mock(HttpResponse.class);
    }

    @Test
    public void ShouldGet() throws Exception{
        Mockito.when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockHttpResponse);
        assertEquals(mockHttpResponse,httpService.get(URL, httpHeaderMap));
    }

    @Test
    public void ShouldTakeHeaders() throws Exception{
        httpService.setRequest(httpGet);
        httpService.addHeaders(httpHeaderMap);
        Header headers[] = httpGet.getAllHeaders();
        assertEquals(HEADER1, headers[1].getName());
        assertEquals(VALUE1, headers[1].getValue());
        assertEquals(HEADER2, headers[0].getName());
        assertEquals(VALUE2, headers[0].getValue());
    }
}
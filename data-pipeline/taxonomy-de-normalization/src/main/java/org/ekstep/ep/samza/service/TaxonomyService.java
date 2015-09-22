package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class TaxonomyService implements Fetchable {
    private HttpService httpService;
    private String url;
    public TaxonomyService(String host, String url){
        this.httpService = new HttpService(host);
        this.url = url;
    }
    public TaxonomyService(String host, String url,HttpClient httpClient){
        this(host,url);
        this.httpService = new HttpService(httpClient,host);
    }
    @Override
    public Map<String, Object> fetch() throws java.io.IOException{
        HashMap<String,String> headerOptions = new HashMap<String, String>();
        headerOptions.put("accept","application/json");
        headerOptions.put("user-id", "username");
//      TODO Check for HTTP Response
        HttpResponse response = httpService.get(url, headerOptions);
        String json = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println("- JSON: "+json);
        Map<String, Object> jsonObject = new Gson().fromJson(json, Map.class);
        return parseJSON(parseJSON(jsonObject.get("result")).get("taxonomy_hierarchy"));
    }
    private Map<String,Object> parseJSON(Object map){
        return (Map<String, Object>)map;
    }
}

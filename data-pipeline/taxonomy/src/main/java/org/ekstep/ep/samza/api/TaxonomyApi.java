package org.ekstep.ep.samza.api;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.Map;

/**
 * Created by sreeharikm on 9/13/15.
 */
public class TaxonomyApi {

    public Map<String, Object> getTaxonomyLibrary() throws Exception {

        System.out.println("inside api call function");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(
                "http://lp-sandbox.ekstep.org:8080/taxonomy-service/taxonomy/hierarchy/literacy_v2?cfields=description,name");
        getRequest.addHeader("accept", "application/json");
        getRequest.addHeader("user-id", "username");

        HttpResponse response = httpClient.execute(getRequest);

        String json = EntityUtils.toString(response.getEntity(), "UTF-8");
        Map<String, Object> jsonObject = new Gson().fromJson(json,Map.class);

        System.out.println("jsonObject .... \n"+jsonObject);
        httpClient.getConnectionManager().shutdown();
        return jsonObject;
    }

}



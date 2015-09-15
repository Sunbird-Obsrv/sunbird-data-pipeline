package org.ekstep.ep.samza.api;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sreeharikm on 9/13/15.
 */
public class TaxonomyApi {

    Map<String, Object> jsonObject = new HashMap<String, Object>();
    DefaultHttpClient httpClient = new DefaultHttpClient();
    private String host;

    public TaxonomyApi(String host){
        this.host = host;
    }

    public Map<String, Object> getTaxonomyLibrary() throws Exception {

        try {
            System.out.println("Inside api call function");

            HttpGet getRequest = new HttpGet(
                    host+"/taxonomy-service/taxonomy/hierarchy/literacy_v2?cfields=description,name");

            getRequest.addHeader("accept", "application/json");
            getRequest.addHeader("user-id", "username");

            HttpResponse response = httpClient.execute(getRequest);

            String json = EntityUtils.toString(response.getEntity(), "UTF-8");
            Map<String, Object> jsonObject = new Gson().fromJson(json, Map.class);

            return jsonObject;
        }
        catch (java.net.SocketTimeoutException e) {
            System.out.println("Socket Timeout Exception"+e.getMessage());
            return jsonObject;
        }
        catch (java.io.IOException e) {
            System.out.println("IOException"+e.getMessage());
            return jsonObject;
        }
        finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

}



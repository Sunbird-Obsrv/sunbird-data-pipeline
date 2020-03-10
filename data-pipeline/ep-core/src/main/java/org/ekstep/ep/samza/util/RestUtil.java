package org.ekstep.ep.samza.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.Map;

public class RestUtil {
    /**
     * @param apiURL         - API Url
     * @param requestHeaders - Map<String, String> Request header parameters
     * @return - Unirest.ResponseBody
     * @throws IOException
     */
    public String get(String apiURL, Map<String, String> requestHeaders) throws UnirestException {
        GetRequest request = Unirest.get(apiURL);
        request.headers(requestHeaders);
        return request.asString().getBody();
    }
}
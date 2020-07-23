package org.ekstep.ep.samza.util;


import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import java.util.Map;

public class RestUtil {

    /**
     * @param apiURL         - API Url
     * @param requestHeaders - Map<String, String> Request header parameters
     * @return - Unirest.ResponseBody
     * @throws UnirestException
     */
    public String get(String apiURL, Map<String, String> requestHeaders) throws UnirestException {
        GetRequest request = Unirest.get(apiURL);
        request.headers(requestHeaders);
        return request.asString().getBody();
    }
}
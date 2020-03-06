package org.ekstep.ep.samza.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.Map;

public class RestUtil {
    /**
     *
     *
     * @param apiURL - API Url
     * @param requestHeaders - Map<String, String> Request header parameters
     * @return - okhttp3.Response
     * @throws IOException
     */
    public okhttp3.Response get(String apiURL, Map<String, String> requestHeaders) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder()
                .url(apiURL)
                .get();
        requestHeaders.forEach((k, v) -> requestBuilder.addHeader(k, v));
        return client.newCall(requestBuilder.build()).execute();
    }
}
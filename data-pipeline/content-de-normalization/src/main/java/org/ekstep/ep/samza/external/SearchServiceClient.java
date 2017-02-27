package org.ekstep.ep.samza.external;

import com.google.gson.Gson;
import okhttp3.*;
import org.ekstep.ep.samza.Content;
import org.ekstep.ep.samza.logger.Logger;

import java.io.IOException;

public class SearchServiceClient implements SearchService{
    static Logger LOGGER = new Logger(SearchServiceClient.class);
    private final String endpoint;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient;

    public SearchServiceClient(String endpoint) {
        this.endpoint = endpoint;
        httpClient = new OkHttpClient();
    }

    @Override
    public Content search(String contentId) throws IOException {
        String body = new Gson().toJson(new SearchRequest(contentId).toMap());
        Request request = new Request.Builder()
                .url(endpoint)
                .post(RequestBody.create(JSON_MEDIA_TYPE, body))
                .build();
        Response response = httpClient.newCall(request).execute();
        SearchResponse searchResponse = new Gson().fromJson(response.body().string(), SearchResponse.class);
        if (!searchResponse.successful()) {
            LOGGER.error("USER SERVICE FAILED. RESPONSE: {}",searchResponse.toString());
            return null;
        }
        if (searchResponse.content() != null) {
            return searchResponse.content();
        }
        return null;
    }
}

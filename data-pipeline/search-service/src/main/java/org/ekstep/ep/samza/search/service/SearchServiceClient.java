package org.ekstep.ep.samza.search.service;

import com.google.gson.Gson;
import okhttp3.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.search.domain.Content;
import org.ekstep.ep.samza.search.domain.Item;
import org.ekstep.ep.samza.search.dto.ContentSearchRequest;
import org.ekstep.ep.samza.search.dto.ContentSearchResponse;
import org.ekstep.ep.samza.search.dto.ItemSearchRequest;
import org.ekstep.ep.samza.search.dto.ItemSearchResponse;

import java.io.IOException;

public class SearchServiceClient implements SearchService {
    static Logger LOGGER = new Logger(SearchServiceClient.class);
    private final String endpoint;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient;

    public SearchServiceClient(String endpoint) {
        this.endpoint = endpoint;
        httpClient = new OkHttpClient();
    }

    @Override
    public Content searchContent(String contentId) throws IOException {
        String body = new Gson().toJson(new ContentSearchRequest(contentId).toMap());
        Request request = new Request.Builder()
                .url(endpoint)
                .post(RequestBody.create(JSON_MEDIA_TYPE, body))
                .build();
        Response response = httpClient.newCall(request).execute();
        ContentSearchResponse contentSearchResponse = new Gson().fromJson(response.body().string(), ContentSearchResponse.class);
        if (!contentSearchResponse.successful()) {
            LOGGER.error("SEARCH SERVICE FAILED. RESPONSE: {}", contentSearchResponse.toString());
            return null;
        }
        if (contentSearchResponse.value() != null) {
            return contentSearchResponse.value();
        }
        return null;
    }

    @Override
    public Item searchItem(String itemId) throws IOException {
        String body = new Gson().toJson(new ItemSearchRequest(itemId).toMap());
        Request request = new Request.Builder()
                .url(endpoint)
                .post(RequestBody.create(JSON_MEDIA_TYPE, body))
                .build();
        Response response = httpClient.newCall(request).execute();
        String string = response.body().string();
        ItemSearchResponse searchResponse = new Gson().fromJson(string, ItemSearchResponse.class);

        if (!searchResponse.successful()) {
            LOGGER.error("SEARCH SERVICE FAILED. RESPONSE: {}", searchResponse.toString());
            return null;
        }

        if (searchResponse.value() != null) {
            return searchResponse.value();
        }
        return null;
    }
}

package org.ekstep.ep.samza.util;

import org.ekstep.ep.samza.core.Logger;
import com.google.gson.Gson;
import okhttp3.*;
import org.ekstep.ep.samza.domain.Location;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LocationSearchServiceClient {
    static Logger LOGGER = new Logger(LocationSearchServiceClient.class);
    private final String channelEndpoint;
    private final String locationEndpoint;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient;
    private String apiToken;

    public LocationSearchServiceClient(String channelEndpoint, String locationEndpoint, String apiToken) {
        this.channelEndpoint = channelEndpoint;
        this.locationEndpoint = locationEndpoint;
        this.apiToken = apiToken;
        httpClient = new OkHttpClient();
    }

    public String searchChannelLocationId(String channel) throws IOException {
        String body = new Gson().toJson(new ChannelSearchRequest(channel).toMap());
        Request request = new Request.Builder()
            .url(channelEndpoint).header(HttpHeaders.AUTHORIZATION, apiToken)
            .post(RequestBody.create(JSON_MEDIA_TYPE, body))
            .build();
        Response response = httpClient.newCall(request).execute();
        String responseBody = response.body().string();
        try {
            ChannelSearchResponse channelSearchResponse = new Gson().fromJson(responseBody, ChannelSearchResponse.class);
            if (!channelSearchResponse.successful()) {
                LOGGER.error("SEARCH SERVICE FAILED. RESPONSE: {}", channelSearchResponse.toString());
                return null;
            }
            if (channelSearchResponse.value() != null) {
                return channelSearchResponse.value();
            }
        } catch (Exception ex) {
            LOGGER.error("SEARCH RESPONSE PARSING FAILED. RESPONSE: {}", responseBody);
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            LOGGER.error("Error trace when parsing Search Response: ", sw.toString());
        }
        return null;
    }

    public Location searchLocation(String locationId) throws IOException {
        String body = new Gson().toJson(new LocationSearchRequest(locationId).toMap());
        Request request = new Request.Builder()
            .url(locationEndpoint).header(HttpHeaders.AUTHORIZATION, apiToken)
            .post(RequestBody.create(JSON_MEDIA_TYPE, body))
            .build();
        Response response = httpClient.newCall(request).execute();
        String string = response.body().string();
        LocationSearchResponse searchResponse = new Gson().fromJson(string, LocationSearchResponse.class);

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

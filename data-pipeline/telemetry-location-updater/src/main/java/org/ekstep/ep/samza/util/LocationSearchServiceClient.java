package org.ekstep.ep.samza.util;

import org.ekstep.ep.samza.core.Logger;
import com.google.gson.Gson;
import okhttp3.*;
import org.ekstep.ep.samza.domain.Location;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

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

    public List<String> searchChannelLocationId(String channel) throws IOException {
        String body = new Gson().toJson(new ChannelSearchRequest(channel).toMap());
        Request request = new Request.Builder()
            .url(channelEndpoint).header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", apiToken))
            .post(RequestBody.create(JSON_MEDIA_TYPE, body))
            .build();
        Response response = httpClient.newCall(request).execute();
        String responseBody = response.body().string();
        try {
            ChannelSearchResponse channelSearchResponse = parseChannelResponse(responseBody);
            if (!channelSearchResponse.successful()) {
                LOGGER.info("ORG SEARCH SERVICE RESPONSE UNSUCCESSFUL. RESPONSE: ", channelSearchResponse.toString());
                return null;
            }
            if (channelSearchResponse.value() != null) {
                LOGGER.info("ORG SEARCH SERVICE RESPONSE SUCCESSFUL. RESPONSE: ", channelSearchResponse.toString());
                return channelSearchResponse.value();
            }
        } catch (Exception ex) {
            LOGGER.error("ORG SEARCH RESPONSE PARSING FAILED. RESPONSE: {}", responseBody);
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            LOGGER.error("Error trace when parsing Org Search Response: ", sw.toString());
        }
        return null;
    }

    public ChannelSearchResponse parseChannelResponse(String responseBody) {
        return new Gson().fromJson(responseBody, ChannelSearchResponse.class);
    }

    public LocationSearchResponse parseLocationResponse(String responseBody) {
        return new Gson().fromJson(responseBody, LocationSearchResponse.class);
    }

    public Location searchLocation(List<String> locationIds) throws IOException {
        String body = new Gson().toJson(new LocationSearchRequest(locationIds).toMap());
        Request request = new Request.Builder()
            .url(locationEndpoint).header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", apiToken))
            .post(RequestBody.create(JSON_MEDIA_TYPE, body))
            .build();
        Response response = httpClient.newCall(request).execute();
        String locationResponseBody = response.body().string();
        LOGGER.info("Location Search Response: ", locationResponseBody);
        LocationSearchResponse searchResponse = parseLocationResponse(locationResponseBody);

        if (!searchResponse.successful()) {
            LOGGER.error("LOCATION SEARCH SERVICE RESPONSE UNSUCCESSFUL. RESPONSE: {}", searchResponse.toString());
            return null;
        }

        if (searchResponse.value() != null) {
            return searchResponse.value();
        }
        return null;
    }
}

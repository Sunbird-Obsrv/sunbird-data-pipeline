package org.ekstep.ep.samza.external;

import com.google.gson.Gson;
import okhttp3.*;
import org.ekstep.ep.samza.logger.Logger;
import java.util.Map;

import java.io.IOException;

public class ObjectServiceClient implements ObjectService {
    static Logger LOGGER = new Logger(ObjectServiceClient.class);

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private String objectServiceEndpoint;
    private final OkHttpClient httpClient;

    public ObjectServiceClient(String objectServiceEndpoint) {
        this.objectServiceEndpoint = objectServiceEndpoint;
        httpClient = new OkHttpClient();
    }

    @Override
    public ObjectResponse createOrUpdate(Map<String,Object> requestMap) throws IOException {
        Request request = new Request.Builder()
                .url(objectServiceEndpoint)
                .post(RequestBody.create(JSON_MEDIA_TYPE, new Gson().toJson(ObjectRequest.create(requestMap))))
                .build();
        Response response = httpClient.newCall(request).execute();
        ObjectResponse objectResponse = new Gson().fromJson(response.body().string(), ObjectResponse.class);

        if (!objectResponse.successful()) {
            LOGGER.error("", "OBJECT SERVICE FAILED. RESPONSE: {}", objectResponse.toString());
        }

        return objectResponse;
    }
}

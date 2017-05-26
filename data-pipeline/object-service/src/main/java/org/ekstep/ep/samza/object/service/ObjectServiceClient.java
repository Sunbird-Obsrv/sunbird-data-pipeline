package org.ekstep.ep.samza.object.service;

import com.google.gson.Gson;
import okhttp3.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.object.dto.GetObjectRequest;
import org.ekstep.ep.samza.object.dto.GetObjectResponse;
import org.ekstep.ep.samza.object.dto.SaveObjectRequest;
import org.ekstep.ep.samza.object.dto.SaveObjectResponse;

import java.io.IOException;
import java.util.Map;

public class ObjectServiceClient implements ObjectService {
    static Logger LOGGER = new Logger(ObjectServiceClient.class);

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private String objectServiceEndpoint;
    private final OkHttpClient httpClient;

    public ObjectServiceClient(String objectServiceEndpoint, OkHttpClient httpClient) {
        this.objectServiceEndpoint = objectServiceEndpoint;
        this.httpClient = httpClient;
    }

    @Override
    public SaveObjectResponse createOrUpdate(Map<String, Object> requestMap) throws IOException {
        Request request = new Request.Builder()
                .url(objectServiceEndpoint + "/v1/object/save")
                .post(RequestBody.create(JSON_MEDIA_TYPE, new Gson().toJson(SaveObjectRequest.create(requestMap))))
                .build();
        Response response = httpClient.newCall(request).execute();
        return new Gson().fromJson(response.body().string(), SaveObjectResponse.class);
    }

    @Override
    public GetObjectResponse get(String id) throws IOException {
        Request request = new Request.Builder()
                .url(objectServiceEndpoint + "/v1/object/read")
                .post(RequestBody.create(JSON_MEDIA_TYPE, new Gson().toJson(GetObjectRequest.create(id))))
                .build();
        Response response = httpClient.newCall(request).execute();
        return new Gson().fromJson(response.body().string(), GetObjectResponse.class);

    }
}

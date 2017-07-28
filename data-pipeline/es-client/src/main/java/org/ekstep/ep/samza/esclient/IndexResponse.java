package org.ekstep.ep.samza.esclient;

import com.google.gson.Gson;
import org.elasticsearch.client.Response;

import java.util.Map;

public class IndexResponse implements ClientResponse {
    private final String status;
    private final String message;

    public IndexResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "BasicResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}

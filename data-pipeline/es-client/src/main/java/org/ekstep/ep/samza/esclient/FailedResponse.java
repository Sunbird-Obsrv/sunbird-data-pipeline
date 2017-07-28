package org.ekstep.ep.samza.esclient;

import com.google.gson.Gson;

import java.util.Map;

public class FailedResponse implements ClientResponse {

    private static final String STATUS = "FAILED";
    private final String status;
    private final String message;

    public FailedResponse(String message) {
        this.status = STATUS;
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
}

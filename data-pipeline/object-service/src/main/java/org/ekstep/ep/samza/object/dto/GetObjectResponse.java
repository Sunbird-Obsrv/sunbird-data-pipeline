package org.ekstep.ep.samza.object.dto;

import java.util.Map;

public class GetObjectResponse {
    public static final String SUCCESS_RESPONSE_STATUS = "successful";
    private String id;
    private String ver;
    private String ts;
    private Map<String, Object> params;
    private Map<String, Object> result;

    public boolean successful() {
        return params != null && SUCCESS_RESPONSE_STATUS.equals(params.get("status"));
    }

    public Map<String, Object> params() {
        return params;
    }

    public Map<String, Object> result() {
        return result;
    }

    @Override
    public String toString() {
        return "GetObjectResponse{" +
                "id='" + id + '\'' +
                ", ver='" + ver + '\'' +
                ", ts='" + ts + '\'' +
                ", params=" + params +
                ", result=" + result +
                '}';
    }
}

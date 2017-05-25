package org.ekstep.ep.samza.object.dto;

import java.util.Map;

public class GetObjectResponse {
    public static final String SUCCESS_RESPONSE_STATUS = "successful";
    private String id;
    private String ver;
    private String ts;
    private Map<String, Object> params;
    private Result result;

    public boolean successful() {
        return params != null && SUCCESS_RESPONSE_STATUS.equals(params.get("status"));
    }

    public Map<String, Object> params() {
        return params;
    }

    public Result result() {
        return result;
    }

    public class Result {
        private Long id;
        private String type;
        private String subtype;
        private String parentid;
        private String parenttype;
        private String code;
        private String name;
        private String details;

        @Override
        public String toString() {
            return "Result{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", subtype='" + subtype + '\'' +
                    ", parentid='" + parentid + '\'' +
                    ", parenttype='" + parenttype + '\'' +
                    ", code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", details='" + details + '\'' +
                    '}';
        }

        public Long id() {
            return id;
        }

        public String type() {
            return type;
        }

        public String subtype() {
            return subtype;
        }

        public String parentid() {
            return parentid;
        }

        public String parenttype() {
            return parenttype;
        }

        public String code() {
            return code;
        }

        public String name() {
            return name;
        }

        public String details() {
            return details;
        }
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

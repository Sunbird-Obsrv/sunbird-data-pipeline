package org.ekstep.ep.samza.util;

import org.ekstep.ep.samza.domain.OrgObject;
import java.util.List;
import java.util.Map;

public class ChannelSearchResponse {
    public static final String SUCCESS_RESPONSE_STATUS = "success";
    private String id;
    private String ver;
    private String ts;
    private Map<String, Object> params;
    private SearchResponse result;

    public boolean successful() {
        return params != null && SUCCESS_RESPONSE_STATUS.equals(params.get("status"));
    }

    public List<String> value() {
        if (result.response.count > 0 && result.response.content.size() > 0) {
            return result.response.content.get(0).locationIds();
        }
        return null;
    }

    private class SearchResult {

        private List<OrgObject> content;
        private Integer count;

        @Override
        public String toString() {
            return "SearchResult{" +
                    "content=" + content +
                    ", count=" + count +
                    '}';
        }
    }

    private class SearchResponse {
        private SearchResult response;

        @Override
        public String toString() {
            return "Response{" + response +
                    '}';
        }
    }

    @Override
    public String toString() {
        return result.toString();
    }
}

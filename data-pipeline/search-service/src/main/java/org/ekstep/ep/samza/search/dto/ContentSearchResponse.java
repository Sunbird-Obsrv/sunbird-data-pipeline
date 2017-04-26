package org.ekstep.ep.samza.search.dto;


import org.ekstep.ep.samza.search.domain.Content;

import java.util.List;
import java.util.Map;

public class ContentSearchResponse {

    public static final String SUCCESS_RESPONSE_STATUS = "successful";
    private String id;
    private String ver;
    private String ts;
    private Map<String, Object> params;
    private SearchResult result;

    public boolean successful() {
        return params != null && SUCCESS_RESPONSE_STATUS.equals(params.get("status"));
    }

    public Content value() {
        if (result.count > 0) {
            return result.content.get(0);
        }
        return null;
    }

    private class SearchResult {
        private List<Content> content;
        private Integer count;

        @Override
        public String toString() {
            return "SearchResult{" +
                    "content=" + content +
                    ", count=" + count +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "id='" + id + '\'' +
                ", ver='" + ver + '\'' +
                ", ts='" + ts + '\'' +
                ", params=" + params +
                ", result=" + result +
                '}';
    }
}

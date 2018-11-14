package org.ekstep.ep.samza.util;

import org.ekstep.ep.samza.domain.OrgObject;
import java.util.List;
import java.util.Map;

public class ChannelSearchResponse {
    public static final String SUCCESS_RESPONSE_STATUS = "successful";
    private String id;
    private String ver;
    private String ts;
    private Map<String, Object> params;
    private SearchResult result;

    public boolean successful() {
        return params != null && SUCCESS_RESPONSE_STATUS.equals(params.get("status"));
    }

    public String value() {
        if (result.count > 0 && result.org.size() > 0) {
            return result.org.get(0).locationId();
        }
        return null;
    }

    private class SearchResult {
        private List<OrgObject> org;
        private Integer count;

        @Override
        public String toString() {
            return "SearchResult{" +
                    "content=" + org +
                    ", count=" + count +
                    '}';
        }
    }
}

package org.ekstep.ep.samza.external;


import org.ekstep.ep.samza.domain.Content;

import java.util.ArrayList;
import java.util.Map;

public class SearchResponse {

    public static final String SUCCESS_RESPONSE_STATUS = "successful";
    private String id;
    private String ver;
    private String ts;
    private Map<String, Object> params;
    private ContentResponse result;

    public boolean successful() {
        return params != null && SUCCESS_RESPONSE_STATUS.equals(params.get("status"));
    }

    public Content content(){
        if (result.content.isEmpty()) {
            return null;
        }
        return result.content.get(0);
    }

    private class ContentResponse {
        private ArrayList<Content> content;
    }

}

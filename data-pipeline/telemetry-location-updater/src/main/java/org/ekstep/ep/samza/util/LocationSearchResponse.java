package org.ekstep.ep.samza.util;

import org.ekstep.ep.samza.domain.LocObject;
import org.ekstep.ep.samza.domain.Location;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocationSearchResponse {
    public static final String SUCCESS_RESPONSE_STATUS = "successful";
    private String id;
    private String ver;
    private String ts;
    private Map<String, Object> params;
    private SearchResponse result;

    public boolean successful() {
        return params != null && SUCCESS_RESPONSE_STATUS.equals(params.get("status"));
    }

    public Location value() {
        if (result.response.loc.size() > 0) {
            String state = "";
            if(result.response.loc.get(0).type().equals("state")) {
                state = result.response.loc.get(0).name();
            }
            Location location = new Location("", "", "", state, "");
            return location;
        }
        return null;
    }

    private class SearchResult {
        private List<LocObject> loc;

        @Override
        public String toString() {
            return "SearchResult{" + loc.stream().map(LocObject::toString).collect(Collectors.joining("|")) +" }";
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

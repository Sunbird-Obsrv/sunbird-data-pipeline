package org.ekstep.ep.samza.util;

import org.ekstep.ep.samza.domain.LocObject;
import org.ekstep.ep.samza.domain.Location;
import org.ekstep.ep.samza.domain.OrgObject;

import java.util.List;
import java.util.Map;

public class LocationSearchResponse {
    public static final String SUCCESS_RESPONSE_STATUS = "successful";
    private String id;
    private String ver;
    private String ts;
    private Map<String, Object> params;
    private SearchResult result;

    public boolean successful() {
        return params != null && SUCCESS_RESPONSE_STATUS.equals(params.get("status"));
    }

    public Location value() {
        Location location = new Location();
        if (result.loc.size() > 0) {
            String state = "";
            if(result.loc.get(0).type().equals("state")) {
                state = result.loc.get(0).name();
            }
            location.setState(state);
            location.setDistrict("");
            return location;
        }
        return null;
    }

    private class SearchResult {
        private List<LocObject> loc;
    }

}

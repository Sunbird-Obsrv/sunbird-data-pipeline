package org.ekstep.ep.samza;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Event {
    private Map<String, Object> data;

    public Event(Map<String, Object> data) {
        this.data = data;
    }

    public boolean belongsToAPartner() {
        String partnerId = getPartnerId();
        return partnerId !=null && !partnerId.isEmpty();
    }

    public String routeTo(){
        String partnerId = getPartnerId();
        return String.format("%s.events", partnerId);
    }

    public void updateType(){
        data.put("type","partner.events");
    }

    private String getPartnerId() {

        ArrayList<Map> tags = (ArrayList<Map>) data.get("tags");
        if(tags == null)
            return null;
        Map firstTag = tags.get(0);
        if(firstTag == null)
            return null;
        return (String)firstTag.get("partnerid");
    }

    public Map<String, Object> getData() {
        return data;
    }
}

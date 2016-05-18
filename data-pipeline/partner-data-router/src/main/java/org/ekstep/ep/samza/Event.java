package org.ekstep.ep.samza;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Array;
import java.util.*;

public class Event {
    private static final List<String> validPartners = Arrays.asList("org.ekstep.partner.akshara", "org.ekstep.partner.pratham", "org.ekstep.partner.sample");
    private Map<String, Object> data;


    public Event(Map<String, Object> data) {
        this.data = data;
    }

    public boolean belongsToAPartner() {
        String partnerId = getPartnerId();
        return StringUtils.isNotBlank(partnerId);
    }

    public String routeTo() {
        String partnerId = getPartnerId();
        return String.format("%s.events", partnerId);
    }

    public void updateType() {
        data.put("type", "partner.events");
    }

    private String getPartnerId() {
        try {
            ArrayList<Map> tags = (ArrayList<Map>) data.get("tags");
            System.out.println(String.format("TAGS:%s",new Gson().toJson(tags)));
            if (tags == null || tags.isEmpty())
                return null;
            String partnerid = "partnerid";
            for (Map tag:tags)
                if(tag!=null && tag.containsKey(partnerid)) {
                    if(tag.get(partnerid) instanceof String) {
                        String partnerId = (String) tag.get(partnerid);
                        if (validPartners.contains(partnerId))
                            return partnerId;
                    }
                    else{
                        ArrayList<String> partners = (ArrayList<String>) tag.get(partnerid);
                        for (String partnerID : partners) {
                            if (validPartners.contains(partnerID))
                                return partnerID;
                        }
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> getData() {
        return data;
    }
}

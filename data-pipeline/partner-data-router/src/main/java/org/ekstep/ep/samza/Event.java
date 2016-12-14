package org.ekstep.ep.samza;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.ekstep.ep.samza.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Event {
    private static final List<String> validPartners = Arrays.asList("org.ekstep.partner.akshara", "org.ekstep.partner.pratham", "org.ekstep.partner.enlearn", "9e94fb35");
    private Map<String, Object> data;
    static Logger LOGGER = new Logger(Event.class);

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
            LOGGER.info(id(), String.format("TAGS: %s", new Gson().toJson(tags)));
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
            LOGGER.error(id(), "ERROR WHEN GETTING PARTNER ID", e);
        }
        return null;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String id() {
        return data != null && data.containsKey("metadata") &&
                (((Map<String, Object>) data.get("metadata")).containsKey("checksum"))
                ? (String) ((Map<String, Object>) data.get("metadata")).get("checksum")
                : null;
    }

    public String eid() {
        return data != null && data.containsKey("eid") ? (String) data.get("eid") : null;
    }

    public String ts() {
        return data != null && data.containsKey("ts") ? (String) data.get("ts") : null;
    }

    public String sid() {
        return data != null && data.containsKey("sid") ? (String) data.get("sid") : null;
    }

}

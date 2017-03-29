package org.ekstep.ep.samza;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.ekstep.ep.samza.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    private Map<String, Object> data;
    static Logger LOGGER = new Logger(Event.class);

    public Event(Map<String, Object> data) {
        this.data = data;
    }

    public boolean belongsToAPartner() {
        String partnerId = getPartnerId();
        return StringUtils.isNotBlank(partnerId);
    }

    public void updateType() {
        data.put("type", "partner.events");
    }

    public void updateMetadata() throws Exception {
        String partnerName = getShaOfPartnerId();
        Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
        if (metadata != null) {
            metadata.put("partner_name",partnerName);
        } else {
            metadata = new HashMap<String, Object>();
            metadata.put("partner_name",partnerName);
            data.put("metadata", metadata);
        }
        LOGGER.info(id(), "METADATA partner_name - ADDED " + metadata);
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
                        return partnerId;
                    }
                    else{
                        ArrayList<String> partners = (ArrayList<String>) tag.get(partnerid);
                        for (String partnerID : partners) {
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

    public String getShaOfPartnerId() throws Exception {
        String partnerId = getPartnerId();
        return encryptPartnerId(partnerId);
    }

    private static String encryptPartnerId(String partnerId) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if(partnerId != null) {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(partnerId.getBytes("UTF-8"));
            return new BigInteger(1, crypt.digest()).toString(16);
        }
        return null;
    }
}

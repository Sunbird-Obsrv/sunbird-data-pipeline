package org.ekstep.ep.samza;

import org.apache.commons.lang.StringUtils;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.Telemetry;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private Telemetry telemetry;
    static Logger LOGGER = new Logger(Event.class);

    public Event(Map<String, Object> data) {
        this.telemetry = new Telemetry(data);
    }

    public boolean belongsToAPartner() {
        String partnerId = getPartnerId();
        return StringUtils.isNotBlank(partnerId);
    }

    public void updateType() {
        telemetry.add("type", "partner.events");
    }

    public void updateMetadata() throws Exception {
        String partnerName = getShaOfPartnerId();
        telemetry.addFieldIfAbsent("metadata",new HashMap<String,Object>());
        telemetry.add("metadata.partner_name", partnerName);
        LOGGER.info(id(), "METADATA partner_name - ADDED " + telemetry.read("metadata").value());
    }

    private String getPartnerId() {
        try {
            return getPartnerIdFromPartnerTags();
        } catch (Exception e) {
            LOGGER.error(id(), "ERROR WHEN GETTING PARTNER ID", e);
        }
        return null;
    }

    private String getPartnerIdFromPartnerTags() {
        ArrayList<Map> tags = (ArrayList<Map>) telemetry.read("tags").value();
        if(tags != null && !tags.isEmpty()){
            return getPartnerIdFromTags(tags, "partnerid");
        }

        ArrayList<String> partners = (ArrayList<String>) telemetry.read("etags.partner").value();
        if(partners != null && !partners.isEmpty()){
            return getPartnerIdFromETags(partners);
        }

        return null;
    }

    private String getPartnerIdFromETags(ArrayList<String> partners) {
        for (String partner : partners) {
            return partner;
        }
        return null;
    }

    private String getPartnerIdFromTags(ArrayList<Map> tags, String partnerId){
        for (Map tag:tags)
            if(tag!=null && tag.containsKey(partnerId)) {
                if(tag.get(partnerId) instanceof String) {
                    String partnerID = (String) tag.get(partnerId);
                    return partnerID;
                }
                else{
                    ArrayList<String> partners = (ArrayList<String>) tag.get(partnerId);
                    for (String partnerID : partners) {
                        return partnerID;
                    }
                }
            }
        return null;
    }

    public Map<String,Object> getMap(){
        return telemetry.getMap();
    }

    public String id() {
        return telemetry.<String>read("metadata.checksum").value();
    }

    public String eid() {
        return telemetry.<String>read("eid").value();
    }

    public String ts() {
        return telemetry.<String>read("ts").value();
    }

    public String sid() {
        return telemetry.<String>read("sid").value();
    }

    public String channel() {
        return telemetry.<String>read("channel").value();
    }

    public String ver() {
        return telemetry.<String>read("ver").value();
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

    public boolean isVersionOne(){
        return ver() != null && ver().equals("1.0");
    }

    public boolean isDefaultChannel(String defaultChannel){
        if(channel() != null && channel().equals(defaultChannel)){
            return true;
        }
        return false;
    }
}

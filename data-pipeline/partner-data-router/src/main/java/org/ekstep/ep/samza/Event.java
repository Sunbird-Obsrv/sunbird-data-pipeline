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
import java.util.List;
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
        ArrayList<Map> cData = (ArrayList<Map>) telemetry.read("context.cdata").value();
        if(cData != null && !cData.isEmpty()){
            return getPartnerIdFromCData(cData);
        }

        return null;
    }

    private String getPartnerIdFromCData(ArrayList<Map> cData){
        for (Map tag:cData)
            if(tag!=null && tag.containsKey("type") && tag.containsKey("id")) {
                if(tag.get("type").equals("partner")) {
                    String partnerID = (String) tag.get("id");
                    return partnerID;
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
        return telemetry.<String>read("context.channel").value();
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

    public boolean isDefaultChannel(List<String> defaultChannels){
        for (String dChannel : defaultChannels) {
            if(channel() != null && channel().equals(dChannel)){
                return true;
            }
        }
        return false;
    }

    public Telemetry telemetry() {
        return this.telemetry;
    }
}

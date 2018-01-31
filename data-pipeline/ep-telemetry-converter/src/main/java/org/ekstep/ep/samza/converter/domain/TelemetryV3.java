package org.ekstep.ep.samza.converter.domain;

import com.google.gson.Gson;
import org.ekstep.ep.samza.converter.exceptions.TelemetryConversionException;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

import java.security.NoSuchAlgorithmException;
import java.util.*;

public class TelemetryV3 {

    private final String ver = "3.0";
    private String eid;
    private long ets;
    private String mid;
    private Actor actor;
    private Context context;
    private TObject object;
    private HashMap<String, Object> edata;
    private ArrayList<String> tags = new ArrayList<>();
    private HashMap<String, String> metadata;

    private Telemetry reader;

    public TelemetryV3(Telemetry reader, Map<String, Object> source)
            throws TelemetryReaderException, NoSuchAlgorithmException, TelemetryConversionException {
        this.reader = reader;
        this.eid = getEid(reader.mustReadValue("eid"), reader);
        this.ets = reader.getEts();
        this.mid = computeMid(this.eid, reader.<String>mustReadValue("mid"));
        HashMap<String, String> metadata = new HashMap<>();
        String checksum = reader.id();
        metadata.put("checksum", checksum);
        this.metadata = metadata;
        this.actor = new Actor(source);
        this.context = new Context(reader);
        this.object = new TObject(reader);
    }

    public TelemetryV3() throws TelemetryReaderException {

    }

    private String computeMid(String eid, String oldMid) {
        // Because v2->v3 is one to many, mids have to be changed
        // We just prefix the V3 EID with old mid to so that mids can still tracked
        return String.format("%s:%s", eid, oldMid);
    }

    private String getEid(String eid, Telemetry event) throws TelemetryConversionException {

        String v3Eid = null;

        switch (eid) {
            case "OE_START":
                v3Eid = "START";
                break;
            case "GE_START":
                v3Eid = "START";
                break;
            case "GE_GENIE_START":
                Map<String, Object> dspec = (Map<String, Object>) event.getEdata().get("dspec");
                if (dspec != null && dspec.containsKey("mdata")) {
                    v3Eid = "EXDATA";
                } else {
                    v3Eid = "START";
                }
                break;
            case "GE_SESSION_START":
                v3Eid = "START";
                break;
            case "CP_SESSION_START":
                v3Eid = "START";
                break;
            case "CE_START":
                v3Eid = "START";
                break;
            case "OE_END":
                v3Eid = "END";
                break;
            case "GE_END":
                v3Eid = "END";
                break;
            case "GE_GENIE_END":
                v3Eid = "END";
                break;
            case "GE_GAME_END":
                v3Eid = "END";
                break;
            case "GE_SESSION_END":
                v3Eid = "END";
                break;
            case "CE_END":
                v3Eid = "END";
                break;
            case "OE_NAVIGATE":
                v3Eid = "IMPRESSION";
                break;
            case "GE_INTERACT":

                if (((String) event.getEdata().get("subtype")).equalsIgnoreCase("SHOW")) {
                    v3Eid = "IMPRESSION";
                    //v3Eid = "LOG"; // For IMPRESSION & LOG the condition is same so I commented LOG
                } else {
                    v3Eid = "INTERACT";
                }
                break;
            case "CP_IMPRESSION":
                v3Eid = "IMPRESSION";
                break;
            case "OE_INTERACT":
                v3Eid = "INTERACT";
                break;
            case "CP_INTERACT":
                v3Eid = "INTERACT";
                break;
            case "CE_INTERACT":
                v3Eid = "INTERACT";
                break;
            case "CE_PLUGIN_LIFECYCLE":
                v3Eid = "INTERACT";
                break;
            case "OE_ASSESS":
                v3Eid = "ASSESS";
                break;
            case "OE_ITEM_RESPONSE":
                v3Eid = "RESPONSE";
                break;
            case "OE_INTERRUPT":
                v3Eid = "INTERRUPT";
                break;
            case "GE_RESUME":
                v3Eid = "INTERRUPT";
                break;
            case "GE_GENIE_RESUME":
                v3Eid = "INTERRUPT";
                break;
            case "GE_INTERRUPT":
                v3Eid = "INTERRUPT";
                break;
            case "GE_FEEDBACK":
                v3Eid = "FEEDBACK";
                break;
            case "GE_TRANSFER":
                v3Eid = "SHARE";
                break;
            case "BE_OBJECT_LIFECYCLE":
                v3Eid = "AUDIT";
                break;
            case "OE_ERROR":
                v3Eid = "ERROR";
                break;
            case "GE_ERROR":
                v3Eid = "ERROR";
                break;
            case "CE_ERROR":
                v3Eid = "ERROR";
                break;
            case "GE_UPDATE":
                v3Eid = "LOG";
                break;
            case "GE_API_CALL":
                v3Eid = "LOG";
                break;
            case "GE_SERVICE_API_CALL":
                v3Eid = "LOG";
                break;
            case "GE_PARTNER_DATA":
                v3Eid = "EXDATA";
                break;
            case "GE_CREATE_USER":
                v3Eid = "AUDIT";
                break;
            case "GE_UPDATE_PROFILE":
            	v3Eid = "AUDIT";
                break;
            default:
                throw new TelemetryConversionException(String.format("Cannot convert '%s' to V3 telemetry. No mapping found", eid), event.getMap());
        }

        return v3Eid;
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, String> metadata) {
        this.metadata = metadata;
    }

    public long getEts() {
        return ets;
    }

    public void setEts(long ets) {
        this.ets = ets;
    }

    public String getMid() {
        return mid;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) throws TelemetryReaderException, NoSuchAlgorithmException {
        this.eid = eid;
        String oldMid = reader.<String>mustReadValue("mid");
        this.mid = computeMid(this.eid, oldMid);
    }

    public String getVer() {
        return ver;
    }

    public Actor getActor() {
        return actor;
    }

    public TObject getObject() {
        return object;
    }

    public void setObject(TObject object) {
        this.object = object;
    }

    public HashMap<String, Object> getEdata() {
        return edata;
    }

    public void setEdata(HashMap<String, Object> edata) {
        this.edata = edata;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(Map<String, Object> event) {
        if (event.containsKey("etags")) {
            Map<String, List<String>> etags = (Map<String, List<String>>) event.get("etags");
            if (null != etags && !etags.isEmpty()) {
                Set<String> keys = etags.keySet();
                Iterator<String> it = keys.iterator();
                while (it.hasNext()) {
                    List<String> tags = etags.get(it.next());
                    this.tags.addAll(tags);
                }
            }
        } else if (event.containsKey("tags")) {
            List<Map<String,Object>> tags = (List<Map<String, Object>>) event.get("tags");
            if(tags != null && !tags.isEmpty()) {
                for (int i = 0; i < tags.size(); i++) {
                    if (tags.get(i) instanceof Map) {
                        Map partnerTags = tags.get(i);
                        if (partnerTags != null && !partnerTags.isEmpty()) {
                            Set<String> keys = partnerTags.keySet();
                            Iterator<String> it = keys.iterator();
                            while (it.hasNext()) {
                                List<String> items = (List<String>) partnerTags.get(it.next());
                                this.tags.addAll(items);
                            }
                        }
                    }
                }
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> v3map = new HashMap<>();
        v3map.put("eid", eid);
        v3map.put("ets", ets);
        v3map.put("ver", ver);
        v3map.put("mid", mid);
        v3map.put("actor", actor);
        v3map.put("context", context);
        v3map.put("object", object);
        v3map.put("metadata", metadata);
        v3map.put("edata", edata);
        v3map.put("tags", tags);
        v3map.put("@timestamp", reader.getAtTimestamp());
        return v3map;
    }

    public String toJson() {
        Map<String, Object> map = toMap();
        return new Gson().toJson(map);
    }
}

package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import org.ekstep.ep.samza.core.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Telemetry {

    static Logger LOGGER = new Logger(Telemetry.class);

    private final String ver = "3.0";
    private String eid;
    private long ets;
    private String mid;
    private Actor actor;
    private Context context;
    private TObject object;
    private HashMap<String, Object> edata;
    private List<String> tags = new ArrayList<>();
    private HashMap<String, String> metadata;
    private long syncts;
    private String syncTimestamp;

    public Telemetry() {

    }

    public Telemetry(Map<String, Object> batchEvent, long syncts, String syncTimestamp, String defaultChannel) {

        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> events = (List<Map<String, Object>>) batchEvent.get("events");

            // TODO - Handle NPE if the batchEvent doesn't have 'ets'. Default 'ets' to System.currentTimeInMillis()
            this.ets = ((Number) batchEvent.get("ets")).longValue();
            this.syncts = syncts;
            this.syncTimestamp = syncTimestamp;

            String mid = (String) batchEvent.get("mid");

            int eventCount = events.size();
            String status = (String) batchEvent.get("sync_status");
            String ver = (String) batchEvent.get("ver");
            if (null == status) {
                status = "SUCCESS";
            }
            String consumerId = (String) batchEvent.get("consumer_id");
            if (null == consumerId) {
                consumerId = "";
            }

            HashMap<String, Object> edata = new HashMap<String, Object>();
            edata.put("type", "telemetry_audit");
            edata.put("level", "INFO");
            edata.put("message", "telemetry sync");
            edata.put("pageid", "data-pipeline");

            List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
            Map<String, Object> param = new HashMap<>();
            param.put("sync_status", status);
            param.put("consumer_id", consumerId);
            param.put("events_count", eventCount);
            param.put("ver", ver);
            params.add(param);
            edata.put("params", params);

            this.eid = "LOG";
            this.edata = edata;
            this.mid = computeMid(this.eid, mid);
            this.metadata = new HashMap<>();
            this.actor = new Actor(batchEvent);
            this.context = new Context(batchEvent, defaultChannel);
            this.object = new TObject(batchEvent);
        } catch (Exception e) {

            LOGGER.info("", "Failed to initialize telemetry spec data: " + e.getMessage());
        }

    }

    public String computeMid(String eid, String mid) {
        // Because v2->v3 is one to many, mids have to be changed
        // We just prefix the LOG EID with telemetry spec mid
        return String.format("%s:%s", eid, mid);
    }


    public TObject getObject() {
        return object;
    }

    public void setObject(TObject object) {
        this.object = object;
    }


    public Map<String, Object> toMap() {
        Map<String, Object> v3map = new HashMap<>();
        v3map.put("eid", this.eid);
        v3map.put("ets", this.ets);
        v3map.put("ver", this.ver);
        v3map.put("mid", this.mid);
        v3map.put("actor", this.actor);
        v3map.put("context", this.context);
        v3map.put("object", this.object);
        v3map.put("metadata", this.metadata);
        v3map.put("edata", this.edata);
        v3map.put("tags", this.tags);
        v3map.put("syncts", this.syncts);
        v3map.put("@timestamp", this.syncTimestamp);
        return v3map;
    }

    public String toJson() {
        Map<String, Object> map = toMap();
        return new Gson().toJson(map);
    }

}

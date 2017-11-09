package org.ekstep.ep.samza.domain;


import java.util.ArrayList;
import java.util.HashMap;

public class TelemetryV3 {
    private String eid;
    private long ets;
    private final String ver = "3.0";
    private String mid;
    private Actor actor;
    private Context context;
    private TObject object;
    private HashMap<String, Object> edata;
    private ArrayList<String> tags;

    public TelemetryV3() {
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

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getVer() {
        return ver;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
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

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

package org.ekstep.ep.samza.domain;

import com.google.gson.annotations.SerializedName;
import org.ekstep.ep.samza.core.Logger;

import java.util.*;

public class Context {
    private String channel = "";
    private String env = "";
    private String sid = "";
    private String did = "";
    static Logger LOGGER = new Logger(Context.class);

    @SerializedName("pdata")
    private Map<String, String> pData;

    @SerializedName("cdata")
    private ArrayList<CData> cData = new ArrayList<>();

    private Rollup rollUp;

    public Context() {
    }

    public Context(Map<String, Object> eventSpec) {

        try{
            List<Map<String, Object>> events = (List<Map<String, Object>>)eventSpec.get("events");
            Map<String, Object> event = events.get(0);
            Map<String, Object> eventContext = (Map<String, Object>)event.get("context");
            env = "telemetry-sync";
            did = (String)eventContext.get("did");
            channel = (String)eventContext.get("channel");
            if (channel==null && "".equals(channel.trim())) {
                channel = "";
            }
            pData = (Map<String, String>)eventContext.get("pdata");
        }catch(Exception e){
            LOGGER.info("","Failed to initialize context: "+ e.getMessage());
        }

    }

    public String getChannel() {
        return channel;
    }

    public String getEnv() {
        return env;
    }

    public String getSid() {
        return sid;
    }

    public String getDid() {
        return did;
    }

    public Map<String, String> getpData() {
        return pData;
    }

    public Rollup getRollUp() {
        return rollUp;
    }

    public void setRollUp(Rollup rollUp) {
        this.rollUp = rollUp;
    }

    public List<CData> getCData() {
        return cData;
    }
}

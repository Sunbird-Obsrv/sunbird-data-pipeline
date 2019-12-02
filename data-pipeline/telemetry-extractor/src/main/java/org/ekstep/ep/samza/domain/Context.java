package org.ekstep.ep.samza.domain;

import com.google.gson.annotations.SerializedName;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.TelemetryExtractorConfig;

import java.util.*;

public class Context {
    private String channel = "";
    private String env = "";
    private String sid = "";
    private String did = "";
    static Logger LOGGER = new Logger(Context.class);

    @SerializedName("pdata")
    private Map<String, String> pData = new HashMap<String, String>();

    @SerializedName("cdata")
    private ArrayList<CData> cData = new ArrayList<>();


    public Context(Map<String, Object> eventSpec, String defaultChannel) {

        this.channel = defaultChannel;
        this.pData.put("id", "pipeline");
        this.pData.put("pid", "");
        this.pData.put("ver", "");

        try {
            List<Map<String, Object>> events = (List<Map<String, Object>>) eventSpec.get("events");
            Map<String, Object> event = events.get(0);
            Map<String, Object> eventContext = (Map<String, Object>) event.get("context");
            env = "telemetry-sync";
            did = (String) eventContext.get("did");
            String channel = (String) eventContext.get("channel");
            if (channel != null && !"".equals(channel.trim())) {
                this.channel = channel;
            }
            Map<String, String> pdata = (Map<String, String>) eventContext.get("pdata");
            if (pdata != null && pdata.containsKey("id")) {
                this.pData = pdata;
            }

        } catch (Exception e) {
            LOGGER.info("", "Failed to initialize context: " + e.getMessage());
        }

    }

}

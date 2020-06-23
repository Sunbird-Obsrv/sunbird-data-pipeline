package org.ekstep.ep.samza.domain;

import com.google.gson.annotations.SerializedName;

import org.ekstep.ep.samza.core.Logger;

import java.util.*;

public class Context {
    private String channel;
    private String env = "data-pipeline";
    private String sid;
    private String did;
    private static Logger LOGGER = new Logger(Context.class);

    @SerializedName("pdata")
    private Map<String, String> pData = new HashMap<>();

    @SerializedName("cdata")
    private ArrayList<CData> cData;


    public Context(String did, String sid, String env, String defaultChannel) {
        this.channel = defaultChannel;
        this.pData.put("id", "data-pipeline");
        this.pData.put("pid", "data-pipeline");
        this.pData.put("ver", "3.0");
        this.sid = sid;
        this.did = did;
    }

}

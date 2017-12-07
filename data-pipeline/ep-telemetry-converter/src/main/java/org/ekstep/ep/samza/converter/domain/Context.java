package org.ekstep.ep.samza.converter.domain;

import com.google.gson.annotations.SerializedName;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Context {
    private String channel;
    private String env;
    private String sid;
    private String did = "";

    @SerializedName("pdata")
    private PData pData;

    @SerializedName("cdata")
    private ArrayList<CData> cData = new ArrayList<>();

    private Rollup rollUp;

    public Context() {
    }

    public Context(Telemetry reader) throws TelemetryReaderException {
        channel = reader.<String>read("channel").valueOrDefault("in.ekstep");
        if ("".equals(channel.trim())) {
            channel = "in.ekstep";
        }
        pData = new PData(reader);

        String eid = reader.mustReadValue("eid");
        String env = reader.<String>read("edata.eks.env").valueOrDefault("");
        if (!env.equals("")) {
            this.env = env;
        } else if (eid.startsWith("OE_")) {
            this.env = "ContentPlayer";
        } else if (eid.startsWith("GE_")) {
            this.env = "Genie";
        } else if (eid.startsWith("CE_")) {
            this.env = "ContentEditor";
        }

        // sid is a mandatory field. but it can come in two possible paths
        // - sid (at the envelope)
        // - context.sid (CE and CP events)
        NullableValue<String> sid = reader.read("sid");
        if (sid.isNull()) {
            // sid in envelope is null. so it might be in context.sid
            this.sid = reader.<String>read("context.sid").valueOrDefault("");
        } else {
            this.sid = sid.value();
        }

        did = reader.<String>read("did").valueOrDefault("");

        List cdata = reader.<List>read("cdata").valueOrDefault(new ArrayList());
        for (Object item : cdata) {
            Map<String, Object> m = (Map<String, Object>) item;
            cData.add(new CData(m));
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

    public PData getpData() {
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

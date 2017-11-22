package org.ekstep.ep.samza.domain;

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

    private ArrayList<CData> cData = new ArrayList<CData>();
    private Rollup rollUp;

    public Context() {
    }

    public Context(Telemetry reader) throws TelemetryReaderException {
        this.channel = reader.getChannel();
        this.pData = new PData(reader);

        String eid = reader.mustReadValue("eid");
        if (eid.startsWith("OE_")) {
            this.env = "ContentPlayer";
        } else if (eid.startsWith("GE_")) {
            this.env = "Genie";
        } else if (eid.startsWith("CE_")) {
            this.env = "ContentEditor";
        } else if (eid.startsWith("CP_")) {
            this.env = reader.<String>read("edata.eks.env").valueOrDefault("");
        }

        // sid is a mandatory field. but it can come in two possible paths
        // - sid (at the envelope)
        // - context.sid (CE and CP events)
        NullableValue<String> sid = reader.read("sid");
        if (sid.isNull()) {
            // sid in envelope is null. so it should come in context.sid
            this.sid = reader.mustReadValue("context.sid");
        } else {
            this.sid = sid.value();
        }

        this.did = reader.<String>read("did").valueOrDefault("");

        List cdata = reader.<List>read("cdata").valueOrDefault(new ArrayList());
        for (Object item : cdata) {
            Map<String, Object> m = (Map<String, Object>) item;
            this.cData.add(new CData(m));
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

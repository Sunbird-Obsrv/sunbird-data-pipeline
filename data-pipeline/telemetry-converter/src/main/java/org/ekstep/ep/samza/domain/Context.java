package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

import java.util.ArrayList;

public class Context {
    private String channel;
    private String env;
    private String sid;
    private String did;
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
            NullableValue<String> env = reader.read("edata.eks.env");
            if (!env.isNull()) {
                this.env = env.value();
            }
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

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public PData getpData() {
        return pData;
    }

    public void setpData(PData pData) {
        this.pData = pData;
    }

    public Rollup getRollUp() {
        return rollUp;
    }

    public void setRollUp(Rollup rollUp) {
        this.rollUp = rollUp;
    }

    public Iterable<CData> getcData() {
        return cData;
    }

    public void addCData(CData c) {
        cData.add(c);
    }
}

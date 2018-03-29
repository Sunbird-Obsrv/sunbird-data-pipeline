package org.ekstep.ep.samza.converter.domain;

import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

public class PData {
    private String MOBILE_PID = "ekstep.genie";
    private String PORTAL_PID = "ekstep_portal";
    private String SUNBIRD_PID = "sunbird-portal";

    private String id = "genie";
    private String pid = "genieservice.android";
    private String ver;

    public PData(Telemetry reader) throws TelemetryReaderException {
        NullableValue<String> id = reader.read("pdata.id");
        if (!id.isNull()) {
            this.id = id.value();
        }

        standardizePID(reader, id);

        NullableValue<String> ver = reader.read("pdata.ver");
        if (!ver.isNull()) {
            this.ver = ver.value();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public String getVer() {
        return ver;
    }

    private void standardizePID(Telemetry reader, NullableValue<String> id) {
        NullableValue<String> eid = reader.read("eid");
        if (!eid.isNull() && (eid.value().startsWith("GE_") || eid.value().startsWith("OE_"))){
            if(!id.isNull() && (id.value().equals("genie") || id.value().equals("in.ekstep"))){
                this.pid = MOBILE_PID;
            } else {
                NullableValue<String> pid = reader.read("pdata.id");
                this.pid = pid.value();
            }
        } else {
            if(id.isNull() || (!id.isNull() && (id.value().isEmpty() || id.value().equals("in.ekstep")))){
                this.pid = PORTAL_PID;
            } else {
                this.pid = SUNBIRD_PID;
            }
        }
    }
}

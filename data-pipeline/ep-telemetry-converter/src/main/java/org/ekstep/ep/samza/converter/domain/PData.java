package org.ekstep.ep.samza.converter.domain;

import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

public class PData {
    private String id = "genie";
    private String pid = "genieservice.android";
    private String ver;

    public PData(Telemetry reader) throws TelemetryReaderException {
        NullableValue<String> id = reader.read("pdata.id");
        if (!id.isNull()) {
            this.id = id.value();
        }

        NullableValue<String> pid = reader.read("pdata.pid");
        if (!pid.isNull()) {
            this.pid = pid.value();
        }

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
}

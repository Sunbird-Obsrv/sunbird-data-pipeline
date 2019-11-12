package org.ekstep.ep.samza.events.domain;


import com.google.gson.Gson;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.Map;

public abstract class Events {

    protected final Telemetry telemetry;

    private Path path;

    public Events(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);
        path = new Path();
    }

    public String getChecksum() {
        String checksum = id();
        if (checksum != null)
            return checksum;
        return mid();
    }

    public String id() {
        NullableValue<String> checksum = telemetry.read("metadata.checksum");
        return checksum.value();
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public String getJson() {
        Gson gson = new Gson();
        String json = gson.toJson(getMap());
        return json;
    }

    public String mid() {
        NullableValue<String> checksum = telemetry.read("mid");
        return checksum.value();
    }

    public String did() {
        NullableValue<String> did = telemetry.read("dimensions.did");
        return did.isNull() ? telemetry.<String>read("context.did").value() : did.value();
    }

    public String eid() {
        NullableValue<String> eid = telemetry.read("eid");
        return eid.value();
    }

    @Override
    public String toString() {
        return "Event{" + "telemetry=" + telemetry + '}';
    }


    public void updateTs(String value){
        telemetry.add("@timestamp",value);
    }

    public String pid() {
        NullableValue<String> pid = telemetry.read("context.pdata.pid");
        return pid.value();
    }

    public String version() {
        return (String) telemetry.read("ver").value();
    }


}

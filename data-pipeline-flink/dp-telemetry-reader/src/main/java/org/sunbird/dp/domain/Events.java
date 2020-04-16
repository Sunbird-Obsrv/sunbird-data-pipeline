package org.sunbird.dp.domain;


import com.google.gson.Gson;
import org.sunbird.dp.reader.NullableValue;
import org.sunbird.dp.reader.Telemetry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Events {

    protected Telemetry telemetry;
    protected Path path;

    public Events(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);
        this.path = new Path();
    }

    public Telemetry getTelemetry() {
        return telemetry;
    }

    public String kafkaKey() {
        return mid();
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
        return new Gson().toJson(getMap());
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

    public Map<String, Object> flags() {
        NullableValue<Map<String, Object>> eid = telemetry.read(path.flags());
        return eid.value();
    }

    @Override
    public String toString() {
        return "Event{" + "telemetry=" + telemetry + '}';
    }


    public void updateTs(String value) {
        telemetry.add("@timestamp", value);
    }

    public String pid() {
        NullableValue<String> pid = telemetry.read("context.pdata.pid");
        return pid.value();
    }

    public String version() {
        return (String) telemetry.read("ver").value();
    }

    public String producerId() {
        NullableValue<String> producerId = telemetry.read("context.pdata.id");
        return producerId.value();
    }

    public final String producerPid() {
        NullableValue<String> producerPid = telemetry.read("context.pdata.pid");
        return producerPid.value();
    }


    public Long ets() {
        NullableValue<Object> ets = telemetry.read("ets");
        if (ets.value().getClass().equals(Double.class)) {
            return ((Double) ets.value()).longValue();
        }
        return ((Long) ets.value());
    }

    public String channel() {
        NullableValue<String> channel = telemetry.read("dimensions.channel");
        return channel.isNull() ? telemetry.<String>read("context.channel").value() : channel.value();
    }

    public String actorId() {
        NullableValue<String> actorid = telemetry.read("uid");
        return actorid.isNull() ? telemetry.<String>read("actor.id").value() : actorid.value();
    }


    public String actorType() {
        NullableValue<String> actortype = telemetry.read("actor.type");
        return actortype.value();
    }

    public String objectID() {
        if (objectFieldsPresent()) {
            return telemetry.<String>read("object.id").value();
        } else return null;
    }

    public String objectType() {
        if (objectFieldsPresent()) {
            return telemetry.<String>read("object.type").value();
        } else return null;
    }

    public boolean objectFieldsPresent() {
        String objectId = telemetry.<String>read("object.id").value();
        String objectType = telemetry.<String>read("object.type").value();
        return null != objectId && null != objectType && !objectId.isEmpty() && !objectType.isEmpty();
    }

    public String edataType() {
        return telemetry.<String>read("edata.type").value();
    }

    public List<Map<String, Object>> edataItems() {
        return telemetry.<List<Map<String, Object>>>read("edata.items").value();
    }

    public void updateFlags(String key, Boolean value) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags."+ key, value);
    }

    public Map<String, Boolean> getFlags() {
        NullableValue<Map<String, Boolean>> flags = telemetry.read("flags");
        return flags.value();
    }

}

package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.events.domain.Events;

import java.util.HashMap;
import java.util.Map;

public class Event extends Events {

    public Event(Map<String, Object> map) {
        super(map);
    }

    public void markSuccess() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.ef_processed", true);
        telemetry.add("type", "events");
    }

    public void updateEventObjectKey(String objId, String type, String version, String rollupLevel1) {
        telemetry.addFieldIfAbsent("object", new HashMap<String, String>());
        telemetry.add("object.id", objId);
        telemetry.add("object.type", type);
        telemetry.add("object.version", version);
        telemetry.add("object.rollup.l1", rollupLevel1);
    }

    public void updatedEventEdata(String edataType, Long size) {
        telemetry.add("edata.type", edataType);
        telemetry.add("edata.size", size);
    }

    public void updateMid(String mid){
        telemetry.add("mid", mid);
    }

    public void removeItems() {
        telemetry.add("edata.items", null);
    }

    public void renameEventIdTo(String eid) {
        telemetry.add("eid", eid);
    }

}

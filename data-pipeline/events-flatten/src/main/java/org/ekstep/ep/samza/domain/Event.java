package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.events.domain.Events;
import org.ekstep.ep.samza.task.EventsFlattenConfig;

import java.util.HashMap;
import java.util.Map;

public class Event extends Events {

    public Event(Map<String, Object> map) {
        super(map);
    }

    public void markFailure(String error, EventsFlattenConfig config) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.ef_processed", false);
        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.ef_error", error);
        telemetry.add("metadata.src", config.jobName());
    }

    public void markSuccess() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.ef_processed", true);
        telemetry.add("type", "events");
    }

    public void updateEventObjectKey(String objId, String type, String version, String rollupLevel1) {
        telemetry.add("object.id", objId);
        telemetry.add("object.type", type);
        telemetry.add("object.version", version);
        telemetry.add("object.rollup.l1", rollupLevel1);
    }

    public void updatedEventEdata(String edataType, Double size){
        telemetry.add("edata.type", edataType);
        telemetry.add("edata.size", size);

    }

    public void removeItems(){
        telemetry.add("edata.items", null);
    }

    public  void renameEventIdTo(String eid){
        telemetry.add("eid", eid);
    }

}

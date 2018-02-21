package org.ekstep.ep.samza;

import org.ekstep.ep.samza.reader.Telemetry;

import java.util.List;
import java.util.Map;


public class Event {

    private final Telemetry telemetry;

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public Event(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);
    }

    public String eid() {
        return telemetry.<String>read("eid").value();
    }

    public String id() {
        return telemetry.<String>read("metadata.checksum").value();
    }

    public String ver() {
        return telemetry.<String>read("ver").value();
    }

    public String channel(){
        return telemetry.<String>read("context.channel").value();
    }

    public boolean isDefaultChannel(List<String> defaultChannels){
        for (String dChannel : defaultChannels) {
            if(channel() != null && channel().equals(dChannel)){
                return true;
            }
        }
        return false;
    }

    public boolean isVersionOne(){
        return ver() != null && ver().equals("1.0");
    }

    public Telemetry telemetry(){
        return this.telemetry;
    }
}

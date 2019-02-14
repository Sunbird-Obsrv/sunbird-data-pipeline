package org.ekstep.ep.samza.domain;


import com.google.gson.Gson;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    private final Telemetry telemetry;
    private Path path;

    public Event(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);
        path = new Path();
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public String getJson() {
        Gson gson = new Gson();
        String json = gson.toJson(getMap());
        return json;
    }

    public String did() {
        NullableValue<String> did = telemetry.read("dimensions.did");
        return did.isNull() ? telemetry.<String>read("context.did").value() : did.value();
    }

    public String channel() {
        NullableValue<String> channel = telemetry.read("dimensions.channel");
        return channel.isNull() ? telemetry.<String>read("context.channel").value() : channel.value();
    }

    public String objectID() {
        if (objectFieldsPresent()) {
            return telemetry.<String>read("object.id").value();
        }
        else return null;
    }

    public boolean objectFieldsPresent() {
        String objectId = telemetry.<String>read("object.id").value();
        String objectType = telemetry.<String>read("object.type").value();
        return objectId != null && objectType != null && !objectId.isEmpty() && !objectType.isEmpty();
    }

    public String actorId() {
        NullableValue<String> actorid = telemetry.read("uid");
        return actorid.isNull() ? telemetry.<String>read("actor.id").value() : actorid.value();
    }

    public String actorType() {
        NullableValue<String> actortype = telemetry.read("actor.type");
        return actortype.value();
    }

    public List<String> dialCode() {
        NullableValue<Object> dialcode = telemetry.read("edata.filters.dialcodes");
        List ids = new ArrayList();
        if (dialcode != null && dialcode.value() != null) {
            if (dialcode.value().getClass().equals(String.class)) {
                ids.add(dialcode.value().toString());
            }
            else {
                ids = ((List) dialcode.value());
            }
        }
        return ids;
    }

    public List<String> getKey(String type) {
        List<String> keyList = new ArrayList<String>();
        switch (type) {
            case "device":
                keyList.add(did());
                return keyList;
            case "user":
                if(actorType() != null && actorType().equalsIgnoreCase("system")) {
                    keyList.add("");
                    return keyList;
                }
                else {
                    keyList.add(actorId());
                    return keyList;
                }
            case "content":
                keyList.add(objectID());
                return keyList;
            case "dialcode":
                return dialCode();
            default:
                keyList.add("");
                return keyList;

        }
    }

    public void addMetaData(String type, List<Map> newData) {
        switch (type) {
            case "device":
                addDeviceData(newData.get(0));
                break;
            case "user":
                addUserData(newData.get(0));
                break;
            case "content":
                addContentData(newData.get(0));
                break;
            case "dialcode":
                addDialCodeData(newData);
                break;
            default:
                break;

        }
    }

    public Map addISOStateCodeToDeviceData(Map deviceData) {
        // add new statecode field
        String statecode = deviceData.get("statecode").toString();
        if(statecode != null && !statecode.isEmpty()) {
            String iso3166statecode = "IN-" + statecode;
            deviceData.put("iso3166statecode", iso3166statecode);
            return deviceData;
        }
        else return deviceData;
    }

    public void addDeviceData(Map newData) {
        NullableValue<Map<String, Object>> previousData = telemetry.read(path.deviceData());
        Map<String, Object> deviceData = previousData.isNull() ? new HashMap<>() : previousData.value();
        // add new statecode field
        if(!deviceData.isEmpty()) {
            deviceData = addISOStateCodeToDeviceData(deviceData);
        }
        deviceData.putAll(newData);
        telemetry.add(path.deviceData(), deviceData);
        setFlag(DeNormalizationConfig.getDeviceLocationJobFlag(), true);
    }

    public void addUserData(Map newData) {
        NullableValue<Map<String, Object>> previousData = telemetry.read(path.userData());
        Map<String, Object> userdata = previousData.isNull() ? new HashMap<>() : previousData.value();
        userdata.putAll(newData);
        telemetry.add(path.userData(), userdata);
        setFlag(DeNormalizationConfig.getUserLocationJobFlag(), true);
    }

    public void addContentData(Map newData) {
        NullableValue<Map<String, Object>> previousData = telemetry.read(path.contentData());
        Map<String, Object> contentData = previousData.isNull() ? new HashMap<>() : previousData.value();
        contentData.putAll(newData);
        telemetry.add(path.contentData(), contentData);
        setFlag(DeNormalizationConfig.getContentLocationJobFlag(), true);
    }

    public void addDialCodeData(List<Map> newData) {
        telemetry.add(path.dialCodeData(), newData);
        setFlag(DeNormalizationConfig.getDialCodeLocationJobFlag(), true);
    }

    public void setFlag(String key, Object value) {
        NullableValue<Map<String, Object>> telemetryFlag = telemetry.read(path.flags());
        Map<String, Object> flags = telemetryFlag.isNull() ? new HashMap<>() : telemetryFlag.value();
        if (!key.isEmpty()) flags.put(key, value);
        telemetry.add(path.flags(), flags);
    }

    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }

    public void markSkipped() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.denorm_processed", false);
        telemetry.add("flags.denorm_checksum_present", false);
    }

    public void markSuccess() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.denorm_processed", true);
    }

    public void markFailure(String error, DeNormalizationConfig config) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.denorm_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.denorm_error", error);
        telemetry.add("metadata.src", config.jobName());
    }

}


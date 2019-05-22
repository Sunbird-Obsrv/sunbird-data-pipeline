package org.ekstep.ep.samza.domain;


import com.google.gson.Gson;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.Path;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Event {
    private final Telemetry telemetry;
    private Path path;
    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

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

    public Long ets() {
        NullableValue<Object> ets = telemetry.read("ets");
        if(ets.value().getClass().equals(Double.class)) {
            return ((Double) ets.value()).longValue();
        }
        return ((Long) ets.value());
    }

    public Long getEndTimestampOfDay(String date) {
        Long ets = dateFormat.parseDateTime(date).plusHours(23).plusMinutes(59).plusSeconds(59).getMillis();
        return ets;
    }

    public Long compareAndAlterEts() {
        Long eventEts = ets();
        Long currentMillis = new Date().getTime();
        String currentDate = formatter.format(currentMillis);
        Long endTsOfCurrentDate = getEndTimestampOfDay(currentDate);
        if(eventEts > endTsOfCurrentDate) telemetry.add("ets", currentMillis);
        return ets();
    }

    public Boolean isOlder(Integer periodInMonths) {
        Long eventEts = ets();
        Long periodInMillis = new DateTime().minusMonths(periodInMonths).getMillis();
        if(eventEts < periodInMillis) return true;
        else return false;
    }

    public String eid() {
        NullableValue<String> eid = telemetry.read("eid");
        return eid.value();
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

    public String objectType() {
        if (objectFieldsPresent()) {
            return telemetry.<String>read("object.type").value();
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
        else {
            NullableValue<Object> dialCode = telemetry.read("edata.filters.dialCodes");
            if (dialCode != null && dialCode.value() != null) {
                telemetry.add("edata.filters.dialcodes", dialCode.value());
                telemetry.add("edata.filters.dialCodes", null);
                if (dialCode.value().getClass().equals(String.class)) {
                    ids.add(dialCode.value().toString());
                }
                else {
                    ids = ((List) dialCode.value());
                }
            }
        }
        return ids;
    }

    public String getKey(String type) {
        switch (type) {
            case "device":
                return did();
            case "user":
                if("system".equalsIgnoreCase(actorType())) {
                    return "";
                }
                else {
                    return actorId();
                }
            case "content":
                return objectID();
            default:
                return "";
        }
    }

    public void addMetaData(String type, Map newData) {
        switch (type) {
            case "device":
                addDeviceData(newData);
                break;
            case "user":
                addUserData(newData);
                break;
            case "content":
                addContentData(newData);
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

    public void addDialCodeData(Map newData) {
        telemetry.add(path.dialCodeData(), newData);
        setFlag(DeNormalizationConfig.getDialCodeLocationJobFlag(), true);
    }

    public void setFlag(String key, Object value) {
        NullableValue<Map<String, Object>> telemetryFlag = telemetry.read(path.flags());
        Map<String, Object> flags = telemetryFlag.isNull() ? new HashMap<>() : telemetryFlag.value();
        if (!key.isEmpty()) flags.put(key, value);
        telemetry.add(path.flags(), flags);
    }

    public Map<String, Object> getFlag() {
        NullableValue<Map<String, Object>> telemetryFlag = telemetry.read(path.flags());
        Map<String, Object> flags = telemetryFlag.isNull() ? new HashMap<>() : telemetryFlag.value();
        return flags;
    }

    public String getUpgradedVersion() {
        BigDecimal updatedVer = BigDecimal.valueOf(Double.parseDouble(telemetry.read(path.ver()).value().toString())).add(BigDecimal.valueOf(0.1));
        return updatedVer.toString();
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

    public String edataType() {
            return telemetry.<String>read("edata.type").value();
    }

}


package org.ekstep.ep.samza.domain;


import org.ekstep.ep.samza.events.domain.Events;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Event extends Events {
    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public Event(Map<String, Object> map) {
        super(map);
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
        if (eventEts > endTsOfCurrentDate) telemetry.add("ets", currentMillis);
        return ets();
    }

    public Boolean isOlder(Integer periodInMonths) {
        Long eventEts = ets();
        Long periodInMillis = new DateTime().minusMonths(periodInMonths).getMillis();
        if (eventEts < periodInMillis) return true;
        else return false;
    }


    public String objectRollUpl1ID() {
        if (objectFieldsPresent() && objectRollUpl1FieldsPresent()) {
            return telemetry.<String>read("object.rollup.l1").value();
        } else return null;
    }


    public boolean objectRollUpl1FieldsPresent() {
        if (objectFieldsPresent()) {
            String objectrollUpl1 = telemetry.<String>read("object.rollup.l1").value();
            return null != objectrollUpl1 && !objectrollUpl1.isEmpty();
        } else return false;
    }

    public boolean checkObjectIdNotEqualsRollUpl1Id() {
        if (objectRollUpl1FieldsPresent() && !(objectID().equals(objectRollUpl1ID())))
            return true;
        else
            return false;
    }

    public String getKey(String type) {
        switch (type) {
            case "device":
                return did();
            case "user":
                if ("system".equalsIgnoreCase(actorType())) {
                    return "";
                } else {
                    return actorId();
                }
            case "content":
                return objectID();
            case "collection":
                return objectRollUpl1ID();
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
            case "collection":
                addCollectionData(newData);
            default:
                break;

        }
    }

    public Map addISOStateCodeToDeviceData(Map deviceData) {
        // add new statecode field
        String statecode = deviceData.get("statecode").toString();
        if (statecode != null && !statecode.isEmpty()) {
            String iso3166statecode = "IN-" + statecode;
            deviceData.put("iso3166statecode", iso3166statecode);
            return deviceData;
        } else return deviceData;
    }

    public void addDeviceData(Map newData) {
        NullableValue<Map<String, Object>> previousData = telemetry.read(path.deviceData());
        Map<String, Object> deviceData = previousData.isNull() ? new HashMap<>() : previousData.value();
        // add new statecode field
        if (!deviceData.isEmpty()) {
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
        if (newData.size() > 2)
            setFlag(DeNormalizationConfig.getUserLocationJobFlag(), true);
        else
            setFlag(DeNormalizationConfig.getUserLocationJobFlag(), false);
    }

    public void addContentData(Map newData) {
        NullableValue<Map<String, Object>> previousData = telemetry.read(path.contentData());
        Map<String, Object> contentData = previousData.isNull() ? new HashMap<>() : previousData.value();
        contentData.putAll(newData);
        telemetry.add(path.contentData(), contentData);
        setFlag(DeNormalizationConfig.getContentLocationJobFlag(), true);
    }

    public void addCollectionData(Map newData) {
        NullableValue<Map<String, Object>> previousData = telemetry.read(path.collectionData());
        Map<String, Object> collectionData = previousData.isNull() ? new HashMap<>() : previousData.value();
        collectionData.putAll(newData);
        telemetry.add(path.collectionData(), collectionData);
        setFlag(DeNormalizationConfig.getCollectionLocationJobFlag(), true);
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

    public void markFailure(String error, DeNormalizationConfig config) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.denorm_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.denorm_error", error);
        telemetry.add("metadata.src", config.jobName());
    }


}


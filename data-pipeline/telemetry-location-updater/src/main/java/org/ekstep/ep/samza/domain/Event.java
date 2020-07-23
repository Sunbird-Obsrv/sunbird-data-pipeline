package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.events.domain.Events;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterConfig;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Event extends Events {

    private Gson gson = new Gson();
    private Type type = new TypeToken<Map<String, Object>>() {
    }.getType();

    public Event(Map<String, Object> map) {
        super(map);
    }

    public void markFailure(String error, TelemetryLocationUpdaterConfig config) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.tr_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.tr_error", error);
        telemetry.add("metadata.src", config.jobName());
    }

    public void addDeviceProfile(DeviceProfile deviceProfile) {
        Map<String, Object> ldata = new HashMap<>();

        ldata.put("countrycode", deviceProfile.getCountryCode());
        ldata.put("country", deviceProfile.getCountry());
        ldata.put("statecode", deviceProfile.getStateCode());
        ldata.put("state", deviceProfile.getState());
        ldata.put("city", deviceProfile.getCity());
        ldata.put("statecustomcode", deviceProfile.getstateCodeCustom());
        ldata.put("statecustomname", deviceProfile.getstateCustomName());
        ldata.put("districtcustom", deviceProfile.getDistrictCustom());
        ldata.put("devicespec", deviceProfile.getDevicespec());
        ldata.put("firstaccess", deviceProfile.getFirstaccess());
        String iso3166statecode = addISOStateCodeToDeviceProfile(deviceProfile);
        if (!iso3166statecode.isEmpty()) {
            ldata.put("iso3166statecode", iso3166statecode);
        }

        Map<String, String> userDeclared = new HashMap<>();
        userDeclared.put("state", deviceProfile.getUserDeclaredState());
        userDeclared.put("district", deviceProfile.getUserDeclaredDistrict());
        ldata.put("userdeclared", userDeclared);
        telemetry.add(path.deviceData(), ldata);
    }

    public void addDerivedLocation(Map<String, String> derivedData) {
        telemetry.add(path.derivedLocationData(), derivedData);
    }

    public String addISOStateCodeToDeviceProfile(DeviceProfile deviceProfile) {
        // add new statecode field
        String statecode = deviceProfile.getStateCode();
        if (statecode != null && !statecode.isEmpty()) {
            return "IN-" + statecode;
        } else return "";
    }

    public void removeEdataLoc() {
        Gson gson = new Gson();
        JsonObject json = gson.toJsonTree(getMap()).getAsJsonObject().get("edata").getAsJsonObject();
        json.remove("loc");
        telemetry.add(path.edata(), json);
    }

    public void setFlag(String key, Object value) {
        NullableValue<Map<String, Object>> telemetryFlag = telemetry.read(path.flags());
        Map<String, Object> flags = telemetryFlag.isNull() ? new HashMap<>() : telemetryFlag.value();
        flags.put(key, value);
        telemetry.add(path.flags(), flags);
    }
}

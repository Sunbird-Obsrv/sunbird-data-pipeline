package org.ekstep.ep.samza.domain;


import com.google.gson.Gson;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.ArrayList;
import java.util.Map;

public class Event {
    private final Telemetry telemetry;

    public Event(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public String getJson() {
        Gson gson = new Gson();
        String json = gson.toJson(getMap());
        return json;
    }

    public String userId() {
        NullableValue<String> userId = telemetry.read("actor.id");
        return userId.value();
    }

    public String objectUserId() {
        NullableValue<String> objectType = telemetry.read("object.type");
        if (null != objectType.value() && objectType.value().equalsIgnoreCase("User")) {
            NullableValue<String> objectUserId = telemetry.read("object.id");
            return objectUserId.value();
        }
        return null;
    }

    public String getUserSignInType() {
        NullableValue<ArrayList<Map<String, String>>> cdata = telemetry.read("context.cdata");
        ArrayList<Map<String, String>> cdataList = cdata.value();
        if (null != cdataList && !cdataList.isEmpty()) {
            for (Map<String, String> cdataMap : cdataList) {
                if (cdataMap.containsKey("type") && cdataMap.get("type").equalsIgnoreCase("SignupType"))
                    return cdataMap.get("id").toString();
            }

        }

        return null;
    }

    public String getUserLoginType() {
        NullableValue<ArrayList<Map<String, String>>> cdata = telemetry.read("context.cdata");
        ArrayList<Map<String, String>> cdataList = cdata.value();
        if (null != cdataList && !cdataList.isEmpty()) {
            for (Map<String, String> cdataMap : cdataList) {
                if (cdataMap.containsKey("type") && cdataMap.get("type").equalsIgnoreCase("UserRole"))
                    return cdataMap.get("id").toString();
            }
        }
        return null;
    }

    public ArrayList<String> getUserMetdataUpdatedList() {
        NullableValue<String> edata_state = telemetry.read("edata.state");
        if(edata_state.value().equalsIgnoreCase("Update")) {
            NullableValue<ArrayList<String>> edata_props = telemetry.read("edata.props");
            if(null != edata_props) {
                ArrayList<String> edata_props_list = edata_props.value();
                if(null != edata_props_list && !edata_props_list.isEmpty()) {
                    return edata_props_list;
                } else return null;
            } else return null;
        } else return null;
    }


    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }


}


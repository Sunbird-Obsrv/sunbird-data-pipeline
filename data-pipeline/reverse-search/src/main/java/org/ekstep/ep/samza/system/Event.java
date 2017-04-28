package org.ekstep.ep.samza.system;


import com.library.checksum.system.Mappable;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event implements Mappable {
    private final Telemetry telemetry;
    private Logger LOGGER = new Logger(this.getClass());

    public Event(Map<String, Object> map) {
        telemetry = new Telemetry(map);
    }

    public String getGPSCoordinates() {
        NullableValue<String> location = telemetry.<String>read("edata.eks.loc");
        return location.isNull() ? "" : location.value();
    }

    public void AddLocation(Location location) {
        Map<String, String> ldata = new HashMap<String, String>();
        ldata.put("locality", location.getCity());
        ldata.put("district", location.getDistrict());
        ldata.put("state", location.getState());
        ldata.put("country", location.getCountry());
        telemetry.add("ldata", ldata);
    }

    public String getDid() {
        NullableValue<String> did = telemetry.<String>read("dimensions.did");
        return (String) (did.isNull()
                ? telemetry.<String>read("did").value()
                : did.value());
    }

    public Map<String, Object> getMap() {
        return (Map<String, Object>) telemetry.getMap();
    }

    @Override
    public void setMetadata(Map<String, Object> metadata) {
        NullableValue<Map<String, Object>> metadataValue = telemetry.<Map<String, Object>>read("metadata");
        if (metadataValue.isNull()) {
            telemetry.add("metadata", metadata);
        } else {
            NullableValue<String> checksum = telemetry.<String>read("metadata.checksum");
            if (checksum.isNull())
                telemetry.add("metadata.checksum", metadata.get("checksum"));
        }
    }


    public void setFlag(String key, Object value) {
        NullableValue<Map<String, Object>> telemetryFlag = telemetry.<Map<String, Object>>read("flags");
        Map<String, Object> flags = telemetryFlag.isNull()
                ? new HashMap<String, Object>()
                : telemetryFlag.value();
        flags.put(key, value);
        telemetry.add("flags", flags);
    }

    public void setTimestamp() {
        Object ets1 = getMap().get("ets");
        LOGGER.info("", MessageFormat.format("ETS:{0}, type: {1}", ets1, ets1.getClass()));
        NullableValue<String> ts = telemetry.<String>read("ts");
        NullableValue<Long> ets = telemetry.<Long>read("ets");

        if (ts.isNull() && !ets.isNull()) {
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String updatedTs = simple.format(new Date(ets.value()));
            telemetry.add("ts", updatedTs);
        }
    }

    public String getMid() {
        NullableValue<String> mid = telemetry.<String>read("mid");
        return mid.value();
    }

    public String id() {
        NullableValue<String> checksum = telemetry.<String>read("metadata.checksum");
        return checksum.value();

    }

    public boolean isLocationEmpty() {
        NullableValue<String> location = telemetry.<String>read("edata.eks.loc");
        return !location.isNull() && location.value().isEmpty();
    }

    public boolean isLocationPresent() {
        NullableValue<String> location = telemetry.<String>read("edata.eks.loc");
        return !(location.isNull() || location.value().isEmpty());
    }

    public boolean isLocationAbsent() {
        NullableValue<String> location = telemetry.<String>read("edata.eks.loc");
        return location.isNull();
    }

    @Override
    public String toString() {
        return "event: " + telemetry.toString();
    }
}


package org.ekstep.ep.samza.system;


import com.library.checksum.system.ChecksumGenerator;
import com.library.checksum.system.KeysToAccept;
import com.library.checksum.system.Mappable;
import org.apache.commons.lang.StringUtils;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.util.Configuration;
import org.ekstep.ep.samza.util.Path;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event implements Mappable {
    private final Telemetry telemetry;
    private final Path path;
    private Logger LOGGER = new Logger(this.getClass());
    private ChecksumGenerator checksumGenerator;

    public Event(Map<String, Object> map) {
        telemetry = new Telemetry(map);
        String[] keys_to_accept = {"uid", "ts", "gdata", "edata"};
        checksumGenerator = new ChecksumGenerator(new KeysToAccept(keys_to_accept));
        path = new Path();
    }

    public String getGPSCoordinates() {
        NullableValue<String> location = telemetry.read(path.loc());
        return location.isNull() ? "" : location.value();
    }

    public void AddLocation(Location location) {
        Map<String, String> ldata = new HashMap<String, String>();
        ldata.put("locality", location.getCity());
        ldata.put("district", location.getDistrict());
        ldata.put("state", location.getState());
        ldata.put("country", location.getCountry());
        telemetry.add(path.ldata(), ldata);
    }

    public String getDid() {
        NullableValue<String> did = telemetry.read(path.dimensionsDid());
        return did.isNull()
                ? telemetry.<String>read(path.contextDid()).value()
                : did.value();
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    @Override
    public void setMetadata(Map<String, Object> metadata) {
        telemetry.addFieldIfAbsent(path.metadata(),metadata);
        telemetry.addFieldIfAbsent(path.checksum(),metadata.get("checksum"));
    }


    public void setFlag(String key, Object value) {
        NullableValue<Map<String, Object>> telemetryFlag = telemetry.read(path.flags());
        Map<String, Object> flags = telemetryFlag.isNull()
                ? new HashMap<String, Object>()
                : telemetryFlag.value();
        flags.put(key, value);
        telemetry.add(path.flags(), flags);
    }

    public void setTimestamp() {
        Object ets1 = getMap().get(path.ets());
        if(ets1 != null)
            LOGGER.info("", MessageFormat.format("Inside Event. ETS:{0}, type: {1}", ets1, ets1.getClass()));
        NullableValue<String> ts = telemetry.read(path.ts());
        Double ets = safelyParse(path.ets());

        if (ts.isNull() && ets != null) {
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String updatedTs = simple.format(new Date(ets.longValue()));
            telemetry.add(path.ts(), updatedTs);
        }
    }

    public String getMid() {
        NullableValue<String> mid = telemetry.read(path.mid());
        return mid.value();
    }

    private Double safelyParse(String etsField){
        try {
            NullableValue<Double> time = telemetry.read(etsField);
            return time.value();
        } catch (ClassCastException e) {
            NullableValue<Long> timeInLong = telemetry.read(etsField);
            return Double.valueOf(timeInLong.value());
        }
    }

    public String id() {
        NullableValue<String> checksum = telemetry.read(path.checksum());
        return checksum.value();

    }

    public boolean isLocationEmpty() {
        NullableValue<String> location = telemetry.read(path.loc());
        return !location.isNull() && location.value().isEmpty();
    }

    public boolean isLocationPresent() {
        NullableValue<String> location = telemetry.read(path.loc());
        return !(location.isNull() || location.value().isEmpty());
    }

    public boolean isLocationAbsent() {
        NullableValue<String> location = telemetry.read(path.loc());
        return location.isNull();
    }

    public void updateDefaults(Configuration configuration){
        updateChecksum();
        updateDefaultChannel(configuration);
    }

    private void updateDefaultChannel(Configuration configuration) {
        String channelString = telemetry.<String>read("context.channel").value();
        String channel = StringUtils.deleteWhitespace(channelString);
        if(channel == null || channel.isEmpty()) {
            telemetry.addFieldIfAbsent("context", new HashMap<String, Object>());
            telemetry.add("context.channel", configuration.getDefaultChannel());
        }
    }

    private void updateChecksum() {
        if (getMid() == null) {
            checksumGenerator.stampChecksum(this);
        } else {
            Map<String, Object> metadata = new HashMap<String, Object>();
            metadata.put("checksum", getMid());
            setMetadata(metadata);
        }
    }

    @Override
    public String toString() {
        return "event: " + telemetry.toString();
    }
}


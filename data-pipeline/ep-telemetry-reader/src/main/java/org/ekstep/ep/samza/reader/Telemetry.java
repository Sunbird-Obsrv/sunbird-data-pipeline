package org.ekstep.ep.samza.reader;


import org.ekstep.ep.samza.logger.Logger;
import java.util.HashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import static java.text.MessageFormat.format;

public class Telemetry {
    private Map<String, Object> map;
    private Logger logger = new Logger(this.getClass());

    public Telemetry(Map<String, Object> map) {
        this.map = map;
    }

    public boolean add(String keyPath, Object value) {
        try {
            ParentMap lastParent = lastParentMap(map, keyPath);
            lastParent.addChild(value);
            return true;
        } catch (Exception e) {
            logger.error("", format("Couldn't add value:{0} at  key: {0} for event:{1}", value, keyPath, map), e);
        }
        return false;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public <T> NullableValue<T> read(String keyPath) {
        try {
            ParentMap parentMap = lastParentMap(map, keyPath);
            return new NullableValue<T>(parentMap.<T>readChild());
        } catch (Exception e) {
            logger.error("", format("Couldn't get key: {0} from event:{1}", keyPath, map), e);
            return new NullableValue<T>(null);
        }
    }

    private ParentMap lastParentMap(Map<String, Object> map, String keyPath) {
        Map<String, Object> parentMap = map;
        String[] keys = keyPath.split("\\.");
        int lastIndex = keys.length - 1;
        if (keys.length > 1) {
            for (int i = 0; i < lastIndex && parentMap != null; i++)
                parentMap = new ParentMap(parentMap, keys[i]).readChild();
        }
        String lastKeyInPath = keys[lastIndex];
        return new ParentMap(parentMap, lastKeyInPath);
    }

    @Override
    public String toString() {
        return "Telemetry{" +
                "map=" + map +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Telemetry telemetry = (Telemetry) o;

        return map != null ? map.equals(telemetry.map) : telemetry.map == null;
    }

    @Override
    public int hashCode() {
        return map != null ? map.hashCode() : 0;
    }

    public String getUID() {
        return this.<String>read("uid").value();
    }

    public String id() {
        return this.<String>read("metadata.checksum").value();
    }

    public Date getTime() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String ts = this.<String>read("ts").value();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
        return simpleDateFormat.parse(ts);
    }

    public <T> void addFieldIfAbsent(String fieldName, T value) {
        if (read(fieldName).isNull()) {
            add(fieldName, value);
        }
    }
}

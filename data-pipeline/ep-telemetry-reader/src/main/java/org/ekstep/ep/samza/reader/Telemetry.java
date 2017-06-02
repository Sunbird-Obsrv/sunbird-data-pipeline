package org.ekstep.ep.samza.reader;


import org.ekstep.ep.samza.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
            ParentType lastParent = lastParentMap(map, keyPath);
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
            ParentType parentMap = lastParentMap(map, keyPath);
            return new NullableValue<T>(parentMap.<T>readChild());
        } catch (Exception e) {
            logger.error("", format("Couldn't get key: {0} from event:{1}", keyPath, map), e);
            return new NullableValue<T>(null);
        }
    }

    private ParentType lastParentMap(Map<String, Object> map, String keyPath) {
        Object parent = map;
        String[] keys = keyPath.split("\\.");
        int lastIndex = keys.length - 1;
        if (keys.length > 1) {
            for (int i = 0; i < lastIndex && parent != null; i++) {
                Object o;
                if (parent instanceof Map) {
                    o = new ParentMap((Map<String, Object>) parent, keys[i]).readChild();
                } else {
                    o = new ParentListOfMap((List<Map<String, Object>>) parent, keys[i]).readChild();
                }
                parent = o;
            }
        }
        String lastKeyInPath = keys[lastIndex];
        if (parent instanceof Map) {
            return new ParentMap((Map<String, Object>) parent, lastKeyInPath);
        } else {
            return new ParentListOfMap((List<Map<String, Object>>) parent, lastKeyInPath);
        }
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

package org.ekstep.ep.samza.model;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.validators.IValidator;
import org.ekstep.ep.samza.validators.ValidatorFactory;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Event {
    private final Map<String, Object> map;
    private Boolean isValid;
    private KeyValueStore<String, Child> childStore;
    private Child child;
    private long timeOfEventTicks;

    public Event(Map<String, Object> map, KeyValueStore<String, Child> childStore) {
        this.map = map;
        this.childStore = childStore;
        this.isValid = true;
    }

    public void initialize() {
        try {
            ArrayList<IValidator> validators = ValidatorFactory.validators(map);
            for (IValidator validator : validators)
                if (validator.isInvalid()) {
                    System.out.println(validator.getErrorMessage());
                    isValid = false;
                    return;
                }

            String uid = (String) map.get("uid");
            String timeOfEvent = (String) map.get("ts");
            Map<String, Object> udata = (Map<String, Object>) map.get("udata");
            Map<String, Boolean> flags = (Map<String, Boolean>) map.get("flags");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            timeOfEventTicks = simpleDateFormat.parse(timeOfEvent).getTime();
            child = childStore.get(uid);
            if (child == null){
                Boolean childProcessed = flags == null ? false : flags.get("child_data_processed");
                child = new Child(uid, childProcessed , timeOfEventTicks, udata);
            }
        } catch (ParseException e) {
            isValid = false;
            e.printStackTrace();
        }
    }

    public void process(ChildDto childDto) {
        if (!isValid) return;
        try {
            System.out.println("Processing event at ts:" + timeOfEventTicks);
            if (child.needsToBeProcessed()) {
                System.out.println("Processing child data, getting data from db");
                child = childDto.process(child);
            }
            update(child);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(Child child) {
        if (!isValid) return;
        map.put("udata", child.getData());
        Map<String, Boolean> flags = (Map<String, Boolean>) map.get("flags");
        if (flags == null)
            flags = new HashMap<String, Boolean>();
        flags.put("child_data_processed", child.isProcessed());
        map.put("flags", flags);
    }

    public Map<String, Object> getData() {
        return map;
    }

    public boolean isProcessed() {
        return child.isProcessed();
    }
}

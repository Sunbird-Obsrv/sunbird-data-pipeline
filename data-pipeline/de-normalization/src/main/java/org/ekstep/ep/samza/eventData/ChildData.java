package org.ekstep.ep.samza.eventData;

import org.ekstep.ep.samza.data.RetryData;
import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.Child;
import org.ekstep.ep.samza.external.UserService;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.HashMap;
import java.util.Map;

public class ChildData {
    private Child child;
    private Telemetry telemetry;
    private KeyValueStore<String, Child> childStore;
    private RetryData retryData;
    static Logger LOGGER = new Logger(ChildData.class);

    public ChildData(Telemetry telemetry, KeyValueStore<String, Child> childStore, RetryData retryData) {
        this.telemetry = telemetry;
        this.childStore = childStore;
        this.retryData = retryData;
    }

    private void update() {
        telemetry.add("udata", child.getData());
        NullableValue<Map<String, Boolean>> flags = telemetry.read("flags");
        if (flags.isNull())
            telemetry.add("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.child_data_processed", child.isProcessed());
    }

    public void initialize() {
        Map<String, Object> udata = telemetry.<Map<String, Object>>read("udata").value();
        Map<String, Boolean> flags = telemetry.<Map<String, Boolean>>read("flags").value();
        String uid = telemetry.<String>read("uid").value();
        child = childStore.get(uid);
        if (child == null) {
            Boolean childProcessed = flags == null || !flags.containsKey("child_data_processed") ? false : flags.get("child_data_processed");
            child = new Child(uid, childProcessed, udata);
        }
    }

    public void process(UserService userService) throws Exception {
        if (child.needsToBeProcessed()) {
            LOGGER.info(telemetry.id(), "PROCESSING - User Service CALL");
            child = userService.getUserFor(child, telemetry.getTime(), telemetry.id());
        }
        if (child.isProcessed()) {
            LOGGER.info(telemetry.id(), "PROCESSING - FOUND CHILD");
            update();
            retryData.removeMetadataFromStore();
        } else {
            LOGGER.info(telemetry.id(), "PROCESSING - CHILD NOT FOUND!");
            retryData.updateMetadataToStore();
        }
    }

    public Boolean isProcessed() {
        return child.isProcessed();
    }
}

package org.ekstep.ep.samza;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.external.UserService;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.validators.IValidator;
import org.ekstep.ep.samza.validators.ValidatorFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;

public class Event {
    private static final String TAG = "Event";
    static Logger LOGGER = new Logger(Event.class);
    private static final int RETRY_BACKOFF_BASE_DEFAULT = 10;
    private static final int RETRY_BACKOFF_LIMIT_DEFAULT = 4;
    private final Map<String, Object> map;
    private final List<String> backendEvents;
    private Boolean canBeProcessed;
    private KeyValueStore<String, Child> childStore;
    private Child child;
    private boolean hadIssueWithDb;
    private Date timeOfEvent;
    private int retryBackoffBase;
    private int retryBackoffLimit;
    private KeyValueStore<String, Object> retryStore;

    public Event(Map<String, Object> map, KeyValueStore<String, Child> childStore, List<String> backendEvents) {
        this.map = map;
        this.childStore = childStore;
        this.canBeProcessed = true;
        this.hadIssueWithDb = false;
        this.backendEvents = backendEvents;
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public void initialize(int retryBackoffBase, int retryBackoffLimit, KeyValueStore<String, Object> retryStore) {
        if (retryBackoffBase == 0)
            retryBackoffBase = RETRY_BACKOFF_BASE_DEFAULT;
        if (retryBackoffLimit == 0)
            retryBackoffLimit = RETRY_BACKOFF_LIMIT_DEFAULT;
        this.retryBackoffBase = retryBackoffBase;
        this.retryBackoffLimit = retryBackoffLimit;
        this.retryStore = retryStore;
        try {
            ArrayList<IValidator> validators = ValidatorFactory.validators(map);
            for (IValidator validator : validators)
                if (validator.isInvalid()) {
                    LOGGER.error(id(), validator.getErrorMessage());
                    canBeProcessed = false;
                    return;
                }

            String uid = getUID();
            String timeOfEventString = (String) map.get("ts");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            timeOfEvent = simpleDateFormat.parse(timeOfEventString);
            Map<String, Object> udata = (Map<String, Object>) map.get("udata");
            Map<String, Boolean> flags = (Map<String, Boolean>) map.get("flags");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            child = childStore.get(uid);
            if (child == null) {
                Boolean childProcessed = flags == null || !flags.containsKey("child_data_processed") ? false : flags.get("child_data_processed");
                child = new Child(uid, childProcessed, udata);
            }
        } catch (ParseException e) {
            canBeProcessed = false;
            LOGGER.error(id(), "EVENT INIT ERROR", e);
        }
    }

    public void process(UserService userService, DateTime now) {
        try {
            LOGGER.info(id(), "PROCESSING - START");
            if (!canBeProcessed) return;
            try {
                if (child.needsToBeProcessed()) {
                    LOGGER.info(id(), "PROCESSING - DB CALL");
                    child = userService.getUserFor(child, timeOfEvent, id());
                }
                if (child.isProcessed()) {
                    LOGGER.info(id(), "PROCESSING - FOUND CHILD");
                    update(child);
                    removeMetadataFromStore();
                } else {
                    LOGGER.info(id(), "PROCESSING - CHILD NOT FOUND!");
                    updateMetadataToStore();
                }
            } catch (Exception e) {
                hadIssueWithDb = true;
                LOGGER.error(id(), format("{0} ERROR WHEN GETTING CHILD #{1}", TAG, this.getMap()), e);
            }
            LOGGER.info(id(), "PROCESSING - STOP");
        } finally {
            addMetadata(now);
        }
    }

    private void update(Child child) {
        if (!canBeProcessed) return;
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
        return canBeProcessed && child.isProcessed();
    }

    public boolean canBeProcessed() {
        return canBeProcessed && !isBackendEvent();
    }

    public boolean hadIssueWithDb() {
        return hadIssueWithDb;
    }

    public void addMetadata(DateTime currentTime) {
        Map<String, Object> metadata = getMetadata();
        if (metadata != null) {
            setLastProcessedAt(currentTime);
            if (metadata.get("processed_count") == null)
                setLastProcessedCount(1);
            else {
                Integer count = (((Double) Double.parseDouble(String.valueOf(metadata.get("processed_count")))).intValue());
                count = count + 1;
                setLastProcessedCount(count);
            }
        } else {
            setLastProcessedAt(currentTime);
            setLastProcessedCount(1);
        }
        LOGGER.info(id(), "METADATA - ADDED " + metadata);

    }

    private void addMetadataToStore() {
        if (retryStore.get(getUID()) == null) {
            updateMetadataToStore();
            LOGGER.info(id(), "STORE - ADDED FOR " + getUID());
        }
    }

    private void updateMetadataToStore() {
        if (map.get("metadata") != null) {
            Map _map = new HashMap();
            _map.put("metadata", map.get("metadata"));
            retryStore.put(getUID(), _map);
            LOGGER.info(id(), "STORE - UPDATED " + _map + " UID " + getUID());
        }
    }

    private void removeMetadataFromStore() {
        retryStore.delete(getUID());
    }

    public boolean isSkipped() {
        LOGGER.info(id(), "CHECK - AT " + DateTime.now());
        DateTime nextProcessingTime = getNextProcessingTime(getLastProcessedTime());
        if (nextProcessingTime == null || nextProcessingTime.isBeforeNow()) {
            LOGGER.info(id(), "CHECK - PROCESSING " + map);
            return false;
        } else {
            LOGGER.info(id(), "CHECK - BACKING OFF " + map);
            addMetadataToStore();
            return true;
        }
    }

    public void setLastProcessedAt(DateTime time) {
        Map<String, Object> metadata = getMetadata();
        metadata.put("last_processed_at", time.toString());
    }

    public void setLastProcessedCount(int n) {
        Map<String, Object> metadata = getMetadata();
        metadata.put("processed_count", n);
    }

    public List backoffTimes(int attempts) {
        List backoffList = new ArrayList();
        DateTime thisTime = getLastProcessedTime();
        int processedCount;
        DateTime nextTime;
        for (int i = 0; i < attempts; i++) {
            nextTime = getNextProcessingTime(thisTime);
            processedCount = getProcessedCount();
            backoffList.add(nextTime);
            thisTime = nextTime;
            setLastProcessedAt(nextTime);
            setLastProcessedCount(processedCount + 1);
        }
        return backoffList;
    }

    private DateTime getNextProcessingTime(DateTime lastProcessedTime) {
        Integer nextBackoffInterval = getNextBackoffInterval();
        if (lastProcessedTime == null || nextBackoffInterval == null)
            return null;
        DateTime nextProcessingTime = lastProcessedTime.plusSeconds(nextBackoffInterval);
        LOGGER.info(id(), "nextProcessingTime: " + nextProcessingTime.toString());
        return nextProcessingTime;
    }

    private Integer getNextBackoffInterval() {
        Integer processedCount = getProcessedCount();
        if (processedCount == null)
            return null;
        return retryBackoffBase * (int) Math.pow(2, processedCount);
    }

    private Integer getProcessedCount() {
        Map metadata = getMetadata();
        if (metadata == null) {
            return null;
        } else {
            Integer processedCount = (Integer) metadata.get("processed_count");
            return processedCount;
        }
    }

    public DateTime getLastProcessedTime() {
        Map metadata = getMetadata();
        String lastProcessedAt = (String) metadata.get("last_processed_at");
        if (lastProcessedAt == null)
            return null;
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        DateTime dt = formatter.parseDateTime(lastProcessedAt);
        return dt;
    }

    private Map<String, Object> getMetadata() {
        String uid = getUID();
        Map retryData = (Map) retryStore.get(uid);
        Map metadata = null;
        Map _map;
        if (retryData != null) {
            _map = retryData;
        } else {
            _map = map;
        }
        if (_map != null)
            metadata = (Map<String, Object>) _map.get("metadata");
        if (metadata == null) {
            metadata = new HashMap<String, Object>();
            map.put("metadata", metadata);
            return metadata;
        }
        return metadata;
    }

    private String getUID() {
        return (String) map.get("uid");
    }

    public String getEID() {
        return map != null && map.containsKey("eid") ? (String) map.get("eid") : null;
    }

    public String id() {
        return map != null && map.containsKey("metadata") &&
            (((Map<String, Object>) map.get("metadata")).containsKey("checksum"))
            ? (String) ((Map<String, Object>) map.get("metadata")).get("checksum")
            : null;
    }

    public void addLastSkippedAt(DateTime currentTime) {
        Map<String, Object> metadata = (Map<String, Object>) map.get("metadata");
        if (metadata != null) {
            metadata.put("last_skipped_at",currentTime.toString());
        } else {
            metadata = new HashMap<String, Object>();
            metadata.put("last_skipped_at",currentTime.toString());
            map.put("metadata", metadata);
        }
        LOGGER.info(id(), "METADATA LAST SKIPPED AT - ADDED " + metadata);
    }

    public boolean isBackendEvent() {
        for (String events : backendEvents) {
            Pattern p = Pattern.compile(events);
            Matcher m = p.matcher(getEID());
            if (m.matches()) {
                LOGGER.info(m.toString(), "FOUND BACKEND EVENT");
                return true;
            }
        }
        return false;
    }

    public void setBackendTrue() {
        LOGGER.info(id(), "ADDING BACKEND EVENT TYPE");
        map.put("backend","true");
    }
}

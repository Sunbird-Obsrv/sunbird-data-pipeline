package org.ekstep.ep.samza;

import org.apache.commons.collections.map.HashedMap;
import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.validators.IValidator;
import org.ekstep.ep.samza.validators.UidValidator;
import org.ekstep.ep.samza.validators.ValidatorFactory;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Event {
    private static final Object ACTOR = "DENORMALIZER";
    private static final Long BACKOFF_CAP = 60*60*24*15L;
    private static final int BASE = 10;
    private static final int RETRY_BACKOFF_BASE_DEFAULT = 10;
    private static final int RETRY_BACKOFF_LIMIT_DEFAULT = 4;
    private final Map<String, Object> map;
    private Boolean canBeProcessed;
    private KeyValueStore<String, Child> childStore;
    private Child child;
    private boolean hadIssueWithDb;
    private Date timeOfEvent;
    private int retryBackoffBase;
    private int retryBackoffLimit;

    public Event(Map<String, Object> map, KeyValueStore<String, Child> childStore) {
        this.map = map;
        this.childStore = childStore;
        this.canBeProcessed = true;
        this.hadIssueWithDb = false;
    }

    public Map<String,Object> getMap(){
        return (Map<String,Object>) this.map;
    }

    public void initialize(int retryBackoffBase, int retryBackoffLimit) {
        if(retryBackoffBase==0)
            retryBackoffBase  = RETRY_BACKOFF_BASE_DEFAULT;
        if(retryBackoffLimit==0)
            retryBackoffLimit = RETRY_BACKOFF_LIMIT_DEFAULT;
        this.retryBackoffBase = retryBackoffBase;
        this.retryBackoffLimit = retryBackoffLimit;
        try {
            ArrayList<IValidator> validators = ValidatorFactory.validators(map);
            for (IValidator validator : validators)
                if (validator.isInvalid()) {
                    System.out.println(validator.getErrorMessage());
                    canBeProcessed = false;
                    return;
                }

            String uid = (String) map.get("uid");
            String timeOfEventString = (String) map.get("ts");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            timeOfEvent = simpleDateFormat.parse(timeOfEventString);
            Map<String, Object> udata = (Map<String, Object>) map.get("udata");
            Map<String, Boolean> flags = (Map<String, Boolean>) map.get("flags");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            child = childStore.get(uid);
            if (child == null){
                Boolean childProcessed = flags == null || !flags.containsKey("child_data_processed") ? false : flags.get("child_data_processed");
                child = new Child(uid, childProcessed ,udata);
            }
        } catch (ParseException e) {
            canBeProcessed = false;
            e.printStackTrace();
        }
    }

    public void process(ChildDto childDto) {
        if (!canBeProcessed) return;
        try {
            System.out.println("Processing event at ts:" + timeOfEvent.getTime());
            if (child.needsToBeProcessed()) {
                System.out.println("Processing child data, getting data from db");
                child = childDto.process(child,timeOfEvent);
            }
            if(child.isProcessed())
                update(child);
        } catch (SQLException e) {
            hadIssueWithDb = true;
            e.printStackTrace();
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

    public boolean canBeProcessed(){
        return canBeProcessed;
    }

    public boolean isChildDataProcessed(){
        return child.isProcessed();
    }

    public boolean hadIssueWithDb() {
        return hadIssueWithDb;
    }

    public void addMetadata() {
        Map<String, Object> metadata = getMetadata();
        DateTime currentTime = new DateTime();
        if(metadata != null){
            setLastProcessedAt(currentTime);
            if(metadata.get("processed_count") == null)
                setLastProcessedCount(1);
            else {
                Integer count = (((Double) Double.parseDouble(String.valueOf(metadata.get("processed_count")))).intValue());
                count = count + 1;
                setLastProcessedCount(count);
            }
        }
        else{
            setLastProcessedAt(currentTime);
            setLastProcessedCount(1);
        }
    }

    public boolean isSkipped() {
        DateTime nextProcessingTime = getNextProcessingTime(getLastProcessedTime());
        if(nextProcessingTime==null||nextProcessingTime.isBeforeNow())
            return false;
        else
            return true;
    }

    public void setLastProcessedAt(DateTime time){
        Map<String, Object> metadata = getMetadata();
        metadata.put("last_processed_at",time.toString());
    }

    public void setLastProcessedCount(int n){
        Map<String, Object> metadata = getMetadata();
        metadata.put("processed_count",n);
    }

    public List backoffTimes(int attempts){
        List backoffList = new ArrayList();
        DateTime thisTime = getLastProcessedTime();
        int processedCount;
        DateTime nextTime;
        for(int i=0;i<attempts;i++){
            nextTime = getNextProcessingTime(thisTime);
            processedCount = getProcessedCount();
            backoffList.add(nextTime);
            thisTime = nextTime;
            setLastProcessedAt(nextTime);
            setLastProcessedCount(processedCount+1);
        }
        return backoffList;
    }

    private DateTime getNextProcessingTime(DateTime lastProcessedTime){
        Integer nextBackoffInterval = getNextBackoffInterval();
        if(lastProcessedTime==null||nextBackoffInterval==null)
            return null;
        return lastProcessedTime.plusSeconds(nextBackoffInterval);
    }

    private Integer getNextBackoffInterval() {
        Integer processedCount = getProcessedCount();
        if(processedCount==null)
            return null;
        return retryBackoffBase*(int)Math.pow(2,processedCount);
    }

    private Integer getProcessedCount(){
        Map metadata = getMetadata();
        if(metadata==null){
            return null;
        } else {
            Integer processedCount = (Integer)metadata.get("processed_count");
            return processedCount;
        }
    }

    public DateTime getLastProcessedTime(){
        Map metadata = getMetadata();
        String lastProcessedAt = (String)metadata.get("last_processed_at");
        if(lastProcessedAt==null)
            return null;
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        DateTime dt = formatter.parseDateTime(lastProcessedAt);
        return dt;
    }

    private Map<String, Object> getMetadata() {
        Map metadata = (Map<String, Object>) map.get("metadata");
        if(metadata==null){
            metadata = new HashMap<String, Object>();
            map.put("metadata",metadata);
            return getMetadata();
        }
        return metadata;
    }
}

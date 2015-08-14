package org.ekstep.ep.samza.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Child implements Serializable {
    public static final String UNAME = "uname";
    public static final String UEKSTEP_ID = "uekstep_id";
    public static final String GENDER = "gender";
    public static final String AGE_COMPLETED_YEARS = "age_completed_years";
    public static final String DOB = "dob";
    public static final String AGE = "age";
    private String uid;
    private Boolean child_data_processed;
    private long age;
    private String dob;
    private int age_completed_years;
    private String gender;
    private String uName;
    private String uEkStepId;
    private long timeOfReference;

    public Child(String uid, Boolean child_data_processed, long timeOfReference, Map<String, Object> udata) {
        this.uid = uid;
        this.child_data_processed = child_data_processed == null ? false : child_data_processed;
        this.timeOfReference = timeOfReference;
        initialize(udata);
    }

    private void initialize(Map<String, Object> udata) {
        if(udata == null) return;
        this.age = ((Integer) udata.get(AGE));
        this.dob = ((String) udata.get(DOB));
        this.age_completed_years = ((Integer) udata.get(AGE_COMPLETED_YEARS));
        this.gender = ((String) udata.get(GENDER));
        this.uName = ((String) udata.get(UNAME));
        this.uEkStepId = ((String) udata.get(UEKSTEP_ID));
    }

    public Boolean isProcessed(){
        return child_data_processed;
    }

    public Boolean needsToBeProcessed(){
        return !isProcessed() && uid != null && !uid.isEmpty();
    }

    public HashMap<String, Object> getData() {
        HashMap<String, Object> udata = new HashMap<String, Object>();
        udata.put(UNAME, this.uName);
        udata.put(DOB, this.dob);
        udata.put(AGE, this.age);
        udata.put(AGE_COMPLETED_YEARS, this.age_completed_years);
        udata.put(GENDER, this.gender);
        udata.put(UEKSTEP_ID, this.uEkStepId);
        return udata;
    }

    public void populate(HashMap<String, Object> childData) {
        System.out.println("trying to read from database");
        String name = (String)childData.get("name");
        String gender = (String)childData.get(GENDER);
        String ekstep_id = (String)childData.get("ekstep_id");
        Timestamp dob = (Timestamp)childData.get(DOB);
        long dobTicksInSeconds = dob.getTime()/1000;
        long secondsInYear = 31556952;
        this.age = timeOfReference - dobTicksInSeconds;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
        this.dob = simpleDateFormat.format(dob);
        this.age_completed_years = (int) (age/secondsInYear);
        this.uName = name;
        this.gender = gender;
        this.uEkStepId = ekstep_id;
        this.child_data_processed = true;
        System.out.println("successfully read from db");
    }

    public String getUid() {
        return uid;
    }
}

package org.ekstep.ep.samza;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class Child implements Serializable {
    public static final String HANDLE = "handle";
    public static final String STANDARD = "standard";
    public static final String GENDER = "gender";
    public static final String AGE_COMPLETED_YEARS = "age_completed_years";
    private final String IS_GROUP_USER = "is_group_user";
    private String uid;
    private Boolean child_data_processed;
    private int age_completed_years;
    private String gender;
    private String handle;
    private Integer standard;
    private Boolean isGroupUser;

    public Child(String uid, Boolean child_data_processed, Map<String, Object> udata) {
        this.uid = uid;
        this.child_data_processed = child_data_processed == null ? false : child_data_processed;
        initialize(udata);
    }

    private void initialize(Map<String, Object> udata) {
        if(udata == null) return;
        age_completed_years = ((Integer) udata.get(AGE_COMPLETED_YEARS));
        gender = ((String) udata.get(GENDER));
        handle = ((String) udata.get(HANDLE));
        standard = ((Integer) udata.get(STANDARD));
        isGroupUser = ((Boolean) udata.get(IS_GROUP_USER));
    }

    public Boolean isProcessed(){
        return child_data_processed;
    }

    public Boolean needsToBeProcessed(){
        return !isProcessed() && uid != null && !uid.isEmpty();
    }

    public HashMap<String, Object> getData() {
        HashMap<String, Object> udata = new HashMap<String, Object>();
        udata.put(HANDLE, handle);
        udata.put(STANDARD,standard);
        udata.put(AGE_COMPLETED_YEARS, age_completed_years);
        udata.put(GENDER, gender);
        udata.put(IS_GROUP_USER, isGroupUser);
        return udata;
    }

    public void populate(HashMap<String, Object> childData, Date timeOfEvent) {
        if(childData == null || childData.isEmpty()){
            System.err.println("No record in the database, skipping the record");
            return;
        }
        System.out.println("trying to read from database");
        populateAgeRelatedFields(childData, timeOfEvent);
        this.handle = (String) childData.get(HANDLE);
        this.standard = (Integer) childData.get(STANDARD);
        this.gender = (String) childData.get(GENDER);
        this.isGroupUser = ((Boolean) childData.get(IS_GROUP_USER));
        this.child_data_processed = true;
        System.out.println("successfully read from db");
    }

    private void populateAgeRelatedFields(HashMap<String, Object> childData, Date timeOfEvent) {
        Integer year_of_birth = (Integer) childData.get("year_of_birth");
        if(year_of_birth == null || year_of_birth <= 0){
            System.err.println("No Age for the children, skipping all age related fields");
            return;
        }
        Calendar timeOfEventFromCalendar = Calendar.getInstance();
        timeOfEventFromCalendar.setTime(timeOfEvent);
        this.age_completed_years = timeOfEventFromCalendar.get(Calendar.YEAR) - year_of_birth;
    }

    public String getUid() {
        return uid;
    }
}

package org.ekstep.ep.samza;

import org.ekstep.ep.samza.logger.Logger;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Child implements Serializable {
    private static final String TAG = Child.class.getSimpleName();
    static Logger LOGGER = new Logger(Child.class);

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
        if (udata == null) return;
        age_completed_years = ((Integer) udata.get(AGE_COMPLETED_YEARS));
        gender = ((String) udata.get(GENDER));
        handle = ((String) udata.get(HANDLE));
        standard = ((Integer) udata.get(STANDARD));
        isGroupUser = ((Boolean) udata.get(IS_GROUP_USER));
    }

    public Boolean isProcessed() {
        return child_data_processed;
    }

    public Boolean needsToBeProcessed() {
        return !isProcessed() && uid != null && !uid.isEmpty();
    }

    public HashMap<String, Object> getData() {
        HashMap<String, Object> udata = new HashMap<String, Object>();
        udata.put(HANDLE, handle);
        udata.put(STANDARD, standard);
        udata.put(AGE_COMPLETED_YEARS, age_completed_years);
        udata.put(GENDER, gender);
        udata.put(IS_GROUP_USER, isGroupUser);
        return udata;
    }

    public void populate(HashMap<String, Object> childData, Date timeOfEvent) {
        if (childData == null || childData.isEmpty()) {
            LOGGER.error(TAG + " NO RECORD IN THE DATABASE");
            return;
        }
        LOGGER.info(TAG + " TRYING TO READ FROM DATABASE");
        populateAgeRelatedFields(childData, timeOfEvent);
        this.handle = (String) childData.get(HANDLE);
        this.standard = (Integer) childData.get(STANDARD);
        this.gender = (String) childData.get(GENDER);
        this.isGroupUser = ((Boolean) childData.get(IS_GROUP_USER));
        this.child_data_processed = true;
        LOGGER.info(TAG + " SUCCESSFULLY READ FROM DB");
    }

    private void populateAgeRelatedFields(HashMap<String, Object> childData, Date timeOfEvent) {
        Integer year_of_birth = (Integer) childData.get("year_of_birth");
        if (year_of_birth == null || year_of_birth <= 0) {
            LOGGER.error(TAG + " NO AGE FOR THE CHILDREN");
            return;
        }
        Calendar timeOfEventFromCalendar = Calendar.getInstance();
        timeOfEventFromCalendar.setTime(timeOfEvent);
        this.age_completed_years = timeOfEventFromCalendar.get(Calendar.YEAR) - year_of_birth;
    }

    public String getUid() {
        return uid;
    }

    public void setAsProcessed() {
        this.child_data_processed = true;
    }
}

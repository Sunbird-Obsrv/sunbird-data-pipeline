package org.ekstep.ep.samza.model;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Child {
    private String uid;
    private Boolean child_data_processed;
    private long age;
    private String dob;
    private int age_completed_years;
    private String gender;
    private String uName;
    private String uEkStepId;
    private long timeOfReference;

    public Child(String uid, Boolean child_data_processed, long timeOfReference) {
        this.uid = uid;
        this.child_data_processed = child_data_processed;
        this.timeOfReference = timeOfReference;
    }

    public void populate(Map<String, Object> udata) {
        this.age = Long.parseLong(((String) udata.get("age")));
        this.dob = ((String) udata.get("dob"));
        this.age_completed_years = (Integer.parseInt(((String) udata.get("age_completed_years"))));
        this.gender = ((String) udata.get("gender"));
        this.uName = ((String) udata.get("uname"));
        this.uEkStepId = ((String) udata.get("uekstep_id"));
    }

    public Boolean isProcessed(){
        return child_data_processed;
    }

    public Boolean canBeProcessed(){
        return uid!= null && !uid.isEmpty();
    }

    public HashMap<String, Object> getData() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        HashMap<String, Object> udata = new HashMap<String, Object>();
        udata.put("uname", this.uName);
        udata.put("dob", this.dob);
        udata.put("age", this.age);
        udata.put("age_completed_years", this.age_completed_years);
        udata.put("gender", this.gender);
        udata.put("uekstep_id", this.uEkStepId);
        map.put("udata", udata);
        return map;
    }

    public void update(Database dataSource){

        String query = String.format("select * from child where encoded_id = %s", uid);
        ResultSet childData = dataSource.get(query);
        if(childData != null)
            populate(childData);
    }

    private void populate(ResultSet childData) {
        try {
            String name = childData.getString("name");
            String gender = childData.getString("gender");
            String ekstep_id = childData.getString("ekstep_id");
            Timestamp dob = childData.getTimestamp("dob");
            long dobTicks = dob.getTime();
            long secondsInYear = 31556952;
            this.age = timeOfReference - dobTicks;
            this.dob = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(dob);
            this.age_completed_years = (int) (age/secondsInYear);
            this.uName = name;
            this.gender = gender;
            this.uEkStepId = ekstep_id;
            this.child_data_processed = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUid() {
        return uid;
    }
}

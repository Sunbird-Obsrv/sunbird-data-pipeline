package org.ekstep.ep.samza.model;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class UpdateProfileDto implements IModel{
    private String UID;
    private String GENDER;
    private Integer YEAR_OF_BIRTH;
    private Integer AGE;
    private Integer STANDARD;
    private String LANGUAGE;
    private Timestamp UPDATED_AT;

    private java.util.Date date = new java.util.Date();

    private boolean isInserted = false;

    private DataSource dataSource;

    public UpdateProfileDto(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void process(Event event) throws SQLException, ParseException {
        Map<String,Object> EKS = (Map<String,Object>) event.getEks();


        if(!isLearnerExist((String) EKS.get("uid"))){
            createLearner(event);
        }

        if(!isProfileExist((String) EKS.get("uid"))){
            createProfile(event);
        }

        parseData(EKS);
        saveData();

    }

    private void parseData(Map<String, Object> EKS) throws ParseException {
        UID = (String) EKS.get("uid");
        if(UID == null) throw new ParseException("UID can't be blank",1);

        GENDER = (String) EKS.get("gender");
        AGE = getAge(EKS);
        YEAR_OF_BIRTH = (Integer) getYear(((Double) EKS.get("age")).intValue());
        LANGUAGE = (String) EKS.get("language");
        STANDARD = getStandard(EKS);

        UPDATED_AT = (Timestamp) new Timestamp(date.getTime());
    }

    private Integer getAge(Map<String, Object> EKS) {
        Integer age = ((Double) EKS.get("age")).intValue();
        if(age != -1){
            return age;
        }
        return null;
    }

    private Integer getStandard(Map<String, Object> EKS) {
        Integer standard = ((Double) EKS.get("standard")).intValue();
        if(standard != -1){
            return standard;
        }
        return null;
    }

    public void saveData() throws SQLException, ParseException {
        PreparedStatement preparedStmt = null;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            String updateQuery = "update profile set year_of_birth = ?, gender = ?, age = ?, standard = ?, language = ?, updated_at = ?"
                    + "where uid = ?";

            preparedStmt = connection.prepareStatement(updateQuery);


            if(AGE != null) {
                preparedStmt.setInt(1, YEAR_OF_BIRTH);
                preparedStmt.setInt(3, AGE);
            }
            else {
                preparedStmt.setNull(1, java.sql.Types.INTEGER);
                preparedStmt.setNull(3, java.sql.Types.INTEGER);
            }
            preparedStmt.setString(2, GENDER);


            if(STANDARD != null)
                preparedStmt.setInt(4, STANDARD);
            else
                preparedStmt.setNull(4, java.sql.Types.INTEGER);

            preparedStmt.setString(5,LANGUAGE);
            preparedStmt.setTimestamp(6, UPDATED_AT);
            preparedStmt.setString(7, UID);


            int affectedRows = preparedStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating Profile failed, no rows affected.");
            }
            else {
                this.setIsInserted();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(preparedStmt!=null)
                preparedStmt.close();
            if(connection!=null)
                connection.close();
        }
    }

    private void createLearner(Event event) throws SQLException, ParseException {
        CreateLearnerDto learnerDto = new CreateLearnerDto(dataSource);
        learnerDto.process(event);
    }

    private void createProfile(Event event) throws SQLException, ParseException {
        CreateProfileDto profileDto = new CreateProfileDto(dataSource);
        profileDto.process(event);
    }

    private boolean isLearnerExist(String uid) throws SQLException {
        boolean flag = false;
        PreparedStatement preparedStmt = null;
        Connection connection = null;
        connection = dataSource.getConnection();
        ResultSet resultSet = null;

        try{
            String query = "select uid from learner where uid = ?";
            preparedStmt = connection.prepareStatement(query);
            preparedStmt.setString(1, uid);

            resultSet = preparedStmt.executeQuery();

            if(resultSet.first()){
                flag = true;
            }

        } finally {
            if(preparedStmt!=null)
                preparedStmt.close();
            if(connection!=null)
                connection.close();
        }
        return flag;
    }

    public boolean isProfileExist(String uid) throws SQLException {
        boolean flag = false;
        PreparedStatement preparedStmt = null;
        Connection connection = null;
        connection = dataSource.getConnection();
        ResultSet resultSet = null;

        try{
            String query = "select uid from profile where uid = ?";
            preparedStmt = connection.prepareStatement(query);
            preparedStmt.setString(1, uid);

            resultSet = preparedStmt.executeQuery();

            if(resultSet.first()){
                flag = true;
            }

        } finally {
            if(preparedStmt!=null)
                preparedStmt.close();
            if(connection!=null)
                connection.close();
        }
        return flag;
    }

    private Integer getYear(Integer age) throws ParseException {
        Calendar dob = new GregorianCalendar();
        if(age != -1){
            dob.add((Calendar.YEAR),- age);
            return dob.getWeekYear();
        }
        return null;
    }

    @Override
    public boolean canProcessEvent(String eid){

        return (eid.equals("GE_UPDATE_PROFILE"));
    }

    @Override
    public void setIsInserted(){
        this.isInserted = true;
    }

    @Override
    public boolean getIsInserted(){
        return this.isInserted;
    }

}

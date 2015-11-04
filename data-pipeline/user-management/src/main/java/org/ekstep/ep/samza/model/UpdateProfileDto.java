package org.ekstep.ep.samza.model;

import javax.sql.DataSource;
import java.io.PrintStream;
import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class UpdateProfileDto implements IModel{
    public static final String UID = "uid";
    public static final String HANDLE = "handle";
    public static final String GENDER = "gender";
    public static final String LANGUAGE = "language";
    public static final String AGE = "age";
    public static final String STANDARD = "standard";
    private String uid;
    private String gender;
    private Integer yearOfBirth;
    private Integer age;
    private Integer standard;
    private String language;
    private String handle;
    private Timestamp updatedAt;

    private java.util.Date date = new java.util.Date();

    private boolean isInserted = false;

    private DataSource dataSource;

    public UpdateProfileDto(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void process(Event event) throws SQLException, ParseException {
        Map<String,Object> EKS = (Map<String,Object>) event.getEks();

        parseData(EKS);

        if(!isProfileExist((String) EKS.get("uid"))){
            createProfile(event);
        }

        saveData();
    }

    private void parseData(Map<String, Object> EKS) throws ParseException {
        uid = (String) EKS.get(UID);
        validateEmptyString(UID,uid);

        handle = (String) EKS.get(HANDLE);
        validateEmptyString(HANDLE,handle);

        gender = (String) EKS.get(GENDER);
        age = getAge(EKS);

        if(age!=null) {
            yearOfBirth = (Integer) getYear(getAge(EKS));
        }

        language = (String) EKS.get(LANGUAGE);
        standard = getStandard(EKS);

        updatedAt = (Timestamp) new Timestamp(date.getTime());
    }

    private void validateEmptyString(String name,String value) throws ParseException {
        if(value == null || value.isEmpty()) throw new ParseException(String.format("%s can't be blank",name),1);
    }

    private Integer getAge(Map<String, Object> EKS) {
        try {
            Integer age = ((Double) EKS.get(AGE)).intValue();
            if (age != -1) {
                return age;
            }
        }catch(Exception e){

        }
        return null;
    }

    private Integer getStandard(Map<String, Object> EKS) {
        Integer standard = ((Double) EKS.get(STANDARD)).intValue();
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
            String updateQuery = "update profile set year_of_birth = ?, gender = ?, age = ?, standard = ?, language = ?, updated_at = ?, handle = ?"
                    + "where uid = ?";

            preparedStmt = connection.prepareStatement(updateQuery);

            if(age != null) {
                preparedStmt.setInt(1, yearOfBirth);
                preparedStmt.setInt(3, age);
            }
            else {
                preparedStmt.setNull(1, java.sql.Types.INTEGER);
                preparedStmt.setNull(3, java.sql.Types.INTEGER);
            }

            preparedStmt.setString(2, gender);

            if(standard != null)
                preparedStmt.setInt(4, standard);
            else
                preparedStmt.setNull(4, java.sql.Types.INTEGER);

            preparedStmt.setString(5, language);
            preparedStmt.setTimestamp(6, updatedAt);

            preparedStmt.setString(7, handle);

            preparedStmt.setString(8, uid);


            int affectedRows = preparedStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating Profile failed, no rows affected.");
            }
            else {
                this.setIsInserted();
            }

        } catch (Exception e) {
            System.err.println("Exception: " + e);
            e.printStackTrace(new PrintStream(System.err));
        } finally {
            if(preparedStmt!=null)
                preparedStmt.close();
            if(connection!=null)
                connection.close();
        }
    }

    private void createProfile(Event event) throws SQLException, ParseException {
        CreateProfileDto profileDto = new CreateProfileDto(dataSource);
        profileDto.process(event);
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

        } catch (Exception e) {
            System.err.println("Exception: " + e);
            e.printStackTrace(new PrintStream(System.err));
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
    public void setIsInserted(){
        this.isInserted = true;
    }

    @Override
    public boolean getIsInserted(){
        return this.isInserted;
    }

}

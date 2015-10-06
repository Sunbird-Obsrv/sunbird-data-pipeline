package org.ekstep.ep.samza.model;

import javax.sql.DataSource;
import java.io.PrintStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class CreateProfileDto implements IModel{
    private String UID;
    private String HANDLE;
    private String GENDER;
    private Integer YEAR_OF_BIRTH;
    private Integer AGE;
    private Integer STANDARD;
    private String LANGUAGE;
    private Timestamp CREATED_AT;
    private Timestamp UPDATED_AT;

    private java.util.Date date = new java.util.Date();

    private boolean isInserted = false;

    private DataSource dataSource;

    public CreateProfileDto(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void process(Event event) throws SQLException, ParseException {
        Map<String,Object> EKS = (Map<String,Object>) event.getEks();

        if(!isLearnerExist((String) EKS.get("uid"))){
            createLearner(event);
        }

        parseData(EKS);
        saveData();
    }

    private void parseData(Map<String, Object> EKS) throws ParseException {
        UID = (String) EKS.get("uid");
        if(UID == null) throw new ParseException("UID can't be blank",1);

        HANDLE = (String) EKS.get("handle");
        if(HANDLE == null) throw new ParseException("HANDLE can't be blank",2);

        GENDER = (String) EKS.get("gender");
        YEAR_OF_BIRTH = (Integer) getYear(((Double) EKS.get("age")).intValue());
        AGE = getAge(EKS);
        STANDARD = getStandard(EKS);
        LANGUAGE = (String) EKS.get("language");

        CREATED_AT = (Timestamp) new Timestamp(date.getTime());
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
            String query = " insert into profile (uid, handle, year_of_birth, gender, age, standard, language, created_at, updated_at)"
                    + " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString (1, UID);
            preparedStmt.setString (2, HANDLE);

            if(AGE != null) {
                preparedStmt.setInt(3, YEAR_OF_BIRTH);
                preparedStmt.setInt(5, AGE);
            }
            else {
                preparedStmt.setNull(3, java.sql.Types.INTEGER);
                preparedStmt.setNull(5, java.sql.Types.INTEGER);
            }

            preparedStmt.setString(4, GENDER);

            if(STANDARD != null)
                preparedStmt.setInt(6, STANDARD);
            else
                preparedStmt.setNull(6, java.sql.Types.INTEGER);

            preparedStmt.setString(7,LANGUAGE);

            preparedStmt.setTimestamp(8, CREATED_AT);
            preparedStmt.setTimestamp(9, UPDATED_AT);


            int affectedRows = preparedStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Profile failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                this.setIsInserted();
            }
            else {
                throw new SQLException("Creating Profile failed, no ID obtained.");
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

    private void createLearner(Event event) throws SQLException, ParseException {
        CreateLearnerDto learnerDto = new CreateLearnerDto(dataSource);
        learnerDto.process(event);
    }

    public boolean isLearnerExist(String uid) throws SQLException {
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

        } catch (Exception e) {
            System.err.println("Exception: " + e);
            e.printStackTrace(new PrintStream(System.err));
        }
        finally {
            if(preparedStmt!=null)
                preparedStmt.close();
            if(connection!=null)
                connection.close();
            if(resultSet!=null)
                resultSet.close();
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
        return (eid.equals("GE_CREATE_PROFILE"));
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

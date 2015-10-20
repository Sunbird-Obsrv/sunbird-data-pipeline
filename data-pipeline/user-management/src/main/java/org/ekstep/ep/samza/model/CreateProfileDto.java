package org.ekstep.ep.samza.model;

import javax.sql.DataSource;
import java.io.PrintStream;
import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class CreateProfileDto implements IModel{
    public static final String UID = "uid";
    public static final String HANDLE = "handle";
    public static final String GENDER = "gender";
    public static final String LANGUAGE = "language";
    public static final String AGE = "age";
    public static final String STANDARD = "standard";
    private String uid;
    private String handle;
    private String gender;
    private Integer yearOfBirth;
    private Integer age;
    private Integer standard;
    private String language;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private java.util.Date date = new java.util.Date();

    private boolean isInserted = false;

    private DataSource dataSource;

    public CreateProfileDto(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void process(Event event) throws SQLException, ParseException {
        Map<String,Object> EKS = (Map<String,Object>) event.getEks();
        parseData(EKS);

        if(!isLearnerExist((String) EKS.get(UID))){
            createLearner(event);
        }
        saveData();
    }

    private void parseData(Map<String, Object> EKS) throws ParseException {
        uid = (String) EKS.get(UID);
        validateEmptyString(UID,uid);

        handle = (String) EKS.get(HANDLE);
        validateEmptyString(HANDLE,handle);

        gender = (String) EKS.get(GENDER);
        yearOfBirth = (Integer) getYear(((Double) EKS.get(AGE)).intValue());
        age = getAge(EKS);
        standard = getStandard(EKS);
        language = (String) EKS.get(LANGUAGE);

        createdAt = (Timestamp) new Timestamp(date.getTime());
        updatedAt = (Timestamp) new Timestamp(date.getTime());
    }

    private void validateEmptyString(String name,String value) throws ParseException {
        if(value == null || value.isEmpty()) throw new ParseException(String.format("%s can't be blank",name),1);
    }

    private Integer getAge(Map<String, Object> EKS) {
        Integer age = ((Double) EKS.get(AGE)).intValue();
        if(age != -1){
            return age;
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
            String query = " insert into profile (uid, handle, year_of_birth, gender, age, standard, language, created_at, updated_at)"
                    + " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString (1, uid);
            preparedStmt.setString (2, handle);

            if(age != null) {
                preparedStmt.setInt(3, yearOfBirth);
                preparedStmt.setInt(5, age);
            }
            else {
                preparedStmt.setNull(3, java.sql.Types.INTEGER);
                preparedStmt.setNull(5, java.sql.Types.INTEGER);
            }

            preparedStmt.setString(4, gender);

            if(standard != null)
                preparedStmt.setInt(6, standard);
            else
                preparedStmt.setNull(6, java.sql.Types.INTEGER);

            preparedStmt.setString(7, language);

            preparedStmt.setTimestamp(8, createdAt);
            preparedStmt.setTimestamp(9, updatedAt);


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
            String query = String.format("select %s from learner where %s = ?",UID,UID);
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

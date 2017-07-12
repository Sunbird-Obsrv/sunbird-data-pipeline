package org.ekstep.ep.samza.model;

import org.ekstep.ep.samza.logger.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

public class UpdateProfileDto implements IModel {
    static Logger LOGGER = new Logger(UpdateProfileDto.class);
    public static final String UID = "uid";
    public static final String HANDLE = "handle";
    public static final String GENDER = "gender";
    public static final String LANGUAGE = "language";
    public static final String AGE = "age";
    public static final String STANDARD = "standard";
    public static final String DAY = "day";
    public static final String MONTH = "month";
    private final String IS_GROUP_USER = "is_group_user";
    private static final String BOARD = "board";
    private static final String MEDIUM = "medium";
    private String uid;
    private String gender;
    private Integer yearOfBirth;
    private Integer age;
    private Integer standard;
    private String language;
    private String handle;
    private Integer day;
    private Integer month;
    private String board;
    private String medium;
    private Boolean isGroupUser;

    private Timestamp updatedAt;

    private boolean isInserted;
    private DataSource dataSource;
    private String channel;

    public UpdateProfileDto(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void process(Event event) throws SQLException, ParseException {
        Map<String, Object> EKS = event.getEks();
        channel = ((String) event.getMap().get("channel"));

        java.util.Date timeOfEvent = event.getTs();
        parseData(EKS, timeOfEvent);

        if (!isProfileExist((String) EKS.get("uid"), event.id())) {
            createProfile(event);
        }

        saveData(event.id());
    }

    private void parseData(Map<String, Object> EKS, java.util.Date timeOfEvent) throws ParseException {
        uid = (String) EKS.get(UID);
        validateEmptyString(UID, uid);

        handle = (String) EKS.get(HANDLE);
        validateEmptyString(HANDLE, handle);

        gender = (String) EKS.get(GENDER);
        age = getIntegerValueFromDouble(EKS, AGE);
        yearOfBirth = getYear(EKS, timeOfEvent);
        standard = getIntegerValueFromDouble(EKS, STANDARD);
        language = (String) EKS.get(LANGUAGE);
        day = getIntegerValueFromDouble(EKS, DAY);
        month = getIntegerValueFromDouble(EKS, MONTH);
        board = (String) EKS.get(BOARD);
        medium = (String) EKS.get(MEDIUM);
        isGroupUser = getBoolean(EKS, IS_GROUP_USER);

        java.util.Date date = new java.util.Date();
        updatedAt = new Timestamp(date.getTime());
    }

    private Boolean getBoolean(Map<String, Object> EKS, String key) {
        Object value = EKS.get(key);
        Boolean booleanValue = value == null ? false : (Boolean) value;
        return booleanValue;
    }

    private void validateEmptyString(String name, String value) throws ParseException {
        if (value == null || value.isEmpty()) throw new ParseException(String.format("%s can't be blank", name), 1);
    }

    private Integer getIntegerValueFromDouble(Map<String, Object> EKS, String name) {
        if (!EKS.containsKey(name)) {
            return null;
        }
        return ((Double) EKS.get(name)).intValue() != -1 ?
                (Integer) ((Double) EKS.get(name)).intValue()
                : null;
    }

    private void saveData(String eventId) throws SQLException, ParseException {
        PreparedStatement preparedStmt = null;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            String updateQuery = "update profile set year_of_birth = ?, gender = ?, age = ?, standard = ?, language = ?, updated_at = ?, handle = ?, day = ?, month = ?, board = ?, medium = ?, is_group_user = ?"
                    + " where uid = ? and channelid = ?";

            preparedStmt = connection.prepareStatement(updateQuery);

            setIntegerValues(preparedStmt, 1, yearOfBirth);
            preparedStmt.setString(2, gender);
            setIntegerValues(preparedStmt, 3, age);
            setIntegerValues(preparedStmt, 4, standard);
            preparedStmt.setString(5, language);
            preparedStmt.setTimestamp(6, updatedAt);
            preparedStmt.setString(7, handle);
            setIntegerValues(preparedStmt, 8, day);
            setIntegerValues(preparedStmt, 9, month);
            preparedStmt.setString(10, board);
            preparedStmt.setString(11, medium);
            preparedStmt.setBoolean(12, isGroupUser);

            preparedStmt.setString(13, uid);
            preparedStmt.setString(14, channel);

            int affectedRows = preparedStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating Profile failed, no rows affected.");
            } else {
                this.setInserted();
            }

        } catch (Exception e) {
            LOGGER.error(eventId, "EXCEPTION WHEN UPDATING PROFILE", e);
        } finally {
            if (preparedStmt != null)
                preparedStmt.close();
            if (connection != null)
                connection.close();
        }
    }

    private void setIntegerValues(PreparedStatement preparedStmt, int index, Integer value) throws SQLException {
        if (value != null)
            preparedStmt.setInt(index, value);
        else
            preparedStmt.setNull(index, Types.INTEGER);
    }

    private void createProfile(Event event) throws SQLException, ParseException {
        CreateProfileDto profileDto = new CreateProfileDto(dataSource);
        profileDto.process(event);
    }

    public boolean isProfileExist(String uid, String eventId) throws SQLException {
        boolean flag = false;
        PreparedStatement preparedStmt = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            String query = "select uid from profile where uid = ?";
            preparedStmt = connection.prepareStatement(query);
            preparedStmt.setString(1, uid);

            resultSet = preparedStmt.executeQuery();

            if (resultSet.first()) {
                flag = true;
            }

        } catch (Exception e) {
            LOGGER.error(eventId, "EXCEPTION WHEN CHECKING IF PROFILE EXISTS", e);
        } finally {
            if (preparedStmt != null)
                preparedStmt.close();
            if (connection != null)
                connection.close();
        }
        return flag;
    }

    private Integer getYear(Map<String, Object> EKS, java.util.Date timeOfEvent) throws ParseException {
        if (!EKS.containsKey(AGE)) {
            return null;
        }
        Integer ageInEvent = ((Double) EKS.get(AGE)).intValue();
        if (ageInEvent != null && ageInEvent != -1) {
            Calendar timeOfEventFromCalendar = Calendar.getInstance();
            timeOfEventFromCalendar.setTime(timeOfEvent);
            return timeOfEventFromCalendar.get(Calendar.YEAR) - ageInEvent;
        }
        return null;
    }

    @Override
    public void setInserted() {
        this.isInserted = true;
    }

    @Override
    public void setDefault() {
        this.isInserted = false;
    }

    @Override
    public boolean getIsInserted() {
        return this.isInserted;
    }

}

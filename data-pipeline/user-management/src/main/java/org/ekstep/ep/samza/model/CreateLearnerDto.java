package org.ekstep.ep.samza.model;
import org.ekstep.ep.samza.logger.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.util.Map;


public class CreateLearnerDto implements IModel{
    static Logger LOGGER = new Logger(CreateLearnerDto.class);
    private DataSource dataSource;
    private String uid;
    private Timestamp createdAt;

    private boolean isInserted;
    private String channelid;

    public CreateLearnerDto(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void process(Event event) throws ParseException, SQLException {
        Map<String,Object> EKS = event.getEks();

        uid = (String) EKS.get("uid");
        if(uid == null || uid.isEmpty()) throw new ParseException("uid can't be blank",1);

        java.util.Date date = new java.util.Date();
        createdAt = new Timestamp(date.getTime());
        channelid = (String) event.getMap().get("channelid");

        saveData(event.id());
    }

    private void saveData(String eventId) throws SQLException {
        PreparedStatement preparedStmt = null;
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            String query = " insert into learner (uid,created_at,channelid)"
                    + " values (?,?,?)";
            preparedStmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setString(1, uid);
            preparedStmt.setTimestamp(2, createdAt);
            preparedStmt.setString(3,channelid);

            int affectedRows = preparedStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Learner failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                this.setInserted();
            }
            else {
                throw new SQLException("Creating Learner failed, no ID obtained.");
            }

        } catch (Exception e) {
            LOGGER.error(eventId, "EXCEPTION WHEN CREATING LEARNER", e);
        } finally {
            if(preparedStmt!=null)
                preparedStmt.close();
            if(connection!=null)
                connection.close();
        }
    }

    @Override
    public void setInserted(){
        this.isInserted = true;
    }

    @Override
    public void setDefault() {
        this.isInserted = false;
    }

    @Override
    public boolean getIsInserted(){
        return this.isInserted;
    }
}



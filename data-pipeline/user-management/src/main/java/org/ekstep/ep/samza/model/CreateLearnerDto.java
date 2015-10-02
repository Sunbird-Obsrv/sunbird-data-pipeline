package org.ekstep.ep.samza.model;
import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.util.Map;

public class CreateLearnerDto implements IModel{
    private DataSource dataSource;
    private String UID;
    private Timestamp CREATED_AT;

    private java.util.Date date = new java.util.Date();

    private boolean isInserted = false;

    public CreateLearnerDto(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void process(Event event) throws ParseException, SQLException {
        Map<String,Object> EKS = (Map<String,Object>) event.getEks();

        UID = (String) EKS.get("uid");
        if(UID == null) throw new ParseException("UID can't be blank",1);

        CREATED_AT = (Timestamp) new Timestamp(date.getTime());

        saveData();
    }

    @Override
    public boolean canProcessEvent(String eid){
        return (eid.equals("GE_CREATE_USER"));
    }

    @Override
    public void saveData() throws SQLException {
        PreparedStatement preparedStmt = null;
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            String query = " insert into learner (uid,created_at)"
                    + " values (?,?)";
            preparedStmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setString(1, UID);
            preparedStmt.setTimestamp(2, CREATED_AT);

            int affectedRows = preparedStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Learner failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                this.setIsInserted();
            }
            else {
                throw new SQLException("Creating Learner failed, no ID obtained.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(preparedStmt!=null)
                preparedStmt.close();
            if(connection!=null)
                connection.close();
        }
    }

    @Override
    public void setIsInserted(){
        this.isInserted = true;
    };

    @Override
    public boolean getIsInserted(){
        return this.isInserted;
    }
}



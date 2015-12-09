package org.ekstep.ep.samza.model;
import javax.sql.DataSource;
import java.io.PrintStream;
import java.sql.*;
import java.text.ParseException;
import java.util.Map;
import java.util.Date;


public class CreateLearnerDto implements IModel{
    private DataSource dataSource;
    private String uid;
    private Timestamp createdAt;

    private boolean isInserted = false;

    public CreateLearnerDto(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void process(Event event) throws ParseException, SQLException {
        Map<String,Object> EKS = (Map<String,Object>) event.getEks();

        uid = (String) EKS.get("uid");
        if(uid == null || uid.isEmpty()) throw new ParseException("uid can't be blank",1);

        java.util.Date date = new java.util.Date();
        createdAt = new Timestamp(date.getTime());

        saveData();
    }

    private void saveData() throws SQLException {
        PreparedStatement preparedStmt = null;
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            String query = " insert into learner (uid,created_at)"
                    + " values (?,?)";
            preparedStmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setString(1, uid);
            preparedStmt.setTimestamp(2, createdAt);

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

    @Override
    public void setIsInserted(){
        this.isInserted = true;
    };

    @Override
    public boolean getIsInserted(){
        return this.isInserted;
    }
}



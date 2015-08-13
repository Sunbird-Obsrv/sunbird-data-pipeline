package org.ekstep.ep.samza.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ChildDto {
    public static final String NAME = "name";
    public static final String GENDER = "gender";
    public static final String EKSTEP_ID = "ekstep_id";
    public static final String DOB = "dob";
    private String host;
    private String port;
    private String schema;
    private String userName;
    private String password;

    public ChildDto(String host, String port, String schema, String userName, String password){

        this.host = host;
        this.port = port;
        this.schema = schema;
        this.userName = userName;
        this.password = password;
    }

    public void process(Child child) throws SQLException {
        String query = String.format("select * from children where encoded_id = '%s'", child.getUid());
        HashMap<String, Object> childData = new HashMap<String, Object>();
        Statement statement = null;
        Connection connection=null;
        ResultSet resultSet = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = String.format("jdbc:mysql://%s:%s/%s", host,port,schema);
            connection = DriverManager
                    .getConnection(url,userName,password);
            statement = connection.createStatement();

            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                childData.put(NAME, resultSet.getString(NAME));
                childData.put(GENDER, resultSet.getString(GENDER));
                childData.put(EKSTEP_ID, resultSet.getString(EKSTEP_ID));
                childData.put(DOB, resultSet.getTimestamp(DOB));
            }
            child.populate(childData);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if(statement!=null)
                statement.close();
            if(connection!=null)
                connection.close();
            if(resultSet!=null)
                resultSet.close();

        }
    }
}

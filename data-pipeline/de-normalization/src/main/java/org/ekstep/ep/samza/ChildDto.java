package org.ekstep.ep.samza;

import java.sql.*;
import java.util.HashMap;

public class ChildDto {
    public static final String HANDLE = "handle";
    public static final String STANDARD = "standard";
    public static final String GENDER = "gender";
    public static final String YEAR_OF_BIRTH = "year_of_birth";
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

    public Child process(Child child) throws SQLException {
        String query = String.format("select * from profile where uid = '%s'", child.getUid());
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
                childData.put(HANDLE, resultSet.getString(HANDLE));
                childData.put(STANDARD, resultSet.getInt(STANDARD));
                childData.put(GENDER, resultSet.getString(GENDER));
                childData.put(YEAR_OF_BIRTH, resultSet.getInt(YEAR_OF_BIRTH));
            }
            child.populate(childData);
            return child;

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
        return child;
    }
}

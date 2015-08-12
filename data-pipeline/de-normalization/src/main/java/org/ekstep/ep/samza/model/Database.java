package org.ekstep.ep.samza.model;

import java.sql.*;

public class Database {
    private String host;
    private String port;
    private String schema;
    private String userName;
    private String password;

    public Database(String host, String port, String schema, String userName, String password){

        this.host = host;
        this.port = port;
        this.schema = schema;
        this.userName = userName;
        this.password = password;
    }
    public ResultSet get(String query) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = String.format("jdbc:mysql://%s:%s/%s?"
                    + "user=%s&password=%s");
            Connection connect = DriverManager
                    .getConnection(connectionString);
            Statement statement = connect.createStatement();
            return statement.executeQuery(query);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

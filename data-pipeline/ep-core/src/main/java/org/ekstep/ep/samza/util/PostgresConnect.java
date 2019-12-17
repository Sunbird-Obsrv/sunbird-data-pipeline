package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import org.postgresql.jdbc2.optional.ConnectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnect {
    private Config config;
    private Connection connection;
    private String user;
    private String password;
    private String db;
    private String host;
    private Integer port;

    public PostgresConnect(Config config) {
        this.config = config;
        user = config.get("postgres.user");
        password = config.get("postgres.password");
        db = config.get("postgres.db");
        host = config.get("postgres.host","127.0.0.1");
        port = config.getInt("postgres.port", 5432);
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%d/%s",host, port, db),
                    user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Connection resetConnection() throws SQLException, ClassNotFoundException {
        connection.close();
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%d/%s", host, port, db),
                user, password);
        return connection;
    }
}

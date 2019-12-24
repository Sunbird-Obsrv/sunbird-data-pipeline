package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import org.postgresql.ds.PGPoolingDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresConnect {
    private Config config;
    private Connection connection;
    private Statement statement;
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
            PGPoolingDataSource source = new PGPoolingDataSource();
            source.setDatabaseName(db);
            source.setPassword(password);
            source.setPortNumber(port);
            source.setUser(user);
            source.setServerName(host);
            source.setMaxConnections(2);
            connection = source.getConnection();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Statement getStatement() throws Exception {
        try {
            statement = connection.createStatement();
        } catch(SQLException ex) {
            connection = resetConnection();
            statement = connection.createStatement();
        }
        return statement;
    }

    public Connection resetConnection() throws SQLException, ClassNotFoundException {
        connection.close();
        Class.forName("org.postgresql.Driver");
        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDatabaseName(db);
        source.setPassword(password);
        source.setPortNumber(port);
        source.setUser(user);
        source.setServerName(host);
        source.setMaxConnections(2);
        connection = source.getConnection();
        return connection;
    }
}

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
    private Integer maxConnections;
    private PGPoolingDataSource source;

    public PostgresConnect(Config config) throws Exception {
        this.config = config;
        user = config.get("postgres.user");
        password = config.get("sensitive.postgres.password");
        db = config.get("postgres.db");
        host = config.get("postgres.host","127.0.0.1");
        port = config.getInt("postgres.port", 5432);
        maxConnections = config.getInt("postgres.maxConnections", 2);
        buildPoolConfig();
        connection = source.getConnection();
        statement = connection.createStatement();
    }

    public void buildPoolConfig() throws Exception {
        Class.forName("org.postgresql.Driver");
        source = new PGPoolingDataSource();
        source.setServerName(host);
        source.setPortNumber(port);
        source.setUser(user);
        source.setPassword(password);
        source.setDatabaseName(db);
        source.setMaxConnections(maxConnections);
    }

    public Connection getConnection() {
        return this.connection;
    }

    public boolean execute(String query) throws Exception {
        try {
            return statement.execute(query);
        } catch (SQLException ex) {
            resetConnection();
            return statement.execute(query);
        }
    }

    public Connection resetConnection() throws Exception {
        closeConnection();
        buildPoolConfig();
        connection = source.getConnection();
        statement = connection.createStatement();
        return connection;
    }

    public void closeConnection() throws Exception {
        connection.close();
        source.close();
    }
}

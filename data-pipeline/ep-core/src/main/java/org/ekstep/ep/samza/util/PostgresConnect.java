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

    public PostgresConnect(Config config) {
        this.config = config;
        user = config.get("postgres.user");
        password = config.get("postgres.password");
        db = config.get("postgres.db");
        host = config.get("postgres.host","127.0.0.1");
        port = config.getInt("postgres.port", 5432);
        maxConnections = config.getInt("postgres.maxConnections", 2);
        try {
            Class.forName("org.postgresql.Driver");
            PGPoolingDataSource source = new PGPoolingDataSource();
            setConnection(source);
            connection = source.getConnection();
            statement = connection.createStatement();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setConnection(PGPoolingDataSource source) {
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
        return statement.execute(query);
    }

    public Connection resetConnection() throws SQLException, ClassNotFoundException {
        connection.close();
        Class.forName("org.postgresql.Driver");
        PGPoolingDataSource source = new PGPoolingDataSource();
        setConnection(source);
        connection = source.getConnection();
        return connection;
    }
}

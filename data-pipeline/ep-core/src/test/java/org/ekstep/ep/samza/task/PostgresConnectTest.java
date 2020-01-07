package org.ekstep.ep.samza.task;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.util.PostgresConnect;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class PostgresConnectTest {

    private Config configMock;
    private PostgresConnect postgresConnect;
    private Connection connection;
    private Connection resetConnection;

    @Before
    public void setUp() throws Exception {
        configMock = mock(Config.class);

        EmbeddedPostgres.builder().setPort(5430).start();

        stub(configMock.get("postgres.user")).toReturn("postgres");
        stub(configMock.get("postgres.password")).toReturn("postgres");
        stub(configMock.get("postgres.db")).toReturn("postgres");
        stub(configMock.get("postgres.host","127.0.0.1")).toReturn("localhost");
        stub(configMock.getInt("postgres.port", 5432)).toReturn(5430);

        postgresConnect = new PostgresConnect(configMock);
    }

    @Test
    public void shouldVerifyPostgresConnect() throws Exception {
        connection = postgresConnect.getConnection();
        assertNotNull(connection);

        postgresConnect.execute("CREATE TABLE device_table(id text PRIMARY KEY, channel text);");
        postgresConnect.execute("INSERT INTO device_table(id,channel)  VALUES('12345','custchannel');");

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM device_table where id='12345';");
        while(rs.next()) {
            assertEquals("12345", rs.getString("id"));
            assertEquals("custchannel", rs.getString("channel"));
        }

        resetConnection = postgresConnect.resetConnection();
        assertNotNull(resetConnection);

        postgresConnect.closeConnection();
    }

}

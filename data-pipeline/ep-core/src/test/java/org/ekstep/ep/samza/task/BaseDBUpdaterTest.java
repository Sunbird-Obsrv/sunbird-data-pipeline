package org.ekstep.ep.samza.task;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.ekstep.ep.samza.util.BaseDBUpdater;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.mockito.Mockito.*;

public class BaseDBUpdaterTest {

    private BaseDBUpdater baseDBUpdater;
    private CassandraConnect cassandraConnect;

    @Before
    public void setUp() {
        cassandraConnect = mock(CassandraConnect.class);
        baseDBUpdater = new BaseDBUpdater(cassandraConnect);

    }

    @Test(expected = DriverException.class)
    public void shouldHandleCassandraException() throws Exception {

        when(cassandraConnect.find(anyString())).thenThrow(new DriverException("Cassandra Exception"));
        baseDBUpdater.readFromCassandra("sunbird","user","id","id");
    }
}

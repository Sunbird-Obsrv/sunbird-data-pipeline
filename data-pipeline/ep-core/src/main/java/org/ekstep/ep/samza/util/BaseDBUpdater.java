package org.ekstep.ep.samza.util;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.util.CassandraConnect;
import java.util.*;

public class BaseDBUpdater {

    private CassandraConnect cassandraConnect;

    public BaseDBUpdater(CassandraConnect cassandraConnect) {
        this.cassandraConnect = cassandraConnect;
    }

    public void updateToCassandra(String keyspace, String table, Map<String, String> data) {
        Insert query = QueryBuilder.insertInto(keyspace, table)
                .values(new ArrayList<>(data.keySet()), new ArrayList<>(data.values()));
        try {
            cassandraConnect.upsert(query);
        } catch(DriverException ex) {
            cassandraConnect.reconnectCluster();
            cassandraConnect.upsert(query);
        }
    }

    public <T> List<Row> readFromCassandra(String keyspace, String table, String column, T value){
        List<Row> rowSet = null;
        String query = QueryBuilder.select().all()
                .from(keyspace, table)
                .where(QueryBuilder.eq(column, value))
                .toString();
        try {
            rowSet = cassandraConnect.find(query);
        } catch(DriverException ex) {
            cassandraConnect.reconnectCluster();
            rowSet = cassandraConnect.find(query);
        }
        return rowSet;
    }
}

//package org.ekstep.ep.samza;
//
//import org.apache.samza.config.Config;
//import org.ekstep.ep.samza.util.*;
//import org.junit.Before;
//import static org.mockito.Mockito.*;
//
//public class UserLocationCacheTest {
//
//    private UserLocationCache userLocationCache;
//    private RedisConnect redisConnectMock;
//    private CassandraConnect cassandraConnectMock;
//
//    @SuppressWarnings("unchecked")
//    @Before
//    public void setUp() {
//        Config config = mock(Config.class);
//        this.redisConnectMock = mock(RedisConnect.class);
//        this.cassandraConnectMock = mock(CassandraConnect.class);
//        this.userLocationCache = new UserLocationCache(config, redisConnectMock, cassandraConnectMock);
//    }
//}

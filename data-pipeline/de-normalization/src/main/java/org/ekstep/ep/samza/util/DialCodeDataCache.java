package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;

import java.util.ArrayList;
import java.util.List;

public class DialCodeDataCache extends DataCache {

    public DialCodeDataCache(Config config, RedisConnect redisConnect, JobMetrics metrics) {

        List<String> defaultList = new ArrayList<>();
        defaultList.add("identifier");
        defaultList.add("channel");
        defaultList.add("batchcode");
        defaultList.add("publisher");
        defaultList.add("generated_on");
        defaultList.add("published_on");
        defaultList.add("status");
        this.redisConnect = redisConnect;
        this.redisConnection = this.redisConnect.getConnection();
        this.redisConnection.select(config.getInt("redis.dialcodeDB.index", 3));
        // this.redisDBIndex = config.getInt("redis.dialcodeDB.index", 3);
        this.fieldsList = config.getList("dialcode.metadata.fields", defaultList);
        this.metrics = metrics;
    }

}

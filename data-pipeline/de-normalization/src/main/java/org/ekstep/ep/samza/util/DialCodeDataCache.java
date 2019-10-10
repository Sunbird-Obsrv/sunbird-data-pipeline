package org.ekstep.ep.samza.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialCodeDataCache extends DataCache {



    public DialCodeDataCache(Config config, JobMetrics metrics) {

        super(config.getList("dialcode.metadata.fields", Arrays.asList("identifier", "channel", "batchcode", "publisher", "generated_on", "published_on", "status")));
        this.databaseIndex = config.getInt("redis.dialcodeDB.index", 6);
        this.redisConnect = new RedisConnect(config);
        this.redisConnection = this.redisConnect.getConnection(databaseIndex);
        this.metrics = metrics;
    }

}

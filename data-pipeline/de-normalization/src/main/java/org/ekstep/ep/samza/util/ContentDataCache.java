package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContentDataCache extends DataCache {

    public ContentDataCache(Config config, JobMetrics metrics) {

        super(config.getList("content.metadata.fields", Arrays.asList("name", "objectType", "contentType", "mediaType", "language", "medium", "mimeType", "framework", "board", "status", "pkgVersion", "lastSubmittedOn", "lastUpdatedOn", "lastPublishedOn")));
        this.databaseIndex = config.getInt("redis.contentDB.index", 5);
        this.redisConnect = new RedisConnect(config);
        this.redisConnection = this.redisConnect.getConnection(databaseIndex);
        this.metrics = metrics;
    }
}

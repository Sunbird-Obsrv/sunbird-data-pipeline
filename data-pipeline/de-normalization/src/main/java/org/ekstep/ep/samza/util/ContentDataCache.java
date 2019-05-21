package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;

import java.util.ArrayList;
import java.util.List;

public class ContentDataCache extends DataCache {

    public ContentDataCache(Config config, RedisConnect redisConnect, JobMetrics metrics) {

        List<String> defaultList = new ArrayList<>();
        defaultList.add("name");
        defaultList.add("objectType");
        defaultList.add("contentType");
        defaultList.add("mediaType");
        defaultList.add("language");
        defaultList.add("medium");
        defaultList.add("mimeType");
        defaultList.add("framework");
        defaultList.add("board");
        defaultList.add("status");
        defaultList.add("pkgVersion");
        defaultList.add("lastSubmittedOn");
        defaultList.add("lastUpdatedOn");
        defaultList.add("lastPublishedOn");
        this.databaseIndex = config.getInt("redis.contentDB.index", 2);
        this.redisConnect = redisConnect;
        this.redisConnection = this.redisConnect.getConnection(databaseIndex);
        this.fieldsList = config.getList("content.metadata.fields", defaultList);
        this.metrics = metrics;

    }
}

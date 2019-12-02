package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.List;

public class ContentCacheConfig {

    private final String JOB_NAME = "ContentCacheUpdater";

    private int contentStoreDb;
    private int dialCodeStoreDb;
    private List<String> contentModelListTypeFields;
    private List<String> dateFields;

    public ContentCacheConfig(Config config) {
        this.contentStoreDb = config.getInt("redis.database.contentStore.id", 5);
        this.dialCodeStoreDb = config.getInt("redis.database.dialCodeStore.id", 6);
        this.contentModelListTypeFields = config.getList("contentModel.fields.listType", new ArrayList<>());
        this.dateFields = config.getList("date.fields.listType", new ArrayList<>());
    }

    public int getContentStoreDb() { return this.contentStoreDb; }

    public int getdialCodeStoreDb() { return this.dialCodeStoreDb; }

    public List<String> getContentModelListTypeFields() { return this.contentModelListTypeFields; }

    public List<String> getDateFields() { return this.dateFields; }

    public String JOB_NAME() { return JOB_NAME; }
}

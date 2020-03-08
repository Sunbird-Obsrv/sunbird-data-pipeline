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
    private String apiHost;
    private String dialCodeAPiEndPoint;
    private String authorizationKey = "";

    public ContentCacheConfig(Config config) {
        this.contentStoreDb = config.getInt("redis.database.contentStore.id", 5);
        this.dialCodeStoreDb = config.getInt("redis.database.dialCodeStore.id", 6);
        this.contentModelListTypeFields = config.getList("contentModel.fields.listType", new ArrayList<>());
        this.dateFields = config.getList("date.fields.listType", new ArrayList<>());
        apiHost = config.get("dialcode.api.host","https://localhost");
        dialCodeAPiEndPoint = config.get("dialcode.api.endpoint","/api/dialcode/v3/read/");
        authorizationKey = config.get("dialcode.api.authorizationkey","");
    }

    public int getContentStoreDb() { return this.contentStoreDb; }

    public int getdialCodeStoreDb() { return this.dialCodeStoreDb; }

    public List<String> getContentModelListTypeFields() { return this.contentModelListTypeFields; }

    public List<String> getDateFields() { return this.dateFields; }

    public String JOB_NAME() { return JOB_NAME; }

    public String getDialCodeAPIUrl(){
        return apiHost.concat(dialCodeAPiEndPoint);
    }
    public String getAuthorizationKey(){
        return this.authorizationKey;
    }
}

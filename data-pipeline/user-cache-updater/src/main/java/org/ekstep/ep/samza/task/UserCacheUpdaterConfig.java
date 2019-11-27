package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

import java.util.Arrays;
import java.util.List;

public class UserCacheUpdaterConfig {

    private final String JOB_NAME = "UserCacheUpdater";

    private String userSignInTypeDefault;
    private String userLoginInTypeDefault;
    private List<String> userSelfSignedInTypeList;
    private List<String> userValidatedTypeList;
    private String userSelfSignedKey;
    private String userValidatedKey;
    private String cassandra_db;
    private String cassandra_user_table;
    private String cassandra_location_table;
    private int userStoreDb;

    public UserCacheUpdaterConfig(Config config) {
        this.userSignInTypeDefault = config.get("user.signin.type.default", "Anonymous");
        this.userLoginInTypeDefault = config.get("user.login.type.default", "NA");
        this.userSelfSignedInTypeList = config.getList("user.selfsignedin.typeList", Arrays.asList("google", "self"));
        this.userValidatedTypeList = config.getList("user.validated.typeList", Arrays.asList("sso"));
        this.userSelfSignedKey = config.get("user.self-siginin.key", "Self-Signed-In");
        this.userValidatedKey = config.get("user.valid.key", "Validated");
        this.cassandra_db = config.get("middleware.cassandra.keyspace", "sunbird");
        this.cassandra_user_table = config.get("middleware.cassandra.user_table", "user");
        this.cassandra_location_table = config.get("middleware.cassandra.location_table", "location");
        this.userStoreDb = config.getInt("redis.database.userStore.id", 4);
    }

    public String getUserSignInTypeDefault() { return userSignInTypeDefault; }

    public String getUserLoginInTypeDefault() { return userLoginInTypeDefault; }

    public String getUserSelfSignedKey() { return userSelfSignedKey; }

    public String getUserValidatedKey() { return userValidatedKey; }

    public List<String> getUserSelfSignedInTypeList() { return userSelfSignedInTypeList; }

    public List<String> getUserValidatedTypeList() { return userValidatedTypeList; }

    public String cassandra_db() { return cassandra_db; }

    public String cassandra_user_table() { return cassandra_user_table; }

    public String cassandra_location_table() { return cassandra_location_table; }

    public String jobName() { return JOB_NAME; }

    public int userStoreDb() { return userStoreDb; }

}

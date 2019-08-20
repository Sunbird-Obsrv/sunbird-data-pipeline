package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import java.util.ArrayList;
import java.util.List;

public class UserDataCache extends DataCache {

    public UserDataCache(Config config, RedisConnect redisConnect) {

        List defaultList = new ArrayList<String>();
        defaultList.add("usertype");
        defaultList.add("grade");
        defaultList.add("language");
        defaultList.add("subject");
        this.redisDBIndex = config.getInt("redis.userDB.index", 1);
        this.redisConnect = redisConnect;
        this.fieldsList = config.getList("user.metadata.fields", defaultList);
    }
}

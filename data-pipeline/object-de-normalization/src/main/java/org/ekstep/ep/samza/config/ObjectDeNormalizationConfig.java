package org.ekstep.ep.samza.config;


import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.task.TaskContext;
import org.ekstep.ep.samza.logger.Logger;

import java.io.FileReader;
import java.util.List;

import static java.util.Arrays.asList;

public class ObjectDeNormalizationConfig {
    public static final String DEFAULT_BACKOFF_BASE = "10";
    public static final String DEFAULT_BACKOFF_LIMIT = "4";
    public static final String DEFAULT_BACKOFF_LIMIT_ENABLE = "true";
    static Logger LOGGER = new Logger(ObjectDeNormalizationConfig.class);
    private final int retryBackoffBase;
    private final int retryBackoffLimit;
    private final Boolean retryBackoffLimitEnable;
    private String successTopic;
    private String retryTopic;
    private String failedTopic;
    private ObjectDenormalizationAdditionalConfig additionalConfig;
    private String objectServiceEndPoint;
    private List<String> fieldsToDenormalize;
    private KeyValueStore<String, Object> retryStore;


    public ObjectDeNormalizationConfig(Config config, TaskContext context) {
        successTopic = config.get("output.success.topic.name", "telemetry.objects.de_normalized");
        retryTopic = config.get("output.retry.topic.name", "telemetry.objects.de_normalized.retry");
        failedTopic = config.get("output.failed.topic.name", "telemetry.objects.de_normalized.fail");
        objectServiceEndPoint = config.get("object.service.endpoint", "http://localhost:3003");
        retryStore = (KeyValueStore<String, Object>) context.getStore("retry");
        retryBackoffBase = Integer.parseInt(config.get("retry.backoff.base", DEFAULT_BACKOFF_BASE));
        retryBackoffLimit = Integer.parseInt(config.get("retry.backoff.limit", DEFAULT_BACKOFF_LIMIT));
        retryBackoffLimitEnable = Boolean.parseBoolean(config.get("retry.backoff.limit.enable", DEFAULT_BACKOFF_LIMIT_ENABLE));
        initFieldsToDenormalize(config);
        initAdditionalConfig(config);
    }

    private void initFieldsToDenormalize(Config config) {
        String fieldsToDenormalize = config
                .get("fields.to.denormalize", "id,type,subtype,parentid,parenttype,code,name")
                .replace(" ", "");
        this.fieldsToDenormalize = asList(fieldsToDenormalize.split(","));
    }

    private void initAdditionalConfig(Config config) {
        String additionalConfigFile = config.get("denorm.config.file", "/etc/samza-jobs/object-denormalization-additional-config.json");
        try {
            String additionalConfigJson = IOUtils.toString(new FileReader(additionalConfigFile));
            additionalConfig = new Gson().fromJson(additionalConfigJson, ObjectDenormalizationAdditionalConfig.class);
            LOGGER.info(null, "OBJECT DENORMALIZATION ADDITIONAL CONFIG: {}", additionalConfig);
        } catch (Exception e) {
            LOGGER.error(null, "UNABLE TO LOAD OBJECT DENORMALIZATION ADDITIONAL CONFIG JSON FILE", e);
        }
    }

    public String successTopic() {
        return successTopic;
    }

    public String retryTopic() {
        return retryTopic;
    }

    public KeyValueStore<String, Object> retryStore() {
        return retryStore;
    }


    public String failedTopic() {
        return failedTopic;
    }

    public ObjectDenormalizationAdditionalConfig additionalConfig() {
        return additionalConfig;
    }

    public String objectServiceEndPoint() {
        return objectServiceEndPoint;
    }

    public List<String> fieldsToDenormalize() {
        return fieldsToDenormalize;
    }

    public int retryBackoffBase() {
        return retryBackoffBase;
    }

    public int retryBackoffLimit() {
        return retryBackoffLimit;
    }

    public Boolean retryBackoffLimitEnable() {
        return retryBackoffLimitEnable;
    }
}
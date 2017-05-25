package org.ekstep.ep.samza.task;


import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.config.ObjectDenormalizationAdditionalConfig;
import org.ekstep.ep.samza.logger.Logger;

import java.io.FileReader;

public class ObjectDeNormalizationConfig {
    static Logger LOGGER = new Logger(ObjectDeNormalizationConfig.class);

    private String successTopic;
    private String failedTopic;
    private ObjectDenormalizationAdditionalConfig additionalConfig;
    private String objectServiceEndPoint;

    public ObjectDeNormalizationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.objects.de_normalized");
        failedTopic = config.get("output.failed.topic.name", "telemetry.objects.de_normalized.fail");
        objectServiceEndPoint = config.get("object.service.endpoint", "http://localhost:3003");
        String additionalConfigFile = config.get("denorm.config.file", "/etc/samza-jobs/object-denormalization-additional-config.json");
        try {
            String additionalConfigJson = IOUtils.toString(new FileReader(additionalConfigFile));
            additionalConfig = new Gson().fromJson(additionalConfigJson, ObjectDenormalizationAdditionalConfig.class);
            LOGGER.info(null, "OBJECT DENORMALIZATION ADDITIONAL CONFIG: {}", additionalConfig);
        } catch (Exception e) {
            LOGGER.error(null, "UNABLE TO LOAD OBJECT DENORMALIZATION ADDITIONAL CONFIG JSON FILE", e);
            e.printStackTrace();
        }
    }

    public String successTopic() {
        return successTopic;
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
}
package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.config.DataDenormalizationConfig;
import org.ekstep.ep.samza.config.EventDenormalizationConfig;
import org.ekstep.ep.samza.config.ObjectDenormalizationAdditionalConfig;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.object.dto.GetObjectResponse;
import org.ekstep.ep.samza.object.service.ObjectService;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.task.ObjectDeNormalizationConfig;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSink;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSource;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;

public class ObjectDeNormalizationService {
    static Logger LOGGER = new Logger(ObjectDeNormalizationService.class);
    private final ObjectDeNormalizationConfig config;
    private final ObjectDenormalizationAdditionalConfig additionalConfig;
    private final ObjectService objectService;
    private Gson gson = new Gson();

    public ObjectDeNormalizationService(ObjectDeNormalizationConfig config,
                                        ObjectDenormalizationAdditionalConfig additionalConfig, ObjectService objectService) {
        this.config = config;
        this.additionalConfig = additionalConfig;
        this.objectService = objectService;
    }

    public void process(ObjectDeNormalizationSource source, ObjectDeNormalizationSink sink) {
        Event event = source.getEvent();

        for (EventDenormalizationConfig config : additionalConfig.eventConfigs()) {
            Pattern compile = Pattern.compile(config.eidPattern());
            if (!compile.matcher(event.eid()).matches()) {
                continue;
            }

            for (DataDenormalizationConfig dataDenormalizationConfig : config.denormalizationConfigs()) {
                NullableValue<String> objectId = event.read(dataDenormalizationConfig.idFieldPath());
                if (objectId.isNull()) {
                    continue;
                }
                GetObjectResponse getObjectResponse = objectService.get(objectId.value());
                if (!getObjectResponse.successful()) {
                    //#TODO HANDLE ERROR
                } else {
                    event.update(dataDenormalizationConfig.denormalizedFieldPath(), denormalizedData(event, getObjectResponse.result()));
                }
            }
        }

        try {
            LOGGER.info(event.id(), "PASSING EVENT THROUGH");
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO FAILED TOPIC. EVENT: " + event, e);
            sink.toSuccessTopic(event);
            sink.toFailedTopic(event);
            e.printStackTrace();
        }
    }

    public Map<String, String> denormalizedData(Event event, GetObjectResponse.Result result) {
        HashMap<String, String> denormalizedData = new HashMap<String, String>();
        denormalizedData.put("id", String.valueOf(result.id()));
        denormalizedData.put("type", result.type());
        denormalizedData.put("subtype", result.subtype());
        denormalizedData.put("parentid", result.parentid());
        denormalizedData.put("parenttype", result.parenttype());
        denormalizedData.put("code", result.code());
        denormalizedData.put("name", result.name());
        Map<String, String> details = null;
        try {
            details = gson.fromJson(result.details(), new TypeToken<Map<String, String>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            LOGGER.error(event.id(),
                    format("UNABLE TO PARSE DETAILS INTO MAP<STRING, STRING>. EVENT: {0}, DETAILS: {1}",
                            event, result.details()));
            e.printStackTrace();
        }
        if (details != null) {
            denormalizedData.putAll(details);
        }
        return denormalizedData;
    }

}

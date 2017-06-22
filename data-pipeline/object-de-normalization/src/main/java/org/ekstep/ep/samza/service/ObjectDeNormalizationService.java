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
import org.ekstep.ep.samza.config.ObjectDeNormalizationConfig;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSink;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSource;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public class ObjectDeNormalizationService {
    static Logger LOGGER = new Logger(ObjectDeNormalizationService.class);
    private final List<String> fieldsToDenormalize;
    private ObjectDeNormalizationConfig config;
    private final ObjectDenormalizationAdditionalConfig additionalConfig;
    private final ObjectService objectService;
    private Gson gson = new Gson();

    public ObjectDeNormalizationService(ObjectDeNormalizationConfig config,
                                        ObjectDenormalizationAdditionalConfig additionalConfig,
                                        ObjectService objectService) {
        this.config = config;
        this.additionalConfig = additionalConfig;
        this.objectService = objectService;
        this.fieldsToDenormalize = config.fieldsToDenormalize();
    }

    public void process(ObjectDeNormalizationSource source, ObjectDeNormalizationSink sink) {
        Event event = source.getEvent(config);
        LOGGER.info(event.id(), "EVENT: {}", event.map());
        try {
            List<EventDenormalizationConfig> eventConfigs = additionalConfig.filter(event);
            for (EventDenormalizationConfig config : eventConfigs) {
                for (DataDenormalizationConfig dataDenormalizationConfig : config.denormalizationConfigs()) {
                    NullableValue<String> objectId = event.read(dataDenormalizationConfig.idFieldPath());
                    if (objectId.isNull()) {
                        continue;
                    }
                    event.setDeNormalizationId(objectId.value());
                    if(!event.canDeNormalise())
                        continue;
                    if(event.shouldBackOff()){
                        event.addLastSkippedAt(DateTime.now());
                        sink.toRetryTopic(event);
                        return;
                    }
                    deNormalise(event, dataDenormalizationConfig, objectId);
                }
            }
            event.flowIn(sink);
        } catch (NullPointerException e){
            LOGGER.error(event.id(), "NULL EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO FAILED TOPIC. EVENT: " + event, e);
            e.printStackTrace();
            sink.toSuccessTopic(event);
            sink.toFailedTopic(event);
        }catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. SENDING EVENT TO RETRY AND ADDING IT TO FAILED TOPIC. EVENT: " + event, e);
            e.printStackTrace();
            event.markRetry(e.toString(),e.getMessage());
            sink.toRetryTopic(event);
            sink.toFailedTopic(event);
        }
    }

    private void deNormalise(Event event, DataDenormalizationConfig dataDenormalizationConfig, NullableValue<String> objectId) throws IOException {
        GetObjectResponse getObjectResponse = objectService.get(objectId.value());
        if (!getObjectResponse.successful()) {
            LOGGER.error(event.id(),
                    format("ERROR WHEN GETTING OBJECT DATA. EVENT: {0}, RESPONSE: {1}",
                            event, getObjectResponse));
            event.markRetry(getObjectResponse.params().get("err"), getObjectResponse.params().get("errmsg"));
        } else {
            event.markProcessed();
            event.update(
                    dataDenormalizationConfig.denormalizedFieldPath(),
                    getDenormalizedData(event, getObjectResponse.result()));
        }
    }

    private HashMap<String, Object> getDenormalizedData(Event event, Map<String, Object> result) {
        HashMap<String, Object> denormalizedData = new HashMap<String, Object>();

        for (String field : fieldsToDenormalize) {
            denormalizedData.put(field, String.valueOf(result.get(field)));
        }

        Map<String, String> detailsMap = getDetailsMap(event, result);
        if (detailsMap != null) {
            denormalizedData.putAll(detailsMap);
        }
        return denormalizedData;
    }

    private Map<String, String> getDetailsMap(Event event, Map<String, Object> result) {
        Map<String, String> details = new HashMap<String, String>();
        try {
            details = gson.fromJson((String) result.get("details"), new TypeToken<Map<String, Object>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            LOGGER.error(event.id(),
                    format("UNABLE TO PARSE DETAILS INTO MAP<STRING, STRING>. EVENT: {0}, DETAILS: {1}",
                            event, result.get("details")));
            e.printStackTrace();
        }
        return details;
    }
}

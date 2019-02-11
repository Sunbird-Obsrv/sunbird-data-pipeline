package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.DialCodeDataCache;

import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public class DialcodeDataUpdater implements IEventUpdater {

    static Logger LOGGER = new Logger(DialcodeDataUpdater.class);
    private DialCodeDataCache dialcodeCache;

    DialcodeDataUpdater(DialCodeDataCache dialcodeCache) {
        this.dialcodeCache = dialcodeCache;
    }

    public Event update(Event event) {

        List<Map> dailcodeData;
        try {
            List<String> dialcodes = event.dialCode();
            if (dialcodes != null && !dialcodes.isEmpty()) {
                dailcodeData = dialcodeCache.getDataForDialCodes(dialcodes);
                if (dailcodeData != null && !dailcodeData.isEmpty()) {
                    event.addDialCodeData(dailcodeData);
                }
                else {
                    event.setFlag(DeNormalizationConfig.getDialCodeLocationJobFlag(), false);
                }
            }
            return event;
        } catch(Exception ex) {
            LOGGER.error(null,
                    format("EXCEPTION. EVENT: {0}, EXCEPTION:",
                            event),
                    ex);
            return event;
        }
    }
}

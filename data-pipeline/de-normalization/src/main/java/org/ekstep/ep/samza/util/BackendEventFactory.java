package org.ekstep.ep.samza.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackendEventFactory {

        static Logger LOGGER = LoggerFactory.getLogger(BackendEventFactory.class);
        private static List<String> eventsToProcess;

        public BackendEventFactory(List<String> eventsToProcess) {
            this.eventsToProcess = eventsToProcess;
        }

        public boolean shouldProcess(String eventID) {
            for (String eventToSkip : eventsToProcess) {
                Pattern p = Pattern.compile(eventToSkip);
                Matcher m = p.matcher(eventID);
                if (m.matches()) {
                    LOGGER.info(m.toString(), "FOUND BACKEND EVENT");
                    return true;
                }
            }
            return false;
        }

    public void addBackendEventType(Map<String,Object> map) {
        LOGGER.info("ADDING BACKEND EVENT TYPE");
        map.put("backend",true);
    }
}

package org.ekstep.ep.samza.cleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.text.MessageFormat.format;

public class ChildDataCleaner implements Cleaner {
    private static final String TAG = ChildDataCleaner.class.getSimpleName();
    static Logger LOGGER = LoggerFactory.getLogger(ChildDataCleaner.class);

    @Override
    public void process(Map<String, Object> map) {
        removeUdata(map);
        removeEdata(map);
    }

    private void removeEdata(Map<String, Object> map) {
        Map<String, Object> eks = (Map<String, Object>) ((Map<String, Object>) map.get("edata")).get("eks");
        if (eks == null) {
            return;
        }
        eks.remove("day");
        eks.remove("gender");
        eks.remove("handle");
        eks.remove("is_group_user");
        eks.remove("loc");
        eks.remove("month");

        LOGGER.debug(format("{0} CLEANED UDATA {1}", TAG , map));
    }

    private void removeUdata(Map<String, Object> map) {
        Map<String, Object> udata = (Map<String, Object>) map.get("udata");
        if (udata == null) {
            return;
        }
        udata.remove("is_group_user");
        udata.remove("gender");
        udata.remove("handle");

        LOGGER.debug(format("{0} CLEANED EKS {1}", TAG, map));
    }
}

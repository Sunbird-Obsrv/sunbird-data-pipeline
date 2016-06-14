package org.ekstep.ep.samza.cleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

import static java.text.MessageFormat.format;

public class MetadataCleaner implements Cleaner {
    private static final String TAG = MetadataCleaner.class.getSimpleName();
    static Logger LOGGER = LoggerFactory.getLogger(MetadataCleaner.class);

    @Override
    public void clean(Map<String, Object> map) {
        map.remove("metadata");
        map.remove("flags");
        map.remove("ready_to_index");
        map.remove("key");
        map.remove("type");

        Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, Object> entry = itr.next();
            if(entry.getKey().startsWith("@"))
            {
                itr.remove();
            }
        }

        LOGGER.debug(format("{0} CLEANED METADATA & FLAGS {1}", TAG , map));
    }
}
